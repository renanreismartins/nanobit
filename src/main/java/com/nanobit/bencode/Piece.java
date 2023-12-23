package com.nanobit.bencode;

import com.nanobit.bencode.hash.BytesToHex;

import java.util.List;
import java.util.stream.IntStream;

public class Piece {
	static final int BLOCK_SIZE = 16 * 1024; // 16384
	public final int id;
	public final int length;
	public final byte[] sha1;
	public final String hex;
	public final List<Block> blocks;

	public Piece(int id, int length, byte[] sha1) {
		this.id = id;
		this.length = length;
		this.sha1 = sha1;
		this.hex = BytesToHex.transform(this.sha1);

		int totalBlocks = (int) Math.ceil((double) length / BLOCK_SIZE);

		this.blocks = IntStream.range(0, totalBlocks)
				.mapToObj(n -> {
					int offset = n * BLOCK_SIZE;
					return new Block(offset, Math.min(BLOCK_SIZE, length - offset), null);
				})
				.toList();
	}
}
