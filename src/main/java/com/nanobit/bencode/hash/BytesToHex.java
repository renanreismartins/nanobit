package com.nanobit.bencode.hash;

import java.math.BigInteger;

public class BytesToHex {
	public static String transform(byte[] bytes) {
		return String.format("%0" + (bytes.length << 1) + "x", new BigInteger(1, bytes));
	}
}
