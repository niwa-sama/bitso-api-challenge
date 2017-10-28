/*
 * Classname: Operation
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 13/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Base Operation to be extended by specific operations
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 13/10/2017
 */
public abstract class Operation {
	protected static final String BOOK = "/v3/ticker/";
	protected static final String ORDER_BOOK = "/v3/order_book/";
	protected static final String TRADES = "/v3/trades/";
	
	protected Map<String, List<String>> params = new HashMap<>();
	protected Method method;
	protected String url;
	
	public enum Method {
		GET, POST;
	}

	public enum Param {
		BOOK("book"),
		AGGREGATE("aggregate"),
		MARKER("marker"),
		SORT("sort"),
		LIMIT("limit");
		
		private String name;
		
		private Param(String name) {
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
	}

	protected Operation() { }
	
	public static Builder builder() {
		return new Builder();
	}
	
	public String path() {
		if(Method.GET.equals(method)) {
			final StringBuilder sb = new StringBuilder();
			if(params != null & params.size() > 0) {
				sb.append("?");
				params.entrySet().stream().forEach(entry -> {
					final String name = entry.getKey();
					entry.getValue().stream().forEach(value -> {
						try {
							final String encValue = URLEncoder.encode(value, "UTF-8");
							if(sb.length() > 1)
								sb.append("&");
							sb.append(name).append("=").append(encValue);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					});
				});
			}
			return sb.insert(0, url).toString();
		}
		return url;
	}
	
	public static class Builder {
		private Map<String, List<String>> params = new HashMap<>();
		private Operation operation;
		
		public synchronized Builder addParameter(Param name, String value) {
			if(params.containsKey(name.getName())) {
				params.get(name.getName()).add(value);
			} else {
				List<String> values = new ArrayList<>();
				values.add(value);
				params.put(name.getName(), values);
			}
			return this;
		}
		
		public synchronized Builder setParameter(Param name, String value) {
			List<String> values = new ArrayList<>();
			values.add(value);
			params.put(name.getName(), values);
			return this;
		}

		public synchronized Builder orderBook() {
			this.operation = new OrderBookOperation();
			return this;
		}
		
		public synchronized Builder book() {
			this.operation = new BookOperation();
			return this;
		}
		
		public synchronized Builder trades() {
			this.operation = new TradesOperation();
			return this;
		}

		public synchronized Operation build() {
			if(operation == null) {
				throw new NoSuchElementException("You must select an operation in order to build it.");
			}
			operation.params = params;
			return operation;
		}
	}
}
