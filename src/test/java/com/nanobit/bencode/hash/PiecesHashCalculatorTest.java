package com.nanobit.bencode.hash;

import com.nanobit.bencode.hash.PiecesHashCalculator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiecesHashCalculatorTest {

	@Test
	void calculatePieceHashesHex() throws IOException {
		byte[] pieces = Files.readAllBytes(Paths.get("src/test/resources/pieces.hash"));

		List<String> piecesHashesHex = new PiecesHashCalculator(92063, 32768, pieces).piecesHashesHex();
		assertEquals("e876f67a2a8886e8f36b136726c30fa29703022d", piecesHashesHex.get(0));
		assertEquals("6e2275e604a0766656736e81ff10b55204ad8d35", piecesHashesHex.get(1));
		assertEquals("f00d937a0213df1982bc8d097227ad9e909acc17", piecesHashesHex.get(2));
	}
}