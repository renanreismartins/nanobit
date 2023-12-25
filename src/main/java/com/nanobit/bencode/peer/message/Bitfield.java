package com.nanobit.bencode.peer.message;

public class Bitfield implements Message {
	public static final int ID = 5;
	private final int messageSize;
	private final byte[] data;

	public Bitfield(int messageSize, byte[] data) {
		this.messageSize = messageSize;
		this.data = data;
	}
}
