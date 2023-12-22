package com.nanobit.bencode.peer;

import java.util.Arrays;

public class Message {
	public final int id;
	public final int size;
	public final byte[] payload;

	public Message(int id, int size, byte[] payload) {
		this.id = id;
		this.size = size;
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", size=" + size +
				", payload=" + Arrays.toString(payload) +
				'}';
	}
}
