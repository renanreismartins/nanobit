package com.nanobit.peer.message;

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
}
