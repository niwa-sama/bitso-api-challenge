/*
 * Classname: BookOperation
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 13/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.rest;

/**
 * Book operation
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 13/10/2017
 */
public class BookOperation extends Operation {
	public BookOperation() {
		url = BOOK;
		method = Method.GET;
	}
}
