package com.nanobit.bencode.hash;

public class InfoHash {
	public final byte[] bencoded;
	public final String hex;

	public InfoHash(byte[] bencoded) {
		this.bencoded = bencoded;
		this.hex = HashCalculator.infoHash(this.bencoded);
	}
}
