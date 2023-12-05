package com.nanobit.bencode;

import java.io.IOException;
import java.io.InputStream;

public class BEncodeStream {
	private final InputStream in;
	
	private int position;

	private int markedPosition;

	public BEncodeStream(InputStream in) {
		this.in = in;
		this.position = 0;
		this.markedPosition = -1;
	}

	public void skip(int i) throws IOException {
		position++;
		in.skip(i);
	}

	public int read() throws IOException {
		position++;
		return in.read();
	}

	public int available() throws IOException {
		return in.available();
	}

	public byte[] readNBytes(int len) throws IOException {
		return in.readNBytes(len);
	}

	public void mark(int readLimit) {
		markedPosition = position;
		in.mark(readLimit);
	}

	public void reset() throws IOException {
		position = markedPosition;
		markedPosition = -1;
		in.reset();
	}

	public int position() {
		return position;
	}
}
