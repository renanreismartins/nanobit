package com.nanobit.bencode.peer;

import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.value.BencodedMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class PeerTest {

	/*
	72.21.17.5, 42546
	152.117.104.8, 48683
	76.182.68.238, 8999
	 */
	@Test
	public void doit() throws IOException {
		Decoder decoder = new Decoder(getClass().getResourceAsStream("/boy.torrent"));
		BencodedMap dict = (BencodedMap) decoder.decode();
		Peer peer = new Peer("76.182.68.238", 8999, "00112233445566778899");
		peer.connect();
		peer.handshake(dict.get("info").encode());
	}

}