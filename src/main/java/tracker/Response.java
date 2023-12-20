package tracker;

import com.nanobit.bencode.peer.Peer;
import com.nanobit.bencode.value.BencodedMap;

import java.util.List;

public class Response {
	public static final String IP = "ip";
	public static final String PORT = "port";
	public static final String PEER_ID = "peer id";
	public final int interval;
	public final List<Peer> peers;

	public Response(BencodedMap response) {
		interval = response.get("interval").asInteger();
		peers = response.get("peers")
				.asList()
				.stream()
				.map(d -> {
					BencodedMap decodedPeer = ((BencodedMap) d);
					return new Peer(
							decodedPeer.get(IP).asString(),
							decodedPeer.get(PORT).asInteger(),
							decodedPeer.get(PEER_ID).asString()
					);
				})
				.toList();
	}

	//TODO to be removed when able to connect to peers behind NATs
	public Peer findPeerByIp(String ip) {
		return peers.stream()
				.filter(p -> ip.equals(p.ip))
				.findAny()
				.orElseThrow();
	}

	@Override
	public String toString() {
		return "Response{" +
				"interval=" + interval +
				", peers=" + peers +
				'}';
	}
}
