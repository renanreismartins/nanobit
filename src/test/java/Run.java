import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.Piece;
import com.nanobit.bencode.TorrentMetadata;
import com.nanobit.bencode.hash.BytesToHex;
import com.nanobit.bencode.hash.PiecesHashCalculator;
import com.nanobit.bencode.peer.Message;
import com.nanobit.bencode.peer.Peer;
import tracker.Client;
import tracker.Response;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Run {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException {
		new Run().run();
	}

	public void run() throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException {

		Path path = Paths.get("initial.avi");
		Files.deleteIfExists(path);

		Decoder decoder = new Decoder(getClass().getResourceAsStream("/boy.torrent"));
		TorrentMetadata meta = new TorrentMetadata(decoder.decodeMap());


		Response response = new Client().some(
				meta.announce.toASCIIString(),
				meta.infoHash.urlEncoded,
				0,
				0,
				735261618,
				6868,
				"00112233445566778899",
				meta.infoHash
		);


		Peer peerConnection = response.findPeerByIp("72.21.17.5");
		peerConnection.connect();
		peerConnection.handshake();
		peerConnection.receiveMessage();
		peerConnection.showInterest();
		peerConnection.receiveMessage();
		peerConnection.receiveMessage();

		//TODO number of pieces calculation should be included on the torrent class
		PiecesHashCalculator piecesHashCalculator = new PiecesHashCalculator(meta.fileLength, meta.pieceLength, meta.pieceHashes);
		Piece firstPiece = piecesHashCalculator.pieces().get(0);

		System.out.println("total blocks " + firstPiece.blocks.size());
		System.out.println(firstPiece.blocks);
		System.out.println();


		firstPiece.blocks.stream().forEach(b -> {
			try {
				peerConnection.sendRequest(0, b.begin, b.size);
				Message message = peerConnection.receiveMessage();

				// TODO CHECK THE BLOCK OFF SET, MESSAGES ARE COMING REPEATED
				if (message.id == 7) {
					System.out.println();
					System.out.println("block received");
					System.out.println();
					System.out.println();
					System.out.println();
					System.out.println();
					int pieceIndex = new BigInteger( Arrays.copyOfRange(message.payload, 0, 4)).intValue();
					int begin = new BigInteger( Arrays.copyOfRange(message.payload, 4, 8)).intValue();
					byte[] data = Arrays.copyOfRange(message.payload, 8, message.payload.length);
					b.data = data;
					//Files.write(Paths.get(pieceIndex + "_" + begin + ".data"), Arrays.copyOfRange(message.payload, 8, message.payload.length));
					Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
				} else {

					while (message.id != 7) {
						System.out.println("not piece message, keep retrying");
						message = peerConnection.receiveMessage();
						System.out.println();
						System.out.println();
						System.out.println();
						if (message.id == 99) {
							break;
						}
					}
				}

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		System.out.println(peerConnection.receiveMessage());

		peerConnection.socket.close();

		ByteBuffer buffer = ByteBuffer.allocate(firstPiece.length);
		firstPiece.blocks.forEach(b -> {
			if (b.data != null) {
				buffer.put(b.data);
			}
		});

		int sum = firstPiece.blocks.stream().mapToInt(b -> b.size).sum();

		System.out.println("buffer.length = " + buffer.position());
		System.out.println("sum = " + sum);
		System.out.println("firstPiece.length = " + firstPiece.length);

		byte[] data = buffer.array();
		System.out.println("piece length end");
		System.out.println(data.length);



		byte[] digestFromDownloadedFile = MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(path));
		byte[] digestFromReceivedData = MessageDigest.getInstance("SHA-1").digest(data);
		byte[] digestFromOriginalTorrent = firstPiece.sha1;


		System.out.println("sha1");
		System.out.println("digestFromDownloadedFile " + Arrays.toString(digestFromDownloadedFile));
		System.out.println("digestFromReceivedData " + Arrays.toString(digestFromReceivedData));
		System.out.println("digestFromOriginalTorrent " + Arrays.toString(digestFromOriginalTorrent));

		String shaFromFile = BytesToHex.transform(digestFromDownloadedFile);
		String shaFromReceivedData = BytesToHex.transform(digestFromReceivedData);
		String shaFromOriginalTorrent = BytesToHex.transform(digestFromOriginalTorrent);


		System.out.println("digestFromDownloadedFile " + shaFromFile);
		System.out.println("shaFromReceivedData " + shaFromReceivedData);
		System.out.println("shaFromOriginalTorrent " + shaFromOriginalTorrent);


		assertEquals(shaFromReceivedData, shaFromFile);
		assertEquals(shaFromOriginalTorrent, shaFromReceivedData);


		assertEquals(firstPiece.sha1.length, digestFromReceivedData.length);
		assertArrayEquals(firstPiece.sha1, digestFromReceivedData);
	}
}
