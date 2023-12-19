package com.nanobit.bencode;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Piece {

	static final int BLOCK_SIZE = 16 * 1024; // 16384
	private final int length;
	public final byte[] sha1;
	public final List<Block> blocks;

	public Piece(int length, byte[] sha1) {
		this.length = length;
		this.sha1 = sha1;

		int totalBlocks = (int) Math.ceil((double) length / BLOCK_SIZE);
		int mod = length % BLOCK_SIZE;
		int lastBlockSize = mod == 0 ? BLOCK_SIZE : mod;

		//TODO BUG IF THERE IS ONLY ONE BLOCK?
		Stream<Block> blocks = IntStream.range(0, totalBlocks - 1)
				.mapToObj(n -> new Block(n * BLOCK_SIZE, BLOCK_SIZE, null));

		Block lastBlock = new Block(totalBlocks * BLOCK_SIZE, lastBlockSize, null);
		this.blocks = Stream.concat(blocks, Stream.of(lastBlock)).toList();
		System.out.println("piece blocks: ");
		System.out.println(this.blocks);
		System.out.println();
	}
}
