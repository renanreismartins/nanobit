package tracker;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InfoHashUrlEncoder {
	/**
	 * Returns an Url Encoded representation of the info hash.
	 * 'Info' is the info section of the bencode file, a SHA1 hash
	 * (byte[]) is created from its encoded representation (see HashCalculator#infoHash, BencodedValue#encode),
	 * then transformed in a hex string of 40 characters.
	 *
	 * This class transform the hex into its url encoded version (80 characters).
	 * It is known that some values do not need to be encoded, creating a smaller string, but this
	 * suffices for the moment.
	 *
	 *
	 * @param      infoHash   hex representation of the SHA1 encoded info section of the torrent file.
	 * @return     the url encoded infoHash
	 */
	public static String encode(String infoHash) {
		return IntStream.range(0, infoHash.length() / 2)
				.mapToObj(i -> "%" + infoHash.substring(2 * i, 2 * i + 2))
				.collect(Collectors.joining());
	}
}
