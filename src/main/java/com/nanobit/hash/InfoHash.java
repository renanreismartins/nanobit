package com.nanobit.hash;

import com.nanobit.tracker.InfoHashUrlEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InfoHash {
	public final byte[] bencoded;
	public final byte[] sha1;
	public final String urlEncoded;

	public InfoHash(byte[] bencoded) {
		this.bencoded = bencoded;

		try {
			this.sha1 = MessageDigest.getInstance("SHA-1").digest(bencoded);
			this.urlEncoded = InfoHashUrlEncoder.encode(BytesToHex.transform(sha1));
		} catch (NoSuchAlgorithmException e) {
			//TODO Specific Ex
			throw new RuntimeException(e);
		}
	}
}
