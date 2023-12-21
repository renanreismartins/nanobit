package com.nanobit.bencode;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PieceTest {

	//TODO refactor to a smaller size
	@Test
	void sumOfBlockSizesShouldBePieceLengthForNonFinalPiece() {
		int pieceSize = 524288;
		byte[] byteArray = new byte[pieceSize];
		Arrays.fill(byteArray, (byte) 0);

		Piece piece = new Piece(pieceSize, byteArray);

		assertEquals(piece.blocks.size(), 32);

		piece.blocks
				.stream()
				.forEach(b -> assertEquals(b.size, Piece.BLOCK_SIZE));

		int sumBlockSize = piece.blocks
				.stream()
				.mapToInt(b -> b.size)
				.sum();

		assertEquals(pieceSize, sumBlockSize);

	}
}