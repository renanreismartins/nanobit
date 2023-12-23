package com.nanobit.bencode.hash;

import com.nanobit.bencode.Piece;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntSupplier;
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

	/*
	public List<Piece> pieces() {
		int mod = fileLength % pieceLength;
		int lastPieceLength = mod == 0 ? pieceLength : mod;

		// TODO HERE IS THE BUG, THE ONLY PIECE IS COMING WITH THE SIZE OF THE LAST PIECE!!!!!!!!!!!!!!!!!
		List<byte[]> hashes = pieceHashes();
		int lastHashIndex = hashes.size() - 1;
		byte[] lastPieceHash = hashes.get(lastHashIndex);

		Stream<Piece> pieces = hashes.stream()
				.limit(lastHashIndex)
				.map(s -> new Piece(pieceLength, s));

		//TODO if piecesHashes returns only one element, we are duplicating it here. BUG
		return Stream
				.concat(pieces, Stream.of(new Piece(lastPieceLength, lastPieceHash)))
				.toList();
	}*/

	public List<Piece> pieces() {
		AtomicInteger id = new AtomicInteger(1);
		return pieceHashes()
				.stream()
				.map(s -> new Piece(id.getAndIncrement(), pieceLength, s))
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

		//TODO could maybe just divide pieceHashes by 20?

		//return IntStream.range(0, totalPieces)
		return IntStream.range(0, 1)
				.mapToObj(i -> Arrays.copyOfRange(pieceHashes, i * SHA1_SIZE, (i * SHA1_SIZE) + SHA1_SIZE))
				.toList();
	}
}
