package com.nanobit.bencode.peer.message;

import java.nio.ByteBuffer;

public class Interest {
	public final byte[] bytes;

	public Interest() {
		this.bytes = ByteBuffer.allocate(5)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 1)
				.put((byte) 2) // putInt?
				.array();
	}
}
