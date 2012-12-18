package rosa.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import rosa.core.util.CSV;

public abstract class BookArchive {
	public static final String IMAGE_EXT = ".tif";
	public static final String MISSING_IMAGE_PREFIX = "*";

	protected final File dir;
	protected final String id;
	protected final String[] filenames;

	/**
	 * @param filename
	 * @return book id of filename
	 */
	public static String getID(String filename) {
		int i = filename.indexOf('.');

		if (i == -1) {
			return null;
		}

		return filename.substring(0, i);
	}

	/**
	 * @return filename without extension and book id
	 */
	public static String getName(String filename) {
		int start = filename.indexOf('.');

		if (start == -1) {
			return null;
		}

		int end = filename.lastIndexOf('.');

		if (end == -1 || start == end) {
			return null;
		}

		return filename.substring(start + 1, end);
	}

	// Return image filename or null if can't find matching filename
	public String guessImageName(String frag) {
		frag = frag.trim();

		if (frag.matches("\\d+")) {
			frag += "r";
		}

		if (frag.matches("\\d[rRvV]")) {
			frag = "00" + frag;
		} else if (frag.matches("\\d\\d[rRvV]")) {
			frag = "0" + frag;
		}

		if (!frag.endsWith(".tif")) {
			frag += ".tif";
		}

		if (!frag.startsWith(id)) {
			frag = id + "." + frag;
		}

		for (String s : filenames) {
			if (s.equalsIgnoreCase(frag)) {
				return s;
			}
		}

		return null;
	}

	protected BookArchive(File dir) throws IOException {
		this.dir = dir;

		this.filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}
		});

		if (filenames == null || filenames.length == 0) {
			throw new IOException("No files in " + dir);
		}

		this.id = getID(filenames[0]);

		if (id == null) {
			throw new IOException("File does not have id " + filenames[0]);
		}

		Arrays.sort(filenames);
	}

	public String permissionName(String lc) {
		return id + ".permission_" + lc + ".html";
	}

	public String descriptionName(String lc) {
		return id + ".description_" + lc + ".xml";
	}

	public String imageTaggingName() {
		return id + ".imagetag.csv";
	}

	public String transcriptionName() {
		return id + ".transcription.xml";
	}

	public String sha1sumName() {
		return id + ".SHA1SUM";
	}

	public boolean exists(String filename) {
		return new File(dir, filename).exists();
	}

	public String id() {
		return id;
	}

	public File dir() {
		return dir;
	}

	public BookDescription description(String lc) throws IOException,
			SAXException {
		File file = new File(dir, descriptionName(lc));

		if (!file.exists()) {
			return null;
		}

		return new BookDescription(file);
	}

	public ReducedTagging reducedTagging(List<String> errors)
			throws IOException {
		File file = new File(dir, reducedTaggingName());

		if (!file.exists()) {
			return null;
		}

		return new ReducedTagging(new FileReader(file), errors);
	}

	public NarrativeMapping narrativeTaggingByHuman(List<String> errors)
			throws IOException {
		File file = new File(dir, narrativeTaggingByHumanName());

		if (!file.exists()) {
			return null;
		}

		return new NarrativeMapping(file, true, errors);
	}

	public NarrativeMapping narrativeTagging(List<String> errors)
			throws IOException {
		File file = new File(dir, narrativeTaggingName());

		if (!file.exists() || file.length() == 0) {
			return null;
		}

		return new NarrativeMapping(file, false, errors);
	}

	public ImageTagging imageTagging(List<String> errors) throws IOException {
		File file = new File(dir, imageTaggingName());

		if (!file.exists() || file.length() == 0) {
			return null;
		}

		return new ImageTagging(file, errors);
	}

	public boolean isImage(String filename) {
		return filename.endsWith(IMAGE_EXT);
	}

	public static boolean isMissing(String filename) {
		return filename.startsWith(MISSING_IMAGE_PREFIX);
	}

	public String imagesName() {
		return id + ".images.csv";
	}

	public String croppedImagesName() {
		return id + "images.crop.csv";
	}

	public String cropdataName() {
		return id + ".crop.txt";
	}

	public String reducedTaggingName() {
		return id + ".redtag.txt";
	}

	public String narrativeTaggingByHumanName() {
		return id + ".nartag.txt";
	}

	public String narrativeTaggingName() {
		return id + ".nartag.csv";
	}

	public String bnfFileMapName() {
		return id + ".bnf.filemap.csv";
	}

	public String bibliographyName() {
		return id + ".bibliography.xml";
	}

	public String bnfMD5SUMName() {
		return id + ".bnf.MD5SUM";
	}

	/**
	 * Return list of images in reading order. The first image will be the front
	 * cover. After the back cover will be misc images.
	 * 
	 * @return
	 * @throws IOException
	 */
	public List<Image> images() throws IOException {
		return loadImageSequence(new File(dir, imagesName()));
	}

	private List<Image> loadImageSequence(File file) throws IOException {
		List<Image> result = new ArrayList<BookArchive.Image>();

		FileReader in = new FileReader(file);
		String[][] table = CSV.parseTable(in);
		in.close();

		for (int i = 0; i < table.length; i++) {
			String filename = table[i][0];

			boolean missing = false;
			
			if (filename.startsWith(MISSING_IMAGE_PREFIX)) {
				filename = filename.substring(MISSING_IMAGE_PREFIX.length());
				missing = true;
			}

			int width = 0;
			int height = 0;

			if (table[i].length > 1) {
				width = Integer.parseInt(table[i][1]);
				height = Integer.parseInt(table[i][2]);
			}

			result.add(new Image(filename, width, height, missing));
		}

		return result;
	}

	public List<Image> croppedImages() throws IOException {
		return loadImageSequence(new File(dir, croppedImagesName()));
	}

	public Image firstExistingImage() throws IOException {
		for (Image image : images()) {
			if (!image.missing()) {
				return image;
			}
		}

		return null;
	}

	/**
	 * Return map from image file names to [left, right, top, botton]. Each of
	 * the numbers is a percentage between 0 and 1 to crop starting from that
	 * side. Return null if no cropping data.
	 * 
	 * @throws IOException
	 */
	public Map<String, double[]> getCroppingData() throws IOException {
		File f = new File(dir, cropdataName());

		if (!f.exists()) {
			return null;
		}

		BufferedReader in = new BufferedReader(new FileReader(f));
		Map<String, double[]> result = new HashMap<String, double[]>();

		String line = null;
		while ((line = in.readLine()) != null) {
			line = line.trim();

			if (line.isEmpty()) {
				continue;
			}

			String[] parts = line.split("\\s+");

			if (parts.length != 5) {
				throw new IOException("Malformed line " + line);
			}

			double[] data = new double[4];

			for (int i = 0; i < data.length; i++) {
				data[i] = Double.parseDouble(parts[i + 1]);
			}

			if (result.containsKey(parts[0])) {
				throw new IOException("Image appears more than once "
						+ parts[0]);
			}

			result.put(parts[0], data);
		}

		in.close();

		return result;
	}

	/**
	 * @return file names in sorted order
	 */
	public String[] filenames() {
		return filenames;
	}

	public File cropDir() {
		return new File(dir, "cropped");
	}

	public boolean isCropped() {
		return cropDir().exists();
	}

	public static class Image {
		private Image(String filename, int width, int height, boolean missing) {
			this.filename = filename;
			this.width = width;
			this.height = height;
			this.missing = missing;
		}

		private final String filename;
		private final int width;
		private final int height;
		private final boolean missing;

		public String fileName() {
			return filename;
		}

		public int width() {
			return width;
		}

		public int height() {
			return height;
		}

		public boolean missing() {
			return missing;
		}
	}
}
