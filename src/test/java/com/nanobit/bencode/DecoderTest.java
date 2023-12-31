package com.nanobit.bencode;

import com.nanobit.bencode.value.BencodedInteger;
import com.nanobit.bencode.value.BencodedList;
import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecoderTest {

	//TODO: I had to expose a mutable state of Decoder, the ByteArrayInputStream, to make assertions
	// the decoder is doing its side effects on it. It might be a good approach to encapsulate ByteArrayInputStream in
	// a specialised class
	@Test
	void peekShouldNotChangeTheStream() throws IOException {
		ByteArrayInputStream in = toInputStream("5:renan");
		Decoder decoder = new Decoder(in);
		char firstPeeked = decoder.peek();
		char secondPeeked = decoder.peek();
		char firstRealChar = (char) in.read();
		char secondRealChar = (char) in.read();

		assertEquals('5', firstPeeked);
		assertEquals('5', secondPeeked);
		assertEquals('5', firstRealChar);
		assertEquals(':', secondRealChar);

	}

	/**
	 * String
	 */

	//TODO Add test for empty String as it is part of the spec.

	@Test
	void decodeString() throws IOException {
		Decoder decoder = new Decoder(toInputStream("5:renan"));
		assertEquals("renan", decoder.decode().asString());
	}

	@Test
	void decodeStringWithDoubleDigitLength() throws IOException {
		Decoder decoder = new Decoder(toInputStream("10:renanrenan"));
		assertEquals("renanrenan", decoder.decode().asString());
	}

	@Test
	void errorOnStringMissingSizeSeparator() {
		assertThrows(IllegalStateException.class, () -> new Decoder(toInputStream("5renan")).decode());
	}

	@Test
	void errorOnStringWithOnlyItsSize() {
		assertThrows(IllegalStateException.class, () -> new Decoder(toInputStream("5")).decode());
	}


	/**
	 * Integer
	 */

	@Test
	void decodeInteger() throws IOException {
		Decoder decoder = new Decoder(toInputStream("i7e"));
		assertEquals(7, decoder.decode().asInteger());
	}

	@Test
	void decodeIntegerWithMoreThanOneDigit() throws IOException {
		Decoder decoder = new Decoder(toInputStream("i71e"));
		assertEquals(71, decoder.decode().asInteger());
	}

	@Test
	void decodeIntegerWithoutFinalDelimitator()  {
		assertThrows(IllegalStateException.class, () -> new Decoder(toInputStream("i7")).decode());
	}

	@Disabled("Check specification to see if it is valid")
	@Test
	void decodeEmptyInteger() throws IOException {
		Decoder decoder = new Decoder(toInputStream("ie"));
		assertEquals(0, decoder.decode().asInteger());
	}


	/**
	 * List
	 */

	@Test
	void decodeEmptyList() throws IOException {
		Decoder decoder = new Decoder(toInputStream("le"));

		assertEquals(List.of(), decoder.decode().asList());
	}

	@Test
	void decodeListWithOneElement() throws IOException {
		Decoder decoder = new Decoder(toInputStream("l5:renane"));

		assertEquals(List.of(new BencodedString("renan".getBytes())), decoder.decode().asList());
	}

	@Test
	void decodeListWithTwoElements() throws IOException {
		Decoder decoder = new Decoder(toInputStream("l5:renani7ee"));

		assertEquals(List.of(new BencodedString("renan".getBytes()), new BencodedInteger(7)), decoder.decode().asList());
	}

	@Test
	void decodeNestedLists() throws IOException {
		Decoder decoder = new Decoder(toInputStream("lli7eee"));

		BencodedValue decoded = decoder.decode();

		assertEquals(List.of(new BencodedList(new BencodedInteger(7))), decoded.asList());
		assertEquals(new BencodedList(new BencodedList(new BencodedInteger(7))), decoded);
	}


	/**
	 * Dictionary
	 */

	@Test
	void decodeDictionaryWithOneElement() throws IOException {
		Decoder decoder = new Decoder(toInputStream("d3:foo3:bare"));

		BencodedValue decoded = decoder.decode();

		assertEquals(new BencodedMap(Map.of(new BencodedString("foo"), new BencodedString("bar"))), decoded);
		assertEquals(Map.of(new BencodedString("foo"), new BencodedString("bar")), decoded.asMap());
	}

	@Test
	void decodeDictionaryWithTwoElements() throws IOException {
		Decoder decoder = new Decoder(toInputStream("d3:foo3:bare"));

		BencodedValue decoded = decoder.decode();

		assertEquals(new BencodedMap(Map.of(new BencodedString("foo"), new BencodedString("bar"))), decoded);
		assertEquals(Map.of(new BencodedString("foo"), new BencodedString("bar")), decoded.asMap());
	}

	@Test
	void bug() throws IOException {
		Decoder decoder = new Decoder(toInputStream("04:port"));

		BencodedValue decoded = decoder.decode();

		System.out.println(decoded.asString());

	}

	@Test
	void read() throws IOException {
		System.out.println(Arrays.toString(toInputStream("renan").readNBytes(10)));
	}

	@Test
	void name() throws IOException {
		String s = "d8:intervali1800e5:peers" +
				"l" +
					"d" +
						"2:ip13:152.117.104.8" +
						"7:peer id20:-DE211D-1*7yrAPg7l(W" +
						"4:porti48683ee" +
				"d" +
						"2:ip15:102.129.234.168" +
						"7:peer id20:-qB4610-GVWlgxL4Y5K6" +
						"4:porti47676ee" +
				"d" +
						"2:ip11:82.3.217.65" +
						"7:peer id20:00112233445566778899" +
						"4:porti6868ee" +
				"d" +
						"2:ip12:85.220.41.99" +
						"7:peer id20:M7-4-3--\u0015���\u0006.��\\04:porti61422eed2:ip13:76.182.68.2387:peer id20:-qB4600-Dud3RmH4DQn74:porti8999eed2:ip11:84.17.41.767:peer id20:-TR4030-dogb9qud2g7d4:porti51413eed2:ip10:72.21.17.57:peer id20:-DE211s-Qva.5QxJANvb4:porti42546eeee";

		Decoder decoder = new Decoder(toInputStream(s));

		System.out.println("size");
		System.out.println(s.length());
		BencodedValue decoded = decoder.decode();
	}

	/**
	 *
	 * d8:intervali1800e5:peersld2:ip13:152.117.104.87:peer id20:-DE211D-1*7yrAPg7l(W4:porti48683eed2:ip15:102.129.234.1687:peer id20:-qB4610-GVWlgxL4Y5K64:porti47676eed2:ip11:82.3.217.657:peer id20:001122334455667788994:porti6868eed2:ip12:85.220.41.997:peer id20:M7-4-3--���.��\
	 * 04:porti61422eed2:ip13:76.182.68.2387:peer id20:-qB4600-Dud3RmH4DQn74:porti8999eed2:ip11:84.17.41.767:peer id20:-TR4030-dogb9qud2g7d4:porti51413eed2:ip10:72.21.17.57:peer id20:-DE211s-Qva.5QxJANvb4:porti42546eeee
	 */




	private static ByteArrayInputStream toInputStream(String str) {
		return new ByteArrayInputStream(str.getBytes(UTF_8));
	}
}
