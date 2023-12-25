package com.nanobit.bencode.peer.message;

public interface Message {

	static Message create(int messageSize, int messageId, byte[] data) {
		return switch (messageId) {
			case 0 -> new Choke();
			case 1 -> new Unchoke();
			case 5 -> new Bitfield(messageSize, data);
			case 7 -> new Block(messageSize, data);
			default -> throw new IllegalArgumentException("Unknown message id: " + messageId);
		};
	}

	/*
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
	}*/
}
