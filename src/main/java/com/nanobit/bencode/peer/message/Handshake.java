package com.nanobit.bencode.peer.message;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Handshake {
	public final byte[] bytes;
	public Handshake(String peerId, byte[] infoHashSHA1) {
		byte[] peerIdBytes = peerId.getBytes(UTF_8);

		this.bytes = ByteBuffer.allocate(68)
				.put((byte) 19)
				.put("BitTorrent protocol".getBytes(UTF_8))
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put(infoHashSHA1)
				.put(peerIdBytes)
				.array();
	}
}
