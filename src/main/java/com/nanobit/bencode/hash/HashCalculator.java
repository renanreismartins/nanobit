package com.nanobit.bencode.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCalculator {
	public static String sha1Hex(final byte[] bytes) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			return BytesToHex.transform(md.digest(bytes));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
