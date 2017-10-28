/*
 * Classname: Formatter
 * Author: Felipe Olivares (isc.felipe.o@gmail.com)
 * Date: 19/10/2017
 * © Felipe Olivares
 */
package com.sonar.traiding.challenge.core.tool;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Utilities
 * @author: Felipe Olivares (isc.felipe.o@gmail.com)
 * @version: 1.0
 * @date: 19/10/2017
 */
public final class Formatter {
	private static final NumberFormat currency = NumberFormat.getCurrencyInstance();
	private static final NumberFormat number;
	private static DateTimeFormatter dateTimeFormatter;
	private static DateTimeFormatter dateTimeFormatterOffset;
	
	static {
		dateTimeFormatter = new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
				.appendOffset("+HHmm", "Z")
				.toFormatter();
		dateTimeFormatterOffset = new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ISO_LOCAL_DATE)
				.appendLiteral("T")
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
				.appendOffset("+HH:mm", "+00:00")
				.toFormatter();
		number = NumberFormat.getNumberInstance();
		number.setMinimumFractionDigits(2);
		number.setMaximumFractionDigits(Integer.MAX_VALUE);
	}
	
	public static String toCurrency(Number number) {
		return currency.format(number);
	}
	
	public static String toDecimal(Number number) {
		return Formatter.number.format(number);
	}
	
	public static String toLocalDateTime(ZonedDateTime date){
		return dateTimeFormatterOffset.format(date);
	}
	
	public static LocalDateTime fromLocalDateTime(String date) {
		return dateTimeFormatter.parse(date, LocalDateTime::from);
	}
}
