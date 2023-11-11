package com.nanobit.bencode.value;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BencodedInteger implements BencodedValue {

	private final byte[] INITIAL_DELIMITATOR_ENCODED = "i".getBytes(UTF_8);
	private final byte[] FINAL_DELIMITATOR_ENCODED = "e".getBytes(UTF_8);
	private final Integer value;

	public BencodedInteger(Integer value) {
		this.value = value;
	}

	@Override
	public byte[] encode() {
		byte[] valueEncoded = String.valueOf(value).getBytes(UTF_8);

		ByteBuffer buffer = ByteBuffer.allocate(INITIAL_DELIMITATOR_ENCODED.length + valueEncoded.length + FINAL_DELIMITATOR_ENCODED.length);
		return buffer
				.put(INITIAL_DELIMITATOR_ENCODED)
				.put(valueEncoded)
				.put(FINAL_DELIMITATOR_ENCODED)
				.array();
	}

	@Override
	public String asString() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Integer asInteger() {
		return value;
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
		BencodedInteger that = (BencodedInteger) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "BencodedInteger{" + "value=" + value + '}';
	}
}

