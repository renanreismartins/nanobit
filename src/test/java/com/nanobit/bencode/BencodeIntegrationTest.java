package com.nanobit.bencode;

import com.nanobit.bencode.hash.HashCalculator;
import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;
import org.junit.jupiter.api.Test;
import tracker.InfoHashUrlEncoder;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import static java.nio.file.Files.readAllBytes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BencodeIntegrationTest {
	@Test
	void shouldParseTorrentProperties() throws IOException {
		Decoder decoder = new Decoder(getClass().getResourceAsStream("/sample.torrent"));
		BencodedMap dict = (BencodedMap) decoder.decode();

		assertEquals("http://bittorrent-test-tracker.codecrafters.io/announce", dict.get("announce").asString());
		assertArrayEquals(readAllBytes(Paths.get("src/test/resources/info.hash")), dict.get("info").encode());
		assertArrayEquals(readAllBytes(Paths.get("src/test/resources/pieces.hash")), ((BencodedString) dict.get("info").asMap().get(new BencodedString("pieces"))).value);

		Map<BencodedString, BencodedValue> info = dict.get("info").asMap();
		assertEquals(92063, info.get(new BencodedString("length")).asInteger());
		assertEquals(32768, info.get(new BencodedString("piece length")).asInteger());
		//TODO more properties
	}

	@Test
	void shouldParseTorrent() throws IOException, NoSuchAlgorithmException {
		Decoder decoder = new Decoder(getClass().getResourceAsStream("/boy.torrent"));
		BencodedMap dict = (BencodedMap) decoder.decode();

		//assertEquals("http://bittorrent-test-tracker.codecrafters.io/announce", dict.get("announce").asString());
		//assertArrayEquals(readAllBytes(Paths.get("src/test/resources/info.hash")), dict.get("info").encode());
		System.out.println(InfoHashUrlEncoder.encode(HashCalculator.infoHash(dict.get("info").encode())));
		System.out.println((HashCalculator.infoHash(dict.get("info").encode())));

		Map<BencodedString, BencodedValue> info = dict.get("info").asMap();
		assertEquals(735261618, info.get(new BencodedString("length")).asInteger());
		assertEquals(524288, info.get(new BencodedString("piece length")).asInteger());
		//TODO more properties
	}
}
