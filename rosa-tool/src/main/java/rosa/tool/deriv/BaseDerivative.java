package rosa.tool.deriv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rosa.core.BookArchive;
import rosa.core.ManuscriptArchive;
import rosa.tool.ByteArray;
import rosa.tool.Config;
import rosa.core.util.FileUtil;

/**
 * Basic derivatives dealing with the integrity of the files.
 */
public class BaseDerivative extends Derivative {

	public boolean check() {
		boolean success = super.check();

		return success;
	}

	public static final String NAME = "base";

	public BaseDerivative(Config site, PrintStream report) throws IOException {
		super(site, report);
	}

	public boolean update(BookArchive archive, boolean force) {
		boolean success = true;

		report.println(archive.id());

		if (!archive.exists(archive.imagesName())) {
			report.println("No images information. Guessing order and calculating dimensions.");

			try {
				guessAndWriteImageSequence(archive, report);
			} catch (IOException e) {
				reportError("Guessing order error", e);
			}
		}

		try {
			updateChecksums(archive, force);
		} catch (IOException e) {
			reportError("Updating checksums", e);
			success = false;
		}

		return success;
	}

	private static int[] getImageDimensionsHack(String path) throws IOException {
		String[] cmd = new String[] { "identify", "-ping", "-format", "%w %h ",
				path + "[0]" };

		Process p = Runtime.getRuntime().exec(cmd);

		try {
			if (p.waitFor() != 0) {
				ByteArray buf = new ByteArray(1024);
				buf.append(p.getErrorStream());
				String err = new String(buf.array, 0, buf.length, "UTF-8");

				throw new IOException("Failed to run on " + path + ": " + err);

			}

			ByteArray buf = new ByteArray(1024);
			buf.append(p.getInputStream());

			String result = new String(buf.array, 0, buf.length, "UTF-8");
			String[] s = result.trim().split("\\s+");

			if (s.length != 2) {
				throw new IOException("Invalid result " + result + " on "
						+ path);
			}

			return new int[] { Integer.parseInt(s[0]),
					Integer.parseInt(s[1].trim()) };
		} catch (NumberFormatException e) {
			throw new IOException("Invalid result.");
		} catch (InterruptedException e) {
			throw new IOException(e);
		} finally {
			p.destroy();
		}
	}

	public static void guessAndWriteImageSequence(BookArchive archive,
			PrintStream report) throws IOException {
		PrintStream out = new PrintStream(new File(archive.dir(),
				archive.imagesName()));

		if (archive instanceof ManuscriptArchive) {
			ManuscriptArchive msarchive = (ManuscriptArchive) archive;
			List<String> ordered = msarchive.guessReadingOrder();

			for (String filename : ordered) {
				out.print(filename);

				if (!filename.startsWith(BookArchive.MISSING_IMAGE_PREFIX)) {
					int[] size = getImageDimensionsHack(new File(archive.dir(),
							filename).getPath());

					out.println("," + size[0] + "," + size[1]);
				}
			}

			for (String filename : archive.filenames()) {
				if (archive.isImage(filename) && !ordered.contains(filename)) {
					out.print(filename);
					int[] size = getImageDimensionsHack(new File(archive.dir(),
							filename).getPath());
					out.println("," + size[0] + "," + size[1]);
				}
			}
		} else {
			for (String filename : archive.filenames()) {
				if (archive.isImage(filename)) {
					out.print(filename);
					int[] size = getImageDimensionsHack(new File(archive.dir(),
							filename).getPath());
					out.println("," + size[0] + "," + size[1]);
				}
			}
		}

		out.close();
	}

	public boolean check(BookArchive archive) {
		boolean success = checkFilenames(archive);

		for (String lc : col.languages()) {
			if (!archive.exists(archive.descriptionName(lc))) {
				report.println("Missing " + archive.descriptionName(lc));
				success = false;
			}

			if (!archive.exists(archive.permissionName(lc))) {
				report.println("Missing " + archive.permissionName(lc));
				success = false;
			}
		}

		if (!archive.exists(archive.sha1sumName())) {
			report.println("Missing " + archive.sha1sumName());
			success = false;
		}

		if (!checkImageSequenceFile(archive)) {
			success = false;
		}

		return success;
	}

	private boolean checkImageSequenceFile(BookArchive archive) {
		try {
			List<BookArchive.Image> images = archive.images();

			boolean success = true;
			Set<String> names = new HashSet<String>();

			for (BookArchive.Image image : images) {
				if (image.missing()) {
					names.add(image.fileName());

					if (archive.exists(image.fileName())) {
						report.println("File marked as missing actually exists: "
								+ image.fileName());
						success = false;
					}
				} else {
					names.add(image.fileName());

					if (!archive.exists(image.fileName())) {
						report.println("No such file: " + image.fileName());
						success = false;
					}
				}
			}

			for (String image : archive.filenames()) {
				if (archive.isImage(image)) {
					if (!names.contains(image)) {
						report.println("Image not ordered: " + image);
						success = false;
					}
				}
			}

			return success;
		} catch (IOException e) {
			reportError("Loading image order", e);
		}

		return false;
	}

	public boolean validate(BookArchive archive) {
		boolean success = check(archive);

		if (!validateChecksums(archive)) {
			success = false;
		}

		return success;
	}

	// filename -> sha1sum
	private Map<String, String> loadChecksums(BookArchive archive)
			throws IOException {
		File sha1sum = new File(archive.dir(), archive.sha1sumName());
		Map<String, String> checksums = new HashMap<String, String>();

		if (sha1sum.exists()) {
			BufferedReader in = new BufferedReader(new FileReader(sha1sum));

			String line = null;
			while ((line = in.readLine()) != null) {
				int i = line.indexOf(' ');

				if (i == -1) {
				    in.close();
					throw new IOException("Malformed checksum entry: " + line);
				}

				String cs = line.substring(0, i).trim();
				String filename = line.substring(i + 1).trim();

				checksums.put(filename, cs);
			}

			in.close();
		}

		return checksums;
	}

	// Write in sha1sum format
	private void writeChecksums(BookArchive archive,
			Map<String, String> checksums) throws IOException {
		PrintStream out = new PrintStream(new File(archive.dir(),
				archive.sha1sumName()));

		for (String filename : checksums.keySet()) {
			String cs = checksums.get(filename);

			if (archive.exists(filename)) {
				out.println(normalizeSHA1SUM(cs) + "  " + filename);
			}
		}

		out.close();
	}

	// Add leading 0 to make sure length is always 40

	private String normalizeSHA1SUM(String cs) {
		if (cs.length() < 40) {
			char[] prefix = new char[40 - cs.length()];
			Arrays.fill(prefix, '0');
			cs = new String(prefix) + cs;
		}

		return cs;
	}

	private void updateChecksums(BookArchive archive, boolean force)
			throws IOException {
		Map<String, String> checksums = loadChecksums(archive);
		long checksumslastmod = new File(archive.dir(), archive.sha1sumName())
				.lastModified();

		report.println("Updating checksums");

		for (String filename : archive.filenames()) {
			if (filename.equals(archive.sha1sumName())) {
				continue;
			}

			String cs = checksums.get(filename);
			File file = new File(archive.dir(), filename);

			if (force || cs == null || file.lastModified() > checksumslastmod) {
				report.println(file.getName() + "...");
				cs = FileUtil.sha1sum(file);
				checksums.put(filename, cs);
			}
		}

		writeChecksums(archive, checksums);
	}

	private boolean checkFilenames(BookArchive archive) {
		boolean success = true;

		// check naming conventions

		for (String s : archive.filenames()) {
			if (!s.startsWith(archive.id() + ".")) {
				report.println("File does not start with ms name: " + s);
				success = false;
			}

			if (!s.endsWith(".xml") && !s.endsWith(".txt")
					&& !s.endsWith(".csv")
					&& !s.endsWith(BookArchive.IMAGE_EXT) && !s.endsWith("~")
					&& !s.equals(archive.sha1sumName())
					&& !s.equals(archive.permissionName("en"))
					&& !s.equals(archive.permissionName("fr"))
					&& !s.equals(archive.narrativeTaggingByHumanName())
					&& !s.equals(archive.narrativeTaggingName())
					&& !s.equals(archive.bnfFileMapName())
					&& !s.equals(archive.bnfMD5SUMName())
					&& !s.equals(archive.imageTaggingName())) {
				report.println("Unknown file: " + s);
				success = false;
			}
		}

		return success;
	}

	private boolean validateChecksums(BookArchive archive) {
		Map<String, String> checksums;

		try {
			checksums = loadChecksums(archive);
		} catch (IOException e) {
			report.println("Loading checksums failed: " + e);
			return false;
		}

		boolean success = true;

		for (String filename : archive.filenames()) {
			if (filename.equals(archive.sha1sumName())) {
				continue;
			}
			

			String cs = checksums.get(filename);

			if (cs == null) {
				report.println("Checksum missing: " + filename);
			} else {
				report.println("Validating checksum: " + filename);

				String testcs;

				try {
					testcs = FileUtil
							.sha1sum(new File(archive.dir(), filename));
				} catch (IOException e) {
					report.println("Exception doing checksum on " + filename
							+ ": " + e);
					return false;
				}

				testcs = normalizeSHA1SUM(testcs);

				if (!testcs.equals(cs)) {
					report.println("Checksum failed: " + filename);
					success = false;
				}
			}
		}

		return success;
	}

	public String name() {
		return NAME;
	}
}
