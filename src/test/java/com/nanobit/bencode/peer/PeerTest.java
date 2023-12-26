package com.nanobit.bencode.peer;

import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.TorrentMetadata;
import com.nanobit.peer.Peer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

class PeerTest {

	/*
	72.21.17.5, 42546
	152.117.104.8, 48683
	76.182.68.238, 8999
	 */
	@Test
	public void doit() throws IOException, URISyntaxException {
		Decoder decoder = new Decoder(getClass().getResourceAsStream("/boy.torrent"));
		TorrentMetadata meta = new TorrentMetadata(decoder.decodeMap());
		Peer peer = new Peer("76.182.68.238", 8999, "00112233445566778899", meta.infoHash);
		peer.connect();
		peer.handshake();
	}

}