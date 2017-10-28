/*
 * Classname: OrderBook
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 19/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.business;

import java.io.Serializable;
import java.util.List;

/**
 * Order Book
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 19/10/2017
 */
public class OrderBook implements Serializable {
	/** Class serial version UID. */
	private static final long serialVersionUID = 1L;
	
	private List<Order> bids;
	private List<Order> asks;
//	private SortedSet<Order> bids;
//	private SortedSet<Order> asks;
	private String updatedAt;
	private Long sequence;
	
	/**
	 * Return the bids
	 * @return the bids
	 */
	public List<Order> getBids() {
		return bids;
	}
	/**
	 * Set the bids
	 * @param bids the bids to set
	 */
	public void setBids(List<Order> bids) {
		this.bids = bids;
	}
	/**
	 * Return the asks
	 * @return the asks
	 */
	public List<Order> getAsks() {
		return asks;
	}
	/**
	 * Set the asks
	 * @param asks the asks to set
	 */
	public void setAsks(List<Order> asks) {
		this.asks = asks;
	}
	/**
	 * Return the updatedAt
	 * @return the updatedAt
	 */
	public String getUpdatedAt() {
		return updatedAt;
	}
	/**
	 * Set the updatedAt
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	/**
	 * Return the sequence
	 * @return the sequence
	 */
	public Long getSequence() {
		return sequence;
	}
	/**
	 * Set the sequence
	 * @param sequence the sequence to set
	 */
	public void setSequence(Long sequence) {
		this.sequence = sequence;
	}
}
