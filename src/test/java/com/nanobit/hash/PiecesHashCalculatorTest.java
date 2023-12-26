package com.nanobit.hash;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PiecesHashCalculatorTest {

	// TODO this became an integration test where the piece creation and its hex are tested
	//  the hex calculation was moved to inside the piece, move test accordingly
	@Test
	void calculatePieceHashesHex() throws IOException {
		/*
		Why there is no need for transforming the bytes to unsigned:

		System.out.println(Arrays.toString(pieces)); [-24, ...]
		First byte = -24

		Transform the absolute value to decimal -> 24 = 00011000

		Use the 2's complement because the original value is negative,
		Invert all the bits and then add 1: 11101000

		Convert the 2's complement to decimal: 11101000 -> 232

		Transform the decimal 232 to hex:
		232 / 16 = 14
		15 * 16 = 224

		232 - 224 = 8

		16 ^ 0 = 1     - 8  = 8
		16 ^ 1 = 16    - 14 = e
		16 ^ 2 = 256   - 0

		0xe8

		String.format("%02x", signedByte) works even with a signed byte because it considers the most significant bit as
		data, so when converting the byte to hex it does it correctly:
		-24 = 11101000 (signed byte)
		11101000 = e8 in hex
		11101000 = 232 in decimal using unsigned byte
		232 = e8 in hex
		*/
		
		byte[] pieces = Files.readAllBytes(Paths.get("src/test/resources/pieces.hash"));

		List<String> piecesHashesHex = new PiecesHashCalculator(92063, 32768, pieces).piecesHashesHex();
		assertEquals("e876f67a2a8886e8f36b136726c30fa29703022d", piecesHashesHex.get(0));
		assertEquals("6e2275e604a0766656736e81ff10b55204ad8d35", piecesHashesHex.get(1));
		assertEquals("f00d937a0213df1982bc8d097227ad9e909acc17", piecesHashesHex.get(2));
	}
}
