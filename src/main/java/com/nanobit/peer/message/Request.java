package com.nanobit.peer.message;

import java.nio.ByteBuffer;

public class Request {
	public final byte[] bytes;

	public Request(int pieceIndex, int begin, int length) {
		// 4 for message size (from protocol) + 1 for message type + 3 * 4 = 12 for all the params
		this.bytes = ByteBuffer.allocate(4 + 1 + 12)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 13)
				.put((byte) 6)
				.putInt(pieceIndex)
				.putInt(begin)
				.putInt(length)
				.array();
	}
}
