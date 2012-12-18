package rosa.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manages files making up a manuscript archive.
 */
public class ManuscriptArchive extends BookArchive {
	public ManuscriptArchive(File dir) throws IOException {
		super(dir);
	}

	public boolean isFolioImage(String filename) {
		if (!filename.startsWith(id)) {
			return false;
		}

		String s = filename.substring(id.length() + 1);
		return s.matches("(\\d+)(r|v)\\.tif");
	}

	public boolean isTextTranscription(String filename) {
		return filename.contains(".transcription.")
				&& filename.endsWith(".txt");
	}

	public String textTranscriptionName(String masterimage) {
		return id + ".transcription"
				+ masterimage.substring(id.length(), masterimage.length() - 4)
				+ ".txt";
	}

	/**
	 * frontcover, pastedown, flyleaves, folios, flyleaves, pastedown, backcover
	 * Missing files have MISSING_IMAGE_PREFIX.
	 * 
	 * @param missing
	 *            If non-null, add missing images.
	 * @return
	 */
	public List<String> guessReadingOrder() {
		String frontcover = id + ".binding.frontcover.tif";
		String backcover = id + ".binding.backcover.tif";
		String frontpastedown = id + ".frontmatter.pastedown.tif";
		String endpastedown = id + ".endmatter.pastedown.tif";
		String frontflyleafprefix = id + ".frontmatter.flyleaf.";
		String endflyleafprefix = id + ".endmatter.flyleaf.";

		List<String> result = new ArrayList<String>();

		if (Arrays.binarySearch(filenames, frontcover) < 0) {
			result.add(BookArchive.MISSING_IMAGE_PREFIX + frontcover);
		} else {
			result.add(frontcover);
		}

		if (Arrays.binarySearch(filenames, frontpastedown) < 0) {
			result.add(BookArchive.MISSING_IMAGE_PREFIX + frontpastedown);
		} else {
			result.add(frontpastedown);
		}

		getImagesInMsOrder(result, frontflyleafprefix);

		// Flyleaves must end in v and have a 1r and 1v

		if (result.size() == 2) {
			result.add(BookArchive.MISSING_IMAGE_PREFIX + frontflyleafprefix
					+ "01r.tif");
			result.add(BookArchive.MISSING_IMAGE_PREFIX + frontflyleafprefix
					+ "01v.tif");
		} else if (result.size() > 2) {
			String last = result.get(result.size() - 1);

			if (!last.endsWith("v.tif")) {
				result.add(BookArchive.MISSING_IMAGE_PREFIX
						+ last.replace("r.tif", "v.tif"));
			}
		}

		getImagesInMsOrder(result, null);

		getImagesInMsOrder(result, endflyleafprefix);

		if (Arrays.binarySearch(filenames, endpastedown) < 0) {
			result.add(BookArchive.MISSING_IMAGE_PREFIX + endpastedown);

		} else {
			result.add(endpastedown);
		}

		if (Arrays.binarySearch(filenames, backcover) < 0) {
			result.add(BookArchive.MISSING_IMAGE_PREFIX + backcover);
		} else {
			result.add(backcover);
		}

		return result;
	}

	// prefix == null for folios
	private void getImagesInMsOrder(List<String> result, String prefix) {
		int nextseq = 1;
		char nextrv = 'r';

		for (int i = 0; i < filenames.length; i++) {
			String n = filenames[i];

			if (prefix == null || n.startsWith(prefix)) {
				String folio = findFolio(n);

				if (folio == null) {
					continue;
				}

				if (prefix == null && !isFolioImage(n)) {
					// only folios
					continue;
				}

				int seq = Integer.parseInt(folio.substring(0,
						folio.length() - 1));
				char rv = folio.charAt(folio.length() - 1);

				for (;;) {
					boolean found = (seq == nextseq && rv == nextrv);

					if (!found) {
						result.add(BookArchive.MISSING_IMAGE_PREFIX
								+ (prefix == null ? id + "." : prefix)
								+ toFolio(nextseq, nextrv) + ".tif");

					}

					if (nextrv == 'v') {
						nextseq++;
						nextrv = 'r';
					} else {
						nextrv = 'v';
					}

					if (found) {
						result.add(n);
						break;
					}
				}
			}
		}
	}

	public static String findFolio(String filename) {
		Pattern p = Pattern.compile("(\\d+)(r|v)");
		Matcher m = p.matcher(filename);

		if (m.find()) {
			int n = Integer.parseInt(m.group(1));
			return String.format("%03d", n) + m.group(2);
		} else {
			return null;
		}
	}

	public static String toFolio(int page, char rectoverso) {
		return String.format("%03d", page) + rectoverso;
	}

	public String firstExistingFolioImage() {
		for (String s : filenames) {
			if (isFolioImage(s)) {
				return s;
			}
		}

		return null;
	}

	public String lastExistingFolioImage() {
		for (int i = filenames.length - 1; i >= 0; i--) {
			if (isFolioImage(filenames[i])) {
				return filenames[i];
			}
		}

		return null;
	}
}
