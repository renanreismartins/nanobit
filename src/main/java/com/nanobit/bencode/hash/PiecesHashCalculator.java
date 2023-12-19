package com.nanobit.bencode.hash;

import com.nanobit.bencode.Piece;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

	public List<Piece> pieces() {
		int mod = fileLength % pieceLength;
		int lastPieceLength = mod == 0 ? pieceLength : mod;

		List<byte[]> hashes = piecesHashes();
		int lastHashIndex = hashes.size() - 1;
		byte[] lastPieceHash = hashes.get(lastHashIndex);

		Stream<Piece> pieces = hashes.stream()
				.limit(lastHashIndex)
				.map(s -> new Piece(pieceLength, s));

		//TODO if piecesHashes returns only one element, we are duplicating it here. BUG
		return Stream
				.concat(pieces, Stream.of(new Piece(lastPieceLength, lastPieceHash)))
				.toList();
	}

	public List<String> piecesHashesHex() {
		return piecesHashes()
				.stream()
				.map(BytesToHex::transform)
				.toList();
	}

	public List<byte[]> piecesHashes() {
		int totalPieces = (int) Math.ceil((double) fileLength / pieceLength);

		//TODO could maybe just divide piecesSHA1 by 20?

		//return IntStream.range(0, totalPieces)
		return IntStream.range(0, 1)
				.mapToObj(i -> Arrays.copyOfRange(piecesSHA1, i * SHA1_SIZE, (i * SHA1_SIZE) + SHA1_SIZE))
				.toList();
	}
}
