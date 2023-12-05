import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.hash.HashCalculator;
import com.nanobit.bencode.peer.Peer;
import com.nanobit.bencode.value.BencodedList;
import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;
import tracker.Client;
import tracker.InfoHashUrlEncoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Run {


	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException {
		new Run().run();
	}

	public void run() throws IOException, NoSuchAlgorithmException, URISyntaxException, InterruptedException {
		Decoder decoder = new Decoder(getClass().getResourceAsStream("/boy.torrent"));
		BencodedMap dict = (BencodedMap) decoder.decode();
		String announce = dict.get("announce").asString();
		String urlEncodedInfoHash = InfoHashUrlEncoder.encode(HashCalculator.infoHash(dict.get("info").encode()));

		byte[] trackerResponse = new Client().some(
				announce,
				urlEncodedInfoHash,
				0,
				0,
				735261618,
				6868,
				"00112233445566778899"
		);

		Decoder d = new Decoder(new ByteArrayInputStream(trackerResponse));
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
		peerConnection.handshake(dict.get("info").encode());


		final int BLOCK_SIZE = 16 * 1024;
		byte[] pieces = dict.asMap().get(new BencodedString("info")).asMap().get(new BencodedString("pieces")).encode();
		byte[] block = Arrays.copyOfRange(pieces, 0, BLOCK_SIZE);


		//peerConnection.sendRequest(0, 0, BLOCK_SIZE);


	}
}
