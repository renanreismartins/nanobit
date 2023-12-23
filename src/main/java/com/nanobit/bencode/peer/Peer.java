package com.nanobit.bencode.peer;

import com.nanobit.bencode.Piece;
import com.nanobit.bencode.hash.BytesToHex;
import com.nanobit.bencode.hash.InfoHash;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

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
		byte[] peerIdBytes = peerId.getBytes(UTF_8);

		byte[] request = ByteBuffer.allocate(68)
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
				.put(infoHash.sha1)
				.put(peerIdBytes)
				.array();

		LOG.info(format("Performing handshake. PeerId: %s", peerId));
		LOG.info(format("Handshake request: %s", new String(request, UTF_8))); //TODO fine level

		socket.getOutputStream().write(request);

		LOG.info("Handshake sent.");
	}

	public void showInterest() throws IOException {
		LOG.info("Sending Interest.");

		ByteBuffer interestedBuffer = ByteBuffer.allocate(5)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 1)
				.put((byte) 2); // putInt?
		socket.getOutputStream().write(interestedBuffer.array());

		LOG.info("Interest sent.");
	}

	public Message receiveMessage() throws IOException {
		InputStream is = socket.getInputStream();

		LOG.info("Receiving message.");
		byte[] messageSizeBytes = is.readNBytes(4);
		LOG.fine(format("Message size bytes: %s", Arrays.toString(messageSizeBytes)));

		if (messageSizeBytes.length == 0) {
			LOG.info("Message received: End of Stream.");
			// TODO what to do in this case? Return null obj like this? If so change id to negative
			return new Message(99, 0, new byte[]{});
		}

		int messageSize = new BigInteger(messageSizeBytes).intValue();
		LOG.info(format("Message size in bytes: %d", messageSize));

		if (messageSize == 0) {
			LOG.info("Message received: Keep Alive.");
		} else {
			int messageType = is.read();
			byte[] message = is.readNBytes(messageSize - 1);

			LOG.info(format("Message received: %d", messageType));
			LOG.fine(format("Message payload: %s", Arrays.toString(message)));

			return new Message(messageType, messageSize, message);
		}

		return new Message(0, 0, null);
	}


	public void receiveHandshake() throws IOException {
		InputStream is = socket.getInputStream();
		LOG.info("Receiving handshake response.");
		byte[] res = is.readNBytes(68);
		LOG.info("Handshake received.");

		// TODO What to do with the Peer Id from this message? or even with the message
		// byte[] resPeerId = Arrays.copyOfRange(res, 48, 68);
	}

	//TODO pass a message obj instead of this params
	// TODO send(Message) then each message knows how to format itself, factory methods
	public void sendRequest(int pieceIndex, int begin, int length) throws IOException {
		// 4 for message size (from protocol) + 1 for message type + 4 * 3 = 12 for all the params
		// + 4 + 4 for the two other params
		ByteBuffer request = ByteBuffer.allocate(4 + 1 + 12)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 13)
				.put((byte) 6)
				.putInt(pieceIndex)
				.putInt(begin)
				.putInt(length);

		LOG.info("Requesting Piece.");
		socket.getOutputStream().write(request.array());
		// TODO log in level fine the request and level info the params
		LOG.info("Piece Requested.");
	}

	public void download(Piece piece, Path path) {

		ByteBuffer buffer = ByteBuffer.allocate(piece.length);
		piece.blocks.stream().forEach(b -> {
			try {
				this.sendRequest(piece.id, b.begin, b.size);
				Message message = this.receiveMessage();

				// TODO CHECK THE BLOCK OFF SET, MESSAGES ARE COMING REPEATED
				if (message.id == 7) {
					System.out.println("block received");
					int pieceIndex = new BigInteger(Arrays.copyOfRange(message.payload, 0, 4)).intValue();
					int begin = new BigInteger(Arrays.copyOfRange(message.payload, 4, 8)).intValue();
					byte[] data = Arrays.copyOfRange(message.payload, 8, message.payload.length);
					buffer.put(data);
					//Files.write(Paths.get(pieceIndex + "_" + begin + ".data"), Arrays.copyOfRange(message.payload, 8, message.payload.length));
					Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
				} else {

					while (message.id != 7) {
						System.out.println("not piece message, keep retrying");
						message = this.receiveMessage();
						if (message.id == 99) {
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
