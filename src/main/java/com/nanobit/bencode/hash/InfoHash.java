package com.nanobit.bencode.hash;

import tracker.InfoHashUrlEncoder;

public class InfoHash {
	public final byte[] bencoded;
	public final String urlEncoded;

	public InfoHash(byte[] bencoded) {
		this.bencoded = bencoded;
		this.urlEncoded = InfoHashUrlEncoder.encode(HashCalculator.sha1Hex(this.bencoded));
	}
}
