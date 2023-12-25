package com.nanobit.bencode.peer;

import com.nanobit.bencode.Piece;
import com.nanobit.bencode.hash.InfoHash;
import com.nanobit.bencode.peer.message.Block;
import com.nanobit.bencode.peer.message.EndOfStream;
import com.nanobit.bencode.peer.message.Handshake;
import com.nanobit.bencode.peer.message.Interest;
import com.nanobit.bencode.peer.message.KeepAlive;
import com.nanobit.bencode.peer.message.Message;
import com.nanobit.bencode.peer.message.Request;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

import static java.lang.String.format;

public class Peer {
	public final String ip;
	public final int port;
	private final String peerId;
	public Socket socket;
	public final InfoHash infoHash;
	public final Path file;
	private final Logger LOG = Logger.getLogger(Peer.class.getName());

	//TODO Maybe create a PeerResponse to handle the response from the tracker
	// Then a Peer to represent the peer connection and receive a socket,
	// so the construction of the obj do not depend on IO.
	public Peer(String ip, int port, String peerId, InfoHash infoHash) {
		this.ip = ip;
		this.port = port;
		this.peerId = peerId;
		this.infoHash = infoHash;
		this.file = null; // TODO
	}

	public void connect() throws IOException {
		socket = new Socket(ip, port);
		LOG.info(format("Connected to: %s:%d. PeerId: %s", ip, port, peerId));
	}

	public void handshake() throws IOException {
		socket.getOutputStream().write(new Handshake(peerId, infoHash.sha1).bytes);
	}

	public void showInterest() throws IOException {
		LOG.info(format("Sending Interest. Ip: %s", ip));
		socket.getOutputStream().write(new Interest().bytes);
		LOG.info(format("Interest sent. Ip: %s", ip));
	}

	public Message receiveMessage() throws IOException {
		InputStream is = socket.getInputStream();

		LOG.info("Receiving message.");
		byte[] messageSizeBytes = is.readNBytes(4);

		if (messageSizeBytes.length == 0) {
			LOG.info("Message received: End of Stream.");
			return new EndOfStream();
		}

		int messageSize = new BigInteger(messageSizeBytes).intValue();
		LOG.info(format("Message size in bytes: %d", messageSize));

		if (messageSize == 0) {
			return new KeepAlive();
		}

		int messageType = is.read();
		LOG.info(format("Message received: %d", messageType));
		byte[] message = is.readNBytes(messageSize - 1);

		return Message.create(messageSize, messageType, message);
	}


	public void receiveHandshake() throws IOException {
		InputStream is = socket.getInputStream();
		LOG.info("Receiving handshake response.");
		byte[] res = is.readNBytes(68);
		LOG.info("Handshake received.");

		// TODO What to do with the Peer Id from this message? or even with the message
		// byte[] resPeerId = Arrays.copyOfRange(res, 48, 68);
	}

	public void sendRequest(Request request) throws IOException {
		LOG.info("Requesting Piece.");
		socket.getOutputStream().write(request.bytes);
		LOG.info("Piece Requested.");
	}

	public void download(Piece piece, Path path) {
		ByteBuffer buffer = ByteBuffer.allocate(piece.length);
		piece.blocks.stream().forEach(b -> {
			try {
				this.sendRequest(new Request(piece.id, b.begin, b.size));
				Message message = this.receiveMessage();

				if (message instanceof Block) {
					Block block = (Block) message;
					buffer.put(block.payload);
					Files.write(path, block.payload, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
				} else {
					while (!(message instanceof Block)) {
						System.out.println("not piece message, keep retrying");
						message = this.receiveMessage();
						if (message instanceof EndOfStream) {
							break;
						}
					}
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		piece.assertIntegrity(buffer.array());
	}

	/*
	TODO: This transform an integer in a byte[]. To understand later
	public class IntegerToByteArray {
    public static void main(String[] args) {
        // Convert Integer to byte array manually
        int number = 12345; // Your integer value
        byte[] byteArray = new byte[Integer.BYTES];

        for (int i = 0; i < Integer.BYTES; ++i) {
            byteArray[i] = (byte) (number >> (i * 8));
        }

        // Display the byte array
        System.out.println("Byte Array:");
        for (byte b : byteArray) {
            System.out.print(String.format("%02X ", b));
        }
    }
}

	//TODO understand this
	private static int convertBytesToInt(byte[] bytes) {
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
				((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
	}
	 */
}
