package tracker;

import com.nanobit.bencode.Decoder;
import com.nanobit.bencode.value.BencodedString;
import com.nanobit.bencode.value.BencodedValue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.SECONDS;

public class Client {
	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		byte[] response = new Client().some(
				"http://tracker.publicdomaintorrents.com:6969/announce",
				"%f0%2a%8a%35%10%3b%8d%e2%e3%05%27%33%80%73%4f%91%c5%e8%f4%79",
				0,
				0,
				735261618,
				6868,
				"00112233445566778899"
		);
		System.out.println(new String(response, UTF_8));

		Decoder d = new Decoder(new ByteArrayInputStream(response));
		Map<BencodedString, BencodedValue> map = d.decode().asMap();
		System.out.println(map);

//		String response = new Client().some(
//				"https://torrent.ubuntu.com/announce?",
//				"%9e%cd%46%76%fd%0f%04%74%15%1a%4b%74%a5%95%8f%42%63%9c%eb%df",
//				0,
//				0,
//				879028224,
//				80,
//				"00112233445566778899"
//		);
//		System.out.println(response);
//
//		Decoder d = new Decoder(new ByteArrayInputStream(response.getBytes(UTF_8)));
//		Map<BencodedString, BencodedValue> map = d.decode().asMap();
//		System.out.println(map);
	}

	public byte[] some(
			String announceUrl,
			String encodedInfoHash,
			int downloaded,
			int uploaded,
			int left,
			int port,
			String peerId
	) throws URISyntaxException, IOException, InterruptedException {
		port = 6868;

		//TODO Some files send 301 code and this client does not redirect, throwing
		// an error instead, that is why this url is hardcoded, fix accepting redirects.
		String url = new StringBuilder("http://tracker.publicdomaintorrents.com:6969/announce")
				.append("?info_hash=%s")
				.append("&port=%d")
				.append("&downloaded=%d")
				.append("&uploaded=%d")
				.append("&left=%d")
				.append("&peer_id=%s")
				.toString()
				.formatted(encodedInfoHash, port, downloaded, uploaded, left, peerId);

		System.out.println(url);

		HttpRequest request = HttpRequest.newBuilder()
				.uri(new URI(url))
				.timeout(Duration.of(10, SECONDS))
				.GET()
				.build();

		HttpResponse<byte[]> send = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
		return send.body();
	}
}
