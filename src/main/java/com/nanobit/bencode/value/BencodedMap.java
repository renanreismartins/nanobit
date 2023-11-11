package com.nanobit.bencode.value;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BencodedMap implements BencodedValue {
	private final byte[] INITIAL_DELIMITATOR_ENCODED = "d".getBytes(UTF_8);
	private final byte[] FINAL_DELIMITATOR_ENCODED = "e".getBytes(UTF_8);
	private final Map<BencodedString, BencodedValue> map;

	public BencodedMap() {
		map = new HashMap<>();
	}

	public BencodedMap(Map<BencodedString, BencodedValue> map) {this.map = map;}

	public BencodedValue get(String key) {
		return map.get(new BencodedString(key));
	}


	@Override
	public byte[] encode() {
		List<byte[]> encodedEntries = map
				.entrySet()
				.stream()
				.map(e -> {
					byte[] encodedKey = e.getKey().encode();
					byte[] encodedValue = e.getValue().encode();

					int capacity = encodedKey.length + encodedValue.length;

					return ByteBuffer.allocate(capacity)
							.put(encodedKey)
							.put(encodedValue)
							.array();
				}).toList();


		List<byte[]> encodedValues = new ArrayList<>();
		encodedValues.add(INITIAL_DELIMITATOR_ENCODED);
		encodedValues.addAll(encodedEntries);
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
		return map;
	}

	@Override
	public List<BencodedValue> asList() {
		return null;
	}

	@Override
	public String toString() {
		return "BencodedMap{" + "map=" + map + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BencodedMap that = (BencodedMap) o;
		return Objects.equals(map, that.map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(map);
	}
}
