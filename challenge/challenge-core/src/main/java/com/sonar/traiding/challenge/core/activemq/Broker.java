/*
 * Classname: Broker
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 23/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.activemq;

import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.command.ActiveMQQueue;

/**
 * Wrapper to start, stop, send message and create consumers to ActiveMQBroker Service
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 23/10/2017
 */
public final class Broker {
	private static BrokerService broker;
	private static Connection connection;
	private static Session session;
	private static boolean started = false;
	
	public enum BitsoQueue {
		TRADES_QUEUE("trades.queue"), DIFF_ORDERS_QUEUE("diff.orders.queue"), ORDERS_QUEUE("orders.queue");
		
		private String name;
		
		private BitsoQueue(String name) {
			this.name = name;
		}
	}
	
	private Broker() {
		
	}
	
	public static synchronized void start() {
		if(!started) {
			try {
				broker = new BrokerService();
				broker.setBrokerName("bitso-trades");
				broker.setStartAsync(false);
				broker.setUseShutdownHook(false);
				broker.setPersistent(false);
				broker.start();
				ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("vm://bitso-trades");
				connection = cf.createConnection();
				connection.start();
				session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
				started = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void stop() {
		if(started) {
			try {
				broker.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void sendMessage(BitsoQueue queue, String message) {
		if(!started)
			throw new RuntimeException("Local Bitso broker hasn't been initilized.");
		try {
			Destination d = new ActiveMQQueue(queue.name);
			MessageProducer mp = session.createProducer(d);
			TextMessage t = session.createTextMessage();
			t.setText(message);
			mp.send(t);
			mp.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void createConsumer(BitsoQueue queue, Consumer<String> consumer) {
		if(!started)
			throw new RuntimeException("Local Bitso broker hasn't been initilized.");
		try {
			Destination d = new ActiveMQQueue(queue.name + "?consumer.prefetchSize=1");
			MessageConsumer c = session.createConsumer(d);
			c.setMessageListener(new Litener(consumer));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private static class Litener implements MessageListener {
		private Consumer<String> consumer;
		
		public Litener(Consumer<String> consumer) {
			this.consumer = consumer;
		}
		
		@Override
		public void onMessage(Message message) {
			try {
				TextMessage tm = (TextMessage) message;
				consumer.accept(tm.getText());
				message.acknowledge();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
