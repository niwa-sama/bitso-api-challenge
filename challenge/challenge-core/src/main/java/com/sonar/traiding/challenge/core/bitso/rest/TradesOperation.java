/*
 * Classname: TradesOperation
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 13/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.rest;

/**
 * Trades operation
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 13/10/2017
 */
public class TradesOperation extends Operation {
	public TradesOperation() {
		url = TRADES;
		method = Method.GET;
	}
}
