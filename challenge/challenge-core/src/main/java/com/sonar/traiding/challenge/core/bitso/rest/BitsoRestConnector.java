/*
 * Classname: BitsoRestConnector
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 09/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Utilities to start, stop and send request to Bitso REST Service
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 09/10/2017
 */
public final class BitsoRestConnector {
	/** Logger object */
	//private static final Logger LOG = LoggerFactory.getLogger(BitsoRestConnector.class);
	public static final String BITSO_REST_URL = "https://api.bitso.com";
	private static final String URL_FORMAT = "((?<protocol>http(s)?)://)?(?<domain>[a-zA-Z0-9\\-]{1,63}(\\.[a-zA-Z0-9\\-]{1,63})*)(:(?<port>[0-9]{1,5}))?";
	
	private static BitsoRestConnector instance;
	
	private PoolingHttpClientConnectionManager pcm;
	private CloseableHttpClient client;
	private String bitsoURL;
	private Pattern pattern;

	public static synchronized BitsoRestConnector getInstance() {
		if (instance == null)
			instance = new BitsoRestConnector();
		return instance;
	}
	
	private BitsoRestConnector() {
		createHTTPPool();
		try {
			pattern = Pattern.compile(URL_FORMAT);
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public void configure(String bitsoURL) throws Exception {
		if (this.bitsoURL != null)
			throw new Exception("Connector already configured.");
		if (bitsoURL == null || bitsoURL.trim().isEmpty())
			throw new InvalidParameterException("Parameter must be not null and not empty");
		this.bitsoURL = bitsoURL;
		Matcher m = pattern.matcher(bitsoURL);
		if(m.matches()) {
//			String protocol = m.group("protocol");
			String domain = m.group("domain");
			String sPort = m.group("port");
			int port = 80;
			if(sPort != null) {
				port = Integer.parseInt(sPort);
			}
			HttpHost host = new HttpHost(domain, port);
			pcm.setMaxPerRoute(new HttpRoute(host), 10);
			client = HttpClients.custom()
					.setConnectionManager(pcm)
//					.setSSLHostnameVerifier((h, s) -> true)
					.setKeepAliveStrategy((r, c) -> 3_600_00)
					.build();
		} else
			throw new InvalidParameterException("Parameter must be a valid url domain");
	}
	
	public void stop() {
		try {
			this.client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String invoke(String path) {
		final String url = bitsoURL.concat(path);
		boolean success = false;
		while(!success) {
			try {
				URIBuilder ub = new URIBuilder(url);
				HttpGet get = new HttpGet(ub.build());
				final RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(400).setConnectTimeout(1000).build();
				get.setConfig(rc);
				CloseableHttpResponse r = client.execute(get, HttpClientContext.create());
				HttpEntity entity = r.getEntity();
				InputStream is = entity.getContent();
				int data;
				ByteArrayOutputStream ba = new ByteArrayOutputStream();
				while((data = is.read()) != -1)
					ba.write(data);
				r.close();
				return new String(ba.toByteArray());
			} catch (SocketTimeoutException e) {
				System.out.println("[WARNING] Timeout on Bitso REST service request, trying again");
			} catch (URISyntaxException | IOException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
				success = true;
			}
		}
		return null;
	}
	
	private void createHTTPPool() {
		this.pcm = new PoolingHttpClientConnectionManager();
//		this.pcm.setMaxTotal(10);
//		this.pcm.setDefaultMaxPerRoute(5);
	}
}
