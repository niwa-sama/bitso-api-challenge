/*
 * Classname: Client
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 20/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sonar.traiding.challenge.core.activemq.Broker;

/**
 * Wrapper to start, stop, send message and receive message from Bitso WebSocket
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 20/10/2017
 */
@WebSocket
public class BitsoWebSocketClient {
	public static final String BITSO_WEBSOCKET_URL = "wss://ws.bitso.com";
	private static final String BITSO_TRADES_SUBSCRIPTION = "{ \"action\": \"subscribe\", \"book\": \"btc_mxn\", \"type\": \"trades\" }";
	private static final String BITSO_DIFF_ORDERS_SUBSCRIPTION = "{ \"action\": \"subscribe\", \"book\": \"btc_mxn\", \"type\": \"diff-orders\" }";
	private static final String BITSO_ORDERS_SUBSCRIPTION = "{ \"action\": \"subscribe\", \"book\": \"btc_mxn\", \"type\": \"orders\" }";
	
	private static BitsoWebSocketClient instance;
	
	private WebSocketClient wsClient;
	private Session session;
	private Runnable connectListener;
	private JSONParser jp = new JSONParser();
	
	public static synchronized BitsoWebSocketClient getInstance() {
		if(instance == null)
			instance = new BitsoWebSocketClient();
		return instance;
	}
	
	private BitsoWebSocketClient() {
	}
	
	public void startWebSocket() {
		try {
			System.out.println("Starting WebSocket client...");
			wsClient = new WebSocketClient();
			wsClient.start();
			URI uri = URI.create(BITSO_WEBSOCKET_URL);
			wsClient.connect(this, uri);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void stopWebSocket() {
		try {
			wsClient.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void registerConnectListener(Runnable listener) {
		this.connectListener = listener;
	}

	public void registerTradesListener(Consumer<String> listener) {
		Broker.createConsumer(Broker.BitsoQueue.TRADES_QUEUE, listener);
	}
	
	public void registerDiffOrdersListener(Consumer<String> listener) {
		Broker.createConsumer(Broker.BitsoQueue.DIFF_ORDERS_QUEUE, listener);
	}

	public void registerOrdersListener(Consumer<String> listener) {
		Broker.createConsumer(Broker.BitsoQueue.ORDERS_QUEUE, listener);
	}

	public void subscribeToTrades() {
		try {
			session.getRemote().sendString(BITSO_TRADES_SUBSCRIPTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void subscribeToDiffOrders() {
		try {
			session.getRemote().sendString(BITSO_DIFF_ORDERS_SUBSCRIPTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void subscribeToOrders() {
		try {
			session.getRemote().sendString(BITSO_ORDERS_SUBSCRIPTION);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@OnWebSocketConnect
	public void onWebSocketConnect(Session session) {
		System.out.println("Connected to Bitso WebSocket Server");
		this.session = session;
		connectListener.run();
	}
	
	@OnWebSocketClose
	public void onWebSocketClose(int statusCode, String reason) {
		System.out.println("Disconnected to Bitso WebSocket Server");
	}
	
	@OnWebSocketMessage
	public void onWebSocketMessage(Session session, String text) {
//		System.out.println("Message received!!!\n" + text);
		try {
			JSONObject json = (JSONObject) jp.parse(text);
			if(json.get("payload") != null) {
				switch(((String) json.get("type")).toLowerCase()) {
					case "trades":
						Broker.sendMessage(Broker.BitsoQueue.TRADES_QUEUE, text);
						break;
					case "diff-orders":
						Broker.sendMessage(Broker.BitsoQueue.DIFF_ORDERS_QUEUE, text);
						break;
					case "orders":
						Broker.sendMessage(Broker.BitsoQueue.ORDERS_QUEUE, text);
						break;
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
