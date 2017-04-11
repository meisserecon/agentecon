package com.agentecon.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class WebUtil {

	public static String readHttp(String address) throws IOException, InterruptedException {
		while (true) {
			try {
				URL url = new URL(address);
				URLConnection conn = url.openConnection();
				if (address.contains("https://api.github.com")) {
					// to allow requests at all
					conn.setRequestProperty("User-Agent", "agentecon.com");
					// to increase rate limit, see https://api.github.com/rate_limit?access_token=e255edf51ccbe47b4a3745710f3e4000b5a0192a
					conn.setRequestProperty("Authorization", "token e255edf51ccbe47b4a3745710f3e4000b5a0192a");
				}
				try (InputStream stream = conn.getInputStream()) {
					String first = new String(readData(stream));
					String next = conn.getHeaderField("Link");
					if (next != null) {
						return first + fetchNext(next);
					} else {
						return first;
					}
				}
			} catch (SocketTimeoutException e) {
				Thread.sleep(5000); // retry later
			}
		}
	}

	private static String fetchNext(String next) throws IOException, InterruptedException {
		if (next.contains("next")) {
			int open = next.indexOf('<');
			int close = next.indexOf('>');
			return readHttp(next.substring(open + 1, close));
		} else {
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
