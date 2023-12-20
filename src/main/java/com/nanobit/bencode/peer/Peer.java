package com.nanobit.bencode.peer;

import com.nanobit.bencode.hash.BytesToHex;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Peer {
	public final String ip;
	public final int port;
	private final String peerId;
	public Socket socket;
	private InputStream is;

	//TODO Remove primitive obsession
	public Peer(String ip, int port, String peerId) {
		this.ip = ip;
		this.port = port;
		this.peerId = peerId;
	}

	public void connect() throws IOException {
		socket = new Socket(ip, port);
		//TODO log connection, info? debug?
	}

	public void handshake(byte[] infoHash) throws IOException {

		System.out.println("connected : " + socket.isConnected());
		ByteBuffer buffer = ByteBuffer.allocate(68)
				.put((byte) 19);

		System.out.println("remaining:");
		System.out.println(buffer.remaining());

		buffer.put("BitTorrent protocol".getBytes(UTF_8));

		System.out.println("remaining:");
		System.out.println(buffer.remaining());


		buffer.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0);


		try {
			byte[] sha1 = MessageDigest.getInstance("SHA-1").digest(infoHash);
			System.out.println("info hash size: " + sha1.length);
			buffer.put(sha1);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		System.out.println("remaining:");
		System.out.println(buffer.remaining());

		byte[] peerIdBytes = peerId.getBytes(UTF_8);
		System.out.println("peer id size: " + peerIdBytes.length);
		buffer.put(peerIdBytes);

		System.out.println("remaining:");
		System.out.println(buffer.remaining());

		byte[] request = buffer.array();
		System.out.println(new String(request, UTF_8));
		socket.getOutputStream().write(request);
		System.out.println("sent");


		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		is = socket.getInputStream();


		receiveHandshake(is);





		// always read 4 by 4 bytes... because  00011 is unchoke 3 first bytes telling the size, 1 for the id
		// 0000 is keep alive so if you read(5), the socket will read 4 and wait for the next byte that will never arrive
		//socket.close();
	}

	public void sendInterested() throws IOException {
		System.out.println("sending interested");
		ByteBuffer interestedBuffer = ByteBuffer.allocate(5)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 0)
				.put((byte) 1)
				.put((byte) 2); // putInt?
		socket.getOutputStream().write(interestedBuffer.array());
		System.out.println("interest sent");
		System.out.println();
	}

	public Message receiveMessage() throws IOException {
		System.out.println("receiving message");
		byte[] messageSizeBytes = is.readNBytes(4);
		System.out.println("number of bytes read: " + messageSizeBytes.length);
		System.out.println("size bytes: " + Arrays.toString(messageSizeBytes));


		if (messageSizeBytes.length == 0) {
			System.out.println("End of stream");
			return new Message(99, 0, null);
		}

		int messageSize = new BigInteger(messageSizeBytes).intValue();
		System.out.println("size decimal: " + messageSize);

		if (messageSize == 0) {
			System.out.println("keep alive message");
		} else {
			int messageType = is.read();
			System.out.println("message type: " + messageType);
			byte[] message = is.readNBytes(messageSize - 1);
			System.out.println(Arrays.toString(message));
			System.out.println();

			return new Message(messageType, messageSize, message);
		}


		return new Message(0, 0, null);
	}


	private static void receiveHandshake(InputStream is) throws IOException {
		System.out.println("Receiving handshake");
		System.out.println("handshake size: " + is.available());
		byte[] res = is.readNBytes(68); // before = 68 TODO an example reads 1024, maybe this influences on the next msg?
		System.out.println("read 68 bytes from the stream");
		System.out.println("stream: " + Arrays.toString(res));
		System.out.println("String stream: " + new String(res, UTF_8));

		//byte[] id = Arrays.copyOfRange(res, 0, 28);
		//-DE211s-Qva.5QxJANvb
		System.out.println("handshake size after reading 68 bytes: " + is.available());


		System.out.println();
		System.out.println();
		System.out.println();

		System.out.println("peer id from the response");
		byte[] resPeerId = Arrays.copyOfRange(res, 48, 68);
		System.out.println("peer id slice: " + Arrays.toString(resPeerId));
		System.out.println("peer id in hex: " + BytesToHex.transform(resPeerId));
		System.out.println("peer id in String: " + new String(resPeerId, UTF_8));

		//TODO or get the last 20 bytes of the response
	}

	//TODO understand this
	private static int convertBytesToInt(byte[] bytes) {
		return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
				((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
	}



	//TODO pass a message obj instead of this params
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

		System.out.println("sending request");
		System.out.println("piece index:" + pieceIndex);
		System.out.println("block begin:" + begin);
		System.out.println("length:" + length);
		socket.getOutputStream().write(request.array());
		System.out.println("request sent");
		System.out.println();
		System.out.println();
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
	 */
}
