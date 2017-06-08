package com.agentecon.github;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class WebUtil {

	private final static String API_ADDRESS = "https://api.github.com";
	private final static String ACCESS_SECRETS = loadSecrets();

	public static String readHttp(String address) throws IOException, InterruptedException {
		if (address.contains(API_ADDRESS)) {
			address += ACCESS_SECRETS;
		}
		String content = "";
		String nextPage = null;
		while (address != null) {
			long t0 = System.nanoTime();
			try {
				URL url = new URL(address);
				URLConnection conn = url.openConnection();
				InputStream stream = conn.getInputStream();
				try {
					content += new String(readData(stream));
					nextPage = getNextPageUrl(conn);
				} finally {
					stream.close();
				}
			} catch (SocketTimeoutException e) {
				Thread.sleep(5000); // retry later
			}
			long t1 = System.nanoTime();
			System.out.println((t1 - t0) / 1000000 + "ms spent reading " + address);
			address = nextPage;
		}
		return content;
	}

	protected static String getNextPageUrl(URLConnection conn) {
		String next = conn.getHeaderField("Link");
		if (next != null && next.contains("next")) {
			int open = next.indexOf('<');
			int close = next.indexOf('>');
			next = next.substring(open + 1, close);
		} else {
			next = null;
		}
		return next;
	}

	private static String loadSecrets() {
		Path path = FileSystems.getDefault().getPath("..", "github-secret.txt");
		try {
			return Files.readAllLines(path).get(0);
		} catch (IOException e) {
			return "";
		}
	}

	public static String extract(String content, String what, int startPos) {
		String item = "\"" + what + "\":\"";
		int pos2 = content.indexOf(item, startPos);
		if (pos2 >= 0) {
			int urlEnd = content.indexOf('"', pos2 + item.length());
			return content.substring(pos2 + item.length(), urlEnd);
		} else {
			return null;
		}
	}

	public static byte[] readData(InputStream source) throws IOException {
		int ava = source.available();
		int size = ava == 0 ? 1000 : ava;
		ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		byte[] buffer = new byte[size];
		while (true) {
			int read = source.read(buffer, 0, buffer.length);
			if (read > 0) {
				out.write(buffer, 0, read);
			} else {
				assert read == -1;
				break;
			}
		}
		return out.toByteArray();
	}

	public static byte[] readData(int size, InputStream source) throws IOException {
		byte[] data = new byte[size];
		int pos = 0;
		while (pos < size) {
			int read = source.read(data, pos, size - pos);
			if (read == -1) {
				throw new RuntimeException("Early end of entry");
			} else {
				pos += read;
			}
		}
		return data;
	}

	public static byte[] readData(URL url) throws SocketTimeoutException, IOException {
		try (InputStream input = url.openStream()) {
			return readData(input);
		}
	}

}
