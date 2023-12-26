import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.TorrentMetadata;
import com.nanobit.hash.PiecesHashCalculator;
import com.nanobit.peer.Peer;
import com.nanobit.tracker.Client;
import com.nanobit.tracker.Response;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import static java.lang.String.format;

public class Run {

	private final Logger LOG = Logger.getLogger(Run.class.getName());

	public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
		System.setProperty(
				"java.util.logging.SimpleFormatter.format",
				"%1$tF %1$tT %4$s %2$s %5$s%6$s%n"
		);
		new Run().run();
	}

	public void run() throws IOException, URISyntaxException, InterruptedException {
		Path path = Paths.get("initial.avi");
		Files.deleteIfExists(path);

		Decoder decoder = new Decoder(getClass().getResourceAsStream("/battle.torrent"));
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
		peerConnection.receiveMessage(); // bitfield

		// TODO receiving an unchoke just after the bitfield
		//  could ignore it, show interest and let the block iteration retry until the download starts
		peerConnection.receiveMessage(); // unchoke
		peerConnection.showInterest();
		peerConnection.receiveMessage(); // unchoke

		//TODO number of pieces calculation should be included on the torrent class
		PiecesHashCalculator piecesHashCalculator = new PiecesHashCalculator(meta.fileLength, meta.pieceLength, meta.pieceHashes);

		piecesHashCalculator.pieces().forEach(p -> {
			LOG.info(format("Downloading piece: %d", p.id));
			peerConnection.download(p, path);
		});


		peerConnection.socket.close();
	}
}
