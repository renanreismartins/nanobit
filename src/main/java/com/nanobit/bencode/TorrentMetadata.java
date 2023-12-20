package com.nanobit.bencode;

import com.nanobit.bencode.value.BencodedMap;
import com.nanobit.bencode.value.BencodedString;

import java.net.URI;
import java.net.URISyntaxException;

public class TorrentMetadata {
	private static final String INFO = "info";
	private static final String LENGTH = "length";
	private static final String PIECE_LENGTH = "piece length";
	private static final String PIECES = "pieces";
	public final URI announce;
	public final int fileLength;
	public final int pieceLength;
	public final byte[] encodedInfoHash;
	public final byte[] pieceHashes;

	public TorrentMetadata(BencodedMap decoded) throws URISyntaxException {
		announce = new URI(decoded.get("announce").asString());

		BencodedMap info = (BencodedMap) decoded.get(INFO);
		fileLength = info.get(LENGTH).asInteger();
		pieceLength = info.get(PIECE_LENGTH).asInteger();
		encodedInfoHash = decoded.get("info").encode();
		pieceHashes = ((BencodedString) info.get(PIECES)).value;
	}
}
