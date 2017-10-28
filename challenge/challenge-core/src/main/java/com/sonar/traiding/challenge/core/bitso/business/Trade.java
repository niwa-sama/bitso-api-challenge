/*
 * Classname: Trade
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
 * Trade
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 19/10/2017
 */
public class Trade implements Serializable, Comparable<Trade> {
	/** Class serial version UID. */
	private static final long serialVersionUID = 1L;
	
	private String book;
	
	private Double amount;
	private LocalDateTime createdAt;
	private String markerSide;
	private Double price;
	private Long tradeID;
	
	private Boolean virtual = false;
	
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
	 * Return the createdAt
	 * @return the createdAt
	 */
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	/**
	 * Set the createdAt
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(String createdAt) {
		this.createdAt = Formatter.fromLocalDateTime(createdAt);
	}
	/**
	 * Set the createdAt
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(long createdAt) {
		this.createdAt = LocalDateTime.ofEpochSecond(createdAt / 1000, (int) createdAt % 1000 * 1000, ZoneOffset.UTC);
	}
	/**
	 * Set the createdAt
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
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
	 * Return the tradeID
	 * @return the tradeID
	 */
	public Long getTradeID() {
		return tradeID;
	}
	/**
	 * Set the tradeID
	 * @param tradeID the tradeID to set
	 */
	public void setTradeID(Long tradeID) {
		this.tradeID = tradeID;
	}
	
	/**
	 * Return the virtual
	 * @return the virtual
	 */
	public Boolean getVirtual() {
		return virtual;
	}
	/**
	 * Set the virtual
	 * @param virtual the virtual to set
	 */
	public void setVirtual(Boolean virtual) {
		this.virtual = virtual;
	}
	public String getPriceFormatted() {
		return Formatter.toCurrency(price);
	}
	public String getAmountFormatted() {
		return Formatter.toDecimal(amount);
	}
	public String getCreatedAtFormatted() {
		return Formatter.toLocalDateTime(createdAt.atZone(ZoneId.of("UTC")));
	}
	

	@Override
	public int compareTo(Trade o) {
		int r = o.getCreatedAt().compareTo(getCreatedAt());
		if (r == 0)
			return Long.compare(o.getTradeID(), this.tradeID);
		else
			return r;
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
		result = prime * result + ((tradeID == null) ? 0 : tradeID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Trade))
			return false;
		Trade other = (Trade) obj;
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
		if (tradeID == null) {
			if (other.tradeID != null)
				return false;
		} else if (!tradeID.equals(other.tradeID))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Trade [book=").append(book).append(", createdAt=").append(createdAt).append(", amount=")
				.append(amount).append(", markerSide=").append(markerSide).append(", price=").append(price)
				.append(", tradeID=").append(tradeID).append("]");
		return builder.toString();
	}
}
