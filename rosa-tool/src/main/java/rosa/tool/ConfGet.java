package rosa.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import rosa.core.util.FileUtil;

/**
 * Awful hack to retrieve attachments from Confluence.
 */
public class ConfGet {
	private final String sessioncookie;
	private final String baseurl;

	public ConfGet(String baseurl, String sessioncookie) {
		this.baseurl = baseurl;
		this.sessioncookie = sessioncookie;
	}

	public String getPageHTML(String space, String page) throws IOException {
        URL url = new URL(baseurl + "/display/" + space + "/" + page);
        return getHTML(url);
	}

	private String getHTML(URL url) throws IOException {
		return FileUtil.readFull(new InputStreamReader(open(url), "UTF-8"))
				.toString();
	}

	private InputStream open(URL url) throws IOException {
		URLConnection con = url.openConnection();
		con.addRequestProperty("Cookie", sessioncookie);
		return con.getInputStream();
	}

	public void copyAttachments(String space, String page, String pattern,
			File destdir) throws IOException {
		// Find link to attachments

		String html = getPageHTML(space, page);
		int start = html.indexOf("/pages/viewpageattachments.action?pageId=");

		if (start == -1) {
			throw new IOException("Failed to parse html");
		}

		int end = html.indexOf('\"', start);

		if (end == -1) {
			throw new IOException("Failed to parse html");
		}

		html = getHTML(new URL(baseurl + html.substring(start, end)));
		int offset = 0;

		for (;;) {
			offset = html.indexOf("/download/attachments/", offset);

			if (offset == -1) {
				break;
			}

			end = html.indexOf('\"', offset);

			if (end == -1) {
				throw new IOException("Failed to parse html");
			}

			String path = html.substring(offset, end);
			offset = end;

			if (path.contains("version=")) {
				continue;
			}

			String name = path.substring(path.lastIndexOf('/') + 1);
			name = URLDecoder.decode(name, "UTF-8");

			if (pattern != null && !name.matches(pattern)) {
				System.out.println("Skipping " + name);
				continue;
			}

			System.out.println(name);

			try {
				FileUtil.copy(open(new URL(baseurl + path)),
						new FileOutputStream(new File(destdir, name)));
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}
}
