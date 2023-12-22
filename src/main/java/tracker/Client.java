package tracker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class Client {
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
