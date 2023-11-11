package com.nanobit.bencode.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCalculator {
	public static String infoHash(final byte[] bytes) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		return BytesToHex.transform(md.digest(bytes));
	}
}
