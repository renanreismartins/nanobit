package com.nanobit.bencode.value;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BencodedString implements BencodedValue, Comparable<BencodedString> {
	final byte[] SEPARATOR_ENCODED = ":".getBytes(UTF_8);
	private final byte[] value;

	public BencodedString(byte[] value) {this.value = value;}

	public BencodedString(String value) {
		this.value = value.getBytes(UTF_8);
	}

	@Override
	public byte[] encode() {
		byte[] valueSizeEncoded = String.valueOf(value.length).getBytes(UTF_8);

		ByteBuffer buffer = ByteBuffer.allocate(valueSizeEncoded.length + SEPARATOR_ENCODED.length + value.length);
		return buffer
				.put(valueSizeEncoded)
				.put(SEPARATOR_ENCODED)
				.put(value)
				.array();
	}

	@Override
	public String asString() {
		return new String(value, UTF_8);
	}

	@Override
	public Integer asInteger() {
		return null;
	}

	@Override
	public Map<BencodedString, BencodedValue> asMap() {
		return null;
	}

	@Override
	public List<BencodedValue> asList() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BencodedString that = (BencodedString) o;
		return Arrays.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(value);
	}

	@Override
	public String toString() {
		return "BencodedString{" + "value=" + new String(value, UTF_8) + '}';
	}

	@Override
	public int compareTo(BencodedString other) {
		return asString().compareTo(other.asString());
	}
}
