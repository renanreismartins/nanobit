package com.nanobit.bencode.value;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BencodeValueTest {

	//TODO test as* methods
	@Test
	void encodeString() {
		assertEquals("5:renan", new String(new BencodedString("renan").encode(), UTF_8));
	}

	@Test
	void encodeInteger() {
		assertEquals("i7e", new String(new BencodedInteger(7).encode(), UTF_8));
	}

	@Test
	void encodeList() {
		assertEquals("li7ee", new String(new BencodedList(new BencodedInteger(7)).encode(), UTF_8));
	}

	@Test
	void encodeDictionary() {
		assertEquals("d5:renani7ee", new String(new BencodedMap(Map.of(new BencodedString("renan"), new BencodedInteger(7))).encode(), UTF_8));
	}
}