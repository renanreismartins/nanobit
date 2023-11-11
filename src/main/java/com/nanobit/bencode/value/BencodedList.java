package com.nanobit.bencode.value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BencodedList implements BencodedValue {

	private final byte[] INITIAL_DELIMITATOR_ENCODED = "l".getBytes(UTF_8);
	private final byte[] FINAL_DELIMITATOR_ENCODED = "e".getBytes(UTF_8);
	private final List<BencodedValue> values;

	public BencodedList(List<BencodedValue> values) {
		this.values = values;
	}

	public BencodedList(BencodedValue value) {
		this.values = List.of(value);
	}

	public void add(BencodedValue value) {
		values.add(value);
	}

	@Override
	public byte[] encode() {
		List<byte[]> encodedValues = new ArrayList<>();
		encodedValues.add(INITIAL_DELIMITATOR_ENCODED);
		encodedValues.addAll(values.stream().map(BencodedValue::encode).toList());
		encodedValues.add(FINAL_DELIMITATOR_ENCODED);

		int sum = encodedValues
				.stream()
				.mapToInt(bytes -> bytes.length)
				.sum();

		return encodedValues
				.stream()
				.reduce(ByteBuffer.allocate(sum),
						ByteBuffer::put,
						(acc1, acc2) -> acc1)
				.array();
	}

	@Override
	public String asString() {
		return null;
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
		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BencodedList that = (BencodedList) o;
		return Objects.equals(values, that.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(values);
	}

	@Override
	public String toString() {
		return "BencodedList{" + "values=" + values + '}';
	}
}
