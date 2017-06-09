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

import com.agentecon.util.IOUtils;

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
					content += new String(IOUtils.readData(stream));
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

}
