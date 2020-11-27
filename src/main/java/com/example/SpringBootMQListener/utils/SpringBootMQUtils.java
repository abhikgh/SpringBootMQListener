package com.example.SpringBootMQListener.utils;

import java.math.BigDecimal;

public class SpringBootMQUtils {

	public static final String BLANK = "";

	public static String getFarenheit(String celsius) {
		return String.valueOf((Integer.parseInt(celsius) * 9 / 5) + 32);
	}

	public static String getKm(String miles) {
		return String.valueOf(new BigDecimal(miles).multiply(new BigDecimal("1.60934")));
	}

	public static String getPound(String kilogram) {
		return String.valueOf(new BigDecimal(kilogram).multiply(new BigDecimal("2.20462")));
	}

	
}
