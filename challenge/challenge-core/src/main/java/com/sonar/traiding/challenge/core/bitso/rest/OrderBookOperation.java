/*
 * Classname: OrderBookOperation
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 13/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.rest;

/**
 * Order Book Operation
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 13/10/2017
 */
public class OrderBookOperation extends Operation {
	
	public OrderBookOperation() {
		url = ORDER_BOOK;
		method = Method.GET;
	}
}
