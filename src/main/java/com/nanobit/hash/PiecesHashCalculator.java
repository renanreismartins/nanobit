package com.nanobit.hash;

import com.nanobit.bencode.Piece;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class PiecesHashCalculator {
	private static final int SHA1_SIZE = 20;
	private final int fileLength;
	private final int pieceLength;
	private final byte[] pieceHashes;

	public PiecesHashCalculator(int fileLength, int pieceLength, byte[] pieceHashes) {
		this.fileLength = fileLength;
		this.pieceLength = pieceLength;
		this.pieceHashes = pieceHashes;
	}

	public List<Piece> pieces() {
		List<byte[]> hashes = pieceHashes();
		return IntStream.range(0, hashes.size())
				.mapToObj(i -> {
					int length = i == hashes.size() - 1 ? fileLength % pieceLength : pieceLength;
					return new Piece(i, length, hashes.get(i));
				})
				.toList();
	}

	public List<String> piecesHashesHex() {
		return pieceHashes()
				.stream()
				.map(BytesToHex::transform)
				.toList();
	}

	public List<byte[]> pieceHashes() {
		int totalPieces = (int) Math.ceil((double) fileLength / pieceLength);
		return IntStream.range(0, totalPieces)
				.mapToObj(i -> Arrays.copyOfRange(pieceHashes, i * SHA1_SIZE, (i * SHA1_SIZE) + SHA1_SIZE))
				.toList();
	}
}
