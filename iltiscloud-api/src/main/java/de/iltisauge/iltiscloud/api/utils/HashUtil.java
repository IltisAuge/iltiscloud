package de.iltisauge.iltiscloud.api.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashUtil {
	
	public static String sha256(String input) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			return String.format("%0128x", new BigInteger(1, digest.digest()));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static String sha512(String input) {
		try {
			final MessageDigest digest = MessageDigest.getInstance("SHA-512");
			digest.reset();
			digest.update(input.getBytes("utf8"));
			return String.format("%0128x", new BigInteger(1, digest.digest()));
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return null;
	}
}