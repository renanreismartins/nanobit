import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.TorrentMetadata;
import com.nanobit.bencode.hash.PiecesHashCalculator;
import com.nanobit.bencode.peer.Peer;
import tracker.Client;
import tracker.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class Run {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException {
		new Run().run();
	}

	public void run() throws IOException, URISyntaxException, InterruptedException {

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

		// TODO Temporal dependency
		peerConnection.connect();
		peerConnection.handshake();
		peerConnection.receiveHandshake();

		peerConnection.receiveMessage();
		peerConnection.showInterest();
		peerConnection.receiveMessage();
		peerConnection.receiveMessage();

		//TODO number of pieces calculation should be included on the torrent class
		PiecesHashCalculator piecesHashCalculator = new PiecesHashCalculator(meta.fileLength, meta.pieceLength, meta.pieceHashes);

		piecesHashCalculator.pieces().forEach(p -> {
			System.out.println("Downloading piece: " + p.id);
			//Path piecePath = path.resolveSibling(path.getFileName() + "_" + p.id);
			peerConnection.download(p, path);
		});


		peerConnection.socket.close();


	}
}
