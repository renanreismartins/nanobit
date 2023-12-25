package com.nanobit.bencode.peer.message;

import java.math.BigInteger;
import java.util.Arrays;

public class Block implements Message {
	public final int size;
	public final int pieceIndex;
	public final int begin;
	public final byte[] payload;

	public Block(int size, byte[] data) {
		this.size = size;
		pieceIndex = new BigInteger(Arrays.copyOfRange(data, 0, 4)).intValue();
		begin = new BigInteger(Arrays.copyOfRange(data, 4, 8)).intValue();
		payload = Arrays.copyOfRange(data, 8, data.length);
	}
}
