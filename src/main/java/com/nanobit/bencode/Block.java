package com.nanobit.bencode;

import java.util.Arrays;

public class Block {
	public final int begin; //TODO call it offset? check official paper
	public final int size; //TODO call it offset? check official paper
	public byte[] data;

	public Block(int begin, int size, byte[] data) {
		this.begin = begin;
		this.size = size;
		this.data = data;
	}

	@Override
	public String toString() {
		return "Block{" +
				"begin=" + begin +
				", size=" + size +
				", data=" + Arrays.toString(data) +
				'}';
	}
}
