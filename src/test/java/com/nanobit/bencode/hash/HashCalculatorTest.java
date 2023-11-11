package com.nanobit.bencode.hash;

import com.nanobit.bencode.hash.HashCalculator;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashCalculatorTest {
	@Test
	void calculateInfoHash() throws IOException, NoSuchAlgorithmException {
		byte[] info = Files.readAllBytes(Paths.get("src/test/resources/info.hash"));
		assertEquals("d69f91e6b2ae4c542468d1073a71d4ea13879a7f", HashCalculator.infoHash(info));
	}
}
