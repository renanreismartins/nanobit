package com.nanobit.bencode.value;

import java.util.List;
import java.util.Map;

public interface BencodedValue {

	byte[] encode();

	String asString();

	Integer asInteger();

	Map<BencodedString, BencodedValue> asMap();

	List<BencodedValue> asList();

}
