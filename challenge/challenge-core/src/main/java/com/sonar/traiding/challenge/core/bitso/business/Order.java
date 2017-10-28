/*
 * Classname: Order
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 19/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.bitso.business;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import com.sonar.traiding.challenge.core.tool.Formatter;

/**
 * Order
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 19/10/2017
 */
public class Order implements Serializable, Comparable<Order> {
	/** Class serial version UID. */
	private static final long serialVersionUID = 1L;
	
	private String book;
	
	private Double amount;
	private LocalDateTime timestamp;
	private String markerSide;
	private Double price;
	private String orderID;
	
	private String status;
	
	/**
	 * Return the book
	 * @return the book
	 */
	public String getBook() {
		return book;
	}
	/**
	 * Set the book
	 * @param book the book to set
	 */
	public void setBook(String book) {
		this.book = book;
	}
	/**
	 * Return the timestamp
	 * @return the timestamp
	 */
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	/**
	 * Set the timestamp
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = Formatter.fromLocalDateTime(timestamp);
	}
	/**
	 * Set the timestamp
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = LocalDateTime.ofEpochSecond(timestamp / 1000, (int) timestamp % 1000 * 1000, ZoneOffset.UTC);
	}
	/**
	 * Set the timestamp
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	/**
	 * Return the amount
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * Set the amount
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * Return the markerSide
	 * @return the markerSide
	 */
	public String getMarkerSide() {
		return markerSide;
	}
	/**
	 * Set the markerSide
	 * @param markerSide the markerSide to set
	 */
	public void setMarkerSide(String markerSide) {
		this.markerSide = markerSide;
	}
	/**
	 * Return the price
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}
	/**
	 * Set the price
	 * @param price the price to set
	 */
	public void setPrice(Double price) {
		this.price = price;
	}
	/**
	 * Return the orderID
	 * @return the orderID
	 */
	public String getOrderID() {
		return orderID;
	}
	/**
	 * Set the orderID
	 * @param tradeID the orderID to set
	 */
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	
	/**
	 * Return the status
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * Set the status
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getPriceFormatted() {
		return Formatter.toCurrency(price);
	}
	public String getAmountFormatted() {
		return Formatter.toDecimal(amount);
	}
	public String getTimestampFormatted() {
		return Formatter.toLocalDateTime(timestamp.atZone(ZoneId.of("UTC")));
	}
	

	@Override
	public int compareTo(Order o) {
		return this.orderID.compareTo(o.getOrderID());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
//		result = prime * result + ((book == null) ? 0 : book.hashCode());
//		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
//		result = prime * result + ((markerSide == null) ? 0 : markerSide.hashCode());
//		result = prime * result + ((price == null) ? 0 : price.hashCode());
		result = prime * result + ((orderID == null) ? 0 : orderID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Order))
			return false;
		Order other = (Order) obj;
//		if (amount == null) {
//			if (other.amount != null)
//				return false;
//		} else if (!amount.equals(other.amount))
//			return false;
//		if (book == null) {
//			if (other.book != null)
//				return false;
//		} else if (!book.equals(other.book))
//			return false;
//		if (createdAt == null) {
//			if (other.createdAt != null)
//				return false;
//		} else if (!createdAt.equals(other.createdAt))
//			return false;
//		if (markerSide == null) {
//			if (other.markerSide != null)
//				return false;
//		} else if (!markerSide.equals(other.markerSide))
//			return false;
//		if (price == null) {
//			if (other.price != null)
//				return false;
//		} else if (!price.equals(other.price))
//			return false;
		if (orderID == null) {
			if (other.orderID != null)
				return false;
		} else if (!orderID.equals(other.orderID))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Order [book=").append(book).append(", timestamp=").append(timestamp).append(", amount=")
				.append(amount).append(", markerSide=").append(markerSide).append(", price=").append(price)
				.append(", tradeID=").append(orderID).append("]");
		return builder.toString();
	}
}
