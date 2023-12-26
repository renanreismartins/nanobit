package com.nanobit.hash;

import java.math.BigInteger;

public class BytesToHex {
	public static String transform(byte[] bytes) {
		/*
		The bitwise left shift operation (byte << X) shifts the bits X positions to the left.

		Let's assume bytes.length = 1
		1 decimal in binary 8 bits = 00000001
		1 << 1 =  00000010 = 2

		Let's assume bytes.length = 2
		2 decimal in binary 8 bits = 00000010
		2 << 1 = 00000100 = 4

		int << 1 = multiplication by 2

		This operation is telling the formatter to format a String twice the size of the array of bytes.
		Because each byte is going to be represented by 2 characters.

		-----------------------------------------------------------------

		Passing an array of bytes to the BigInteger constructor will construct a huge number based
		on the binary representation of each byte.
		new BigInteger(1, bytes) transforms the array of bytes into a positive integer.
		For example: b = {10, 15} in big-endian binary format is (10) 00001010 (15) 00001111 = 2575
		The following code evaluates to:
		String.format("%0" + (2 << 1) + "x", new BigInteger(1, 2575))
		And will return "0a0f"
		 */
		return String.format("%0" + (bytes.length << 1) + "x", new BigInteger(1, bytes));
	}
}
