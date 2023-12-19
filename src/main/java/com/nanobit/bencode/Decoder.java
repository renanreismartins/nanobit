package com.nanobit.bencode;

import com.nanobit.bencode.value.BencodedInteger;
import com.nanobit.bencode.value.BencodedList;
import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Decoder {
	private final InputStream in;

	public Decoder(InputStream in) {this.in = in;}

	public BencodedValue decode() throws IOException {
		char firstChar = peek();
		if (Character.isDigit(firstChar)) {
			return decodeString();
		}

		if ('i' == firstChar) {
			return decodeInteger();
		}

		if ('l' == firstChar) {
			return decodeList();
		}

		if ('d' == firstChar) {
			return decodeDictionary();
		}

		return null;
	}

	//TODO dict and lists seem to be the same kind of data structure, could I 'reduce' it somehow?
	private BencodedValue decodeDictionary() throws IOException {
		in.skip(1);

		char current = peek();
		//TODO check specification for dictionary key ordering
		Map<BencodedString, BencodedValue> dict = new TreeMap<>();
		while (current != 'e') {
			BencodedString key = decodeString();
			dict.put(key, decode());
			current = peek();
		}

		in.skip(1);

		return new BencodedMap(dict);
	}

	//TODO this and other methods have to skip 1 char for the delimiter (string decoding excluded).
	private BencodedValue decodeList() throws IOException {
		in.skip(1);

		char current = peek();
		List<BencodedValue> values = new ArrayList<>();
		while (current != 'e') {
			values.add(decode());
			current = peek();
		}

		in.skip(1);

		return new BencodedList(values);
	}

	//TODO: Consider moving to its own class.
	// It can be generalised and re-used on the String decoding.
	private BencodedValue decodeInteger() throws IOException {
		in.skip(1); // TODO instead of skip, consume initial delimitator consumeInt();

		char nextChar = (char) in.read();
		int number = 0;
		while (Character.isDigit(nextChar)) {
			//TODO Check the possible implementation
			// bytesToRead = bytesToRead * 10 + (nextChar - '0');
			number = number * 10 + Character.getNumericValue(nextChar);
			nextChar = (char) in.read();
		}

		if (nextChar != 'e') {
			//TODO Add better message and better Exception type.
			throw new IllegalStateException("Number malformed: Integer final deliminator (e) missing.");
		}

		return new BencodedInteger(number);
	}

	//TODO: Consider moving to its own class.
	public BencodedString decodeString() throws IOException {
		int bytesToRead = Character.getNumericValue(in.read());


		/**
		 * A simpler implementation could be just the concatenations of the size digits
		 * in a String and made a conversion using a method like Integer.valueOf().
		 */

		char nextChar = (char) in.read();
		while (Character.isDigit(nextChar)) {
			//TODO Check the possible implementation
			// bytesToRead = bytesToRead * 10 + (nextChar - '0');
			bytesToRead = bytesToRead * 10 + Character.getNumericValue(nextChar);
			nextChar = (char) in.read();
		}

		if (nextChar != ':') {
			//TODO Add better message and better Exception type.
			throw new IllegalStateException("String malformed: Size Separator (:) missing.");
		}

		return new BencodedString(in.readNBytes(bytesToRead));
	}

	public char peek() throws IOException {
		in.mark(in.available());
		char ch = (char) in.read();
		in.reset();

		return ch;
	}
}
