package com.nanobit.bencode;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Piece {
	static final int BLOCK_SIZE = 16 * 1024; // 16384
	public final int id;
	public final int length;
	public final byte[] sha1;
	public final List<Block> blocks;

	public Piece(int id, int length, byte[] sha1) {
		this.id = id;
		this.length = length;
		this.sha1 = sha1;

		int totalBlocks = (int) Math.ceil((double) length / BLOCK_SIZE);
		int mod = length % BLOCK_SIZE;
		int lastBlockSize = mod == 0 ? BLOCK_SIZE : mod;

		//TODO instead of concatenating the last block, stream over all blocks
		// and calculate the block_size on the loop block_size = min(self.BLOCK_SIZE, piece.length - piece_offset)
		Stream<Block> blocks = IntStream.rangeClosed(0, totalBlocks)
				.mapToObj(n -> new Block(n * BLOCK_SIZE, BLOCK_SIZE, null));

		//TODO BUG
		// THE LAST BLOCK IS  Block{begin=491520, size=16384, data=null}, Block{begin=524288, size=16384, data=null}]
		// THE BEGIN IS TWICE AS BIG, CHECK IF THE NUMBER OF BLOCKS IS ALSO CORRECT
		Block lastBlock = new Block(totalBlocks * BLOCK_SIZE, lastBlockSize, null);
		this.blocks = Stream.concat(blocks, Stream.of(lastBlock)).toList();
		System.out.println("piece blocks: ");
		System.out.println(this.blocks);
		System.out.println();
	}
}
