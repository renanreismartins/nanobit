package com.nanobit.bencode.hash;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PiecesHashCalculator {

	private static final int SHA1_SIZE = 20;
	private final int fileLength;
	private final int pieceLength;
	private final byte[] piecesSHA1;

	public PiecesHashCalculator(int fileLength, int pieceLength, byte[] piecesSHA1) {
		this.fileLength = fileLength;
		this.pieceLength = pieceLength;
		this.piecesSHA1 = piecesSHA1;
	}

	public List<String> piecesHashesHex() {
		return piecesHashes()
				.stream()
				.map(BytesToHex::transform)
				.toList();
	}

	private List<byte[]> piecesHashes() {
		int totalPieces = (int) Math.ceil((double) fileLength / pieceLength);

		return IntStream.range(0, totalPieces)
				.mapToObj(i -> Arrays.copyOfRange(piecesSHA1, i * SHA1_SIZE, (i * SHA1_SIZE) + SHA1_SIZE))
				.toList();
	}
}
