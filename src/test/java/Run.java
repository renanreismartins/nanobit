import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.Piece;
import com.nanobit.bencode.hash.BytesToHex;
import com.nanobit.bencode.hash.HashCalculator;
import com.nanobit.bencode.hash.PiecesHashCalculator;
import com.nanobit.bencode.peer.Message;
import com.nanobit.bencode.peer.Peer;
import com.nanobit.bencode.TorrentMetadata;
import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;
import tracker.Client;
import tracker.InfoHashUrlEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

		TorrentMetadata meta = new TorrentMetadata((BencodedMap) decoder.decode());

		String urlEncodedInfoHash = InfoHashUrlEncoder.encode(HashCalculator.infoHash(meta.encodedInfoHash));

		byte[] trackerResponse = new Client().some(
				meta.announce.toASCIIString(),
				urlEncodedInfoHash,
				0,
				0,
				735261618,
				6868,
				"00112233445566778899"
		);

		Decoder d = new Decoder(new ByteArrayInputStream(trackerResponse));
		System.out.println("tracker response:");
		System.out.println(new String(trackerResponse, StandardCharsets.UTF_8));
		Map<BencodedString, BencodedValue> decodedTrackerResponse = d.decode().asMap();

		List<BencodedValue> peers = decodedTrackerResponse.get(new BencodedString("peers")).asList();
//		BencodedMap peer = (BencodedMap) peers.stream().filter(p -> "76.182.68.238".equals(((BencodedMap) p).get("ip").asString())).findFirst().get();
		BencodedMap peer = (BencodedMap) peers.stream().filter(p -> "72.21.17.5".equals(((BencodedMap) p).get("ip").asString())).findFirst().get();


		System.out.println("calling peer");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();








		Peer peerConnection = new Peer(peer.get("ip").asString(), peer.get("port").asInteger(), "00112233445566778899");
		peerConnection.connect();
		peerConnection.handshake(meta.encodedInfoHash);


		System.out.println();
		System.out.println();
		System.out.println();
		peerConnection.receiveMessage();
		peerConnection.sendInterested();


		//todo remove?
		System.out.println();
		System.out.println();
		System.out.println();
		peerConnection.receiveMessage();


		//peerConnection.sendRequest(0, 0, BLOCK_SIZE);


//		System.out.println();
//		System.out.println();
//		System.out.println();
//		peerConnection.receiveMessage();
//
//		//peerConnection.sendRequest(0, 0, BLOCK_SIZE);
//
//
//
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		peerConnection.receiveMessage();
//
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		peerConnection.receiveMessage();
//
//		System.out.println();
//		System.out.println();
//		System.out.println();
//		peerConnection.receiveMessage();
//
//		Thread.sleep(3000l);
//
//		System.out.println();
//		System.out.println();
//		System.out.println();
		peerConnection.receiveMessage();







		//TODO number of pieces calculation should be included on the torrent class
		PiecesHashCalculator piecesHashCalculator = new PiecesHashCalculator(meta.fileLength, meta.pieceLength, meta.piecesHash);
		Piece firstPiece = piecesHashCalculator.pieces().get(0);

		System.out.println("start blocks");
		System.out.println("start blocks");
		System.out.println("start blocks");
		System.out.println();


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



		int sum = firstPiece.blocks.stream().mapToInt(b -> b.size).sum();
		ByteBuffer buffer = ByteBuffer.allocate(sum);
		firstPiece.blocks.forEach(b -> buffer.put(b.data));
		byte[] data = buffer.array();

		byte[] digestFromDownloadedFile = MessageDigest.getInstance("SHA-1").digest(Files.readAllBytes(path));
		byte[] digestFromReceivedData = MessageDigest.getInstance("SHA-1").digest(data);
		byte[] digestFromOriginalTorrent = firstPiece.sha1;


		System.out.println("sha1");
		System.out.println("digestFromDownloadedFile " + Arrays.toString(digestFromDownloadedFile));
		System.out.println("digestFromReceivedData " + Arrays.toString(digestFromReceivedData));
		System.out.println("digestFromOriginalTorrent " + Arrays.toString(digestFromOriginalTorrent));

		String shaFromFile = BytesToHex.transform(digestFromDownloadedFile);
		String shaFromReceivedData = BytesToHex.transform(digestFromReceivedData);
		String shaFromOriginalTorrent = BytesToHex.transform(firstPiece.sha1);


		System.out.println("digestFromDownloadedFile " + shaFromFile);
		System.out.println("shaFromReceivedData " + shaFromReceivedData);
		System.out.println("shaFromOriginalTorrent " + shaFromOriginalTorrent);


		assertEquals(shaFromOriginalTorrent, shaFromFile);
		assertEquals(shaFromOriginalTorrent, shaFromReceivedData);


		assertEquals(firstPiece.sha1.length, digestFromReceivedData.length);
		assertArrayEquals(firstPiece.sha1, digestFromReceivedData);


		//		List.of().stream().
//
//		IntStream.range(0, totalPieces)
//				.mapToObj(i -> new Piece(i, pieceLength))
//





		//peerConnection.sendRequest(0, 0, BLOCK_SIZE);


	}
}
