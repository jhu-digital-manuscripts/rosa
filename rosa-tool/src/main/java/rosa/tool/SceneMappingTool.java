package rosa.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rosa.core.util.XMLUtil;

// TODO cruft, rescue transcription work?

public class SceneMappingTool {
	public static class Scene {
		public final String id;
		public final int relative_line_start;
		public final int relative_line_end;
		public final int lecoy_start;
		public final int lecoy_end;
		public final String desc;

		private Scene(Matcher m) {
			this.id = m.group(1);
			this.relative_line_start = Integer.parseInt(m.group(2));
			this.relative_line_end = Integer.parseInt(m.group(3));
			this.lecoy_start = Integer.parseInt(m.group(4));
			this.lecoy_end = Integer.parseInt(m.group(5));
			this.desc = m.group(6);
		}
	}

	public static class ManuscriptInstance {
		public final int lines_per_column;
		public final int lines_per_miniature;
		public final FolioData[] foliodata;

		public ManuscriptInstance(File datafile) throws IOException {
			BufferedReader in = new BufferedReader(new FileReader(datafile));
			String line = null;

			line = in.readLine().trim();
			String[] parts = line.split("\\s+");

			this.lines_per_column = Integer.parseInt(parts[0]);
			this.lines_per_miniature = Integer.parseInt(parts[1]);

			Set<String> foliocols = new HashSet<String>();

			ArrayList<FolioData> foliodatalist = new ArrayList<FolioData>();

			while ((line = in.readLine()) != null) {
				line = line.trim();

				if (line.length() == 0) {
					continue;
				} else {
					parts = line.split("\\s+");

					FolioData data = new FolioData(parts[0], Integer
							.parseInt(parts[1]), Integer.parseInt(parts[2]),
							parts.length == 3 ? -1 : Integer.parseInt(parts[3]));

					if (!data.foliocol.equals("*")
							&& foliocols.contains(data.foliocol)) {
						System.err.println("Repeated " + data.foliocol);
					}

					foliocols.add(data.foliocol);

					foliodatalist.add(data);
				}
			}

			in.close();
			
			foliodata = foliodatalist.toArray(new FolioData[] {});
		}
	}

	public static class FolioData {
		public final String foliocol;
		public final int num_miniatures;
		public final int num_rubrics;
		public final int lecoy_start; // -1 for doesn't exist

		public FolioData(String foliocol, int num_miniatures, int num_rubrics,
				int lecoy_num) {
			this.foliocol = foliocol;
			this.num_miniatures = num_miniatures;
			this.num_rubrics = num_rubrics;
			this.lecoy_start = lecoy_num;
		}
	}

	public static class ManuscriptScene {
		public final String sceneid;

		public final String foliocol_start;
		public final String foliocol_end;

		public ManuscriptScene(String sceneid, String foliocol_start,
				String foliocol_end) {
			this.foliocol_end = foliocol_end;
			this.foliocol_start = foliocol_start;
			this.sceneid = sceneid;
		}
	}

	public static List<Scene> loadSceneList(File scenesfile) throws IOException {
		List<Scene> scenes = new ArrayList<Scene>();

		BufferedReader in = new BufferedReader(new FileReader(scenesfile));

		String line = null;

		Pattern p1 = Pattern
				.compile("([GJ][\\w\\d\\.]+)\\s+(\\d+)\\s*-\\s*(\\d+)\\s*\\(Lecoy,\\s*(\\d+)\\s*[–-]\\s*(\\d+)\\s*\\)\\s*[–-](.*)");
		int n = 0;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			n++;
			if (line.length() == 0) {
				continue;
			}

			Matcher m = p1.matcher(line);

			if (!m.matches()) {
				System.err.println("Unable to parse line " + n + ": " + line);
			}

			scenes.add(new Scene(m));
		}

		in.close();

		return scenes;
	}

	public static List<ManuscriptScene> loadManuscriptSceneList(File datafile)
			throws IOException {
		List<ManuscriptScene> scenes = new ArrayList<ManuscriptScene>();

		BufferedReader in = new BufferedReader(new FileReader(datafile));

		String line = null;

		while ((line = in.readLine()) != null) {
			line = line.trim();

			if (line.length() == 0) {
				continue;
			}

			String[] parts = line.split("\\s+");

			scenes.add(new ManuscriptScene(parts[0], parts[1], parts[2]));
		}

		in.close();

		return scenes;
	}

	public static void generateStatsFromTranscription(String msname,
			File fedoradata) throws IOException, SAXException {
		File dir = new File(fedoradata, msname);

		System.out
				.println("\"folio\", \"first_lecoy\", \"last_lecoy\", \"first_logical\", \"last_logical\", \"num_lecoy\", \"num_line\", \"num_miniatures\"");

		String[] filenames = dir.list();
		Arrays.sort(filenames);

		for (String filename : filenames) {
			if (!filename.contains("transcription")) {
				continue;
			}

			String folio = filename.substring(msname.length()
					+ ".transcription.".length(), filename.length() - 4);
			int num_lines = 0;
			int num_lecoy = 0;
			int num_miniatures = 0;
			String first_lecoy = "";
			String last_lecoy = "";
			int first_logical = -1;
			int last_logical = -1;

			Document doc = XMLUtil.createDocument(new File(dir, filename));
			NodeList list = doc.getDocumentElement().getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {
				Node n = list.item(i);

				if (n.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element e = (Element) n;
				String name = n.getNodeName();

				if (name.equals("lg")) {
					NodeList lines = e.getElementsByTagName("l");
					num_lines += lines.getLength();

					if (lines.getLength() > 0) {
						try {
							if (first_logical == -1) {
								first_logical = Integer
										.parseInt(((Element) lines.item(0))
												.getAttribute("n"));
							}

							last_logical = Integer.parseInt(((Element) lines
									.item(lines.getLength() - 1))
									.getAttribute("n"));
						} catch (NumberFormatException ex) {
							System.err
									.println("Warning: Not a number when parse l n attribute: "
											+ ex);
						}
					}

					NodeList milestones = e.getElementsByTagName("milestone");
					num_lecoy += milestones.getLength();

					for (int j = 0; j < milestones.getLength(); j++) {
						Element milestone = (Element) milestones.item(j);

						if (first_lecoy.length() == 0) {
							first_lecoy = milestone.getAttribute("n");
						}

						last_lecoy = milestone.getAttribute("n");
					}
				} else if (name.equals("div") && e.hasAttribute("type")
						&& e.getAttribute("type").equals("miniature")) {
					num_miniatures++;
				}
			}

			System.out.println("\"" + folio + "\", \"" + first_lecoy + "\", \""
					+ last_lecoy + "\", " + first_logical + ", " + last_logical
					+ ", " + num_lecoy + ", " + num_lines + ", "
					+ num_miniatures);
		}
	}

	public static void generateInstanceTranscripts(String msname,
			File fedoradatadir) throws IOException, SAXException {
		File dir = new File(fedoradatadir, msname);
		String[] filenames = dir.list();
		Arrays.sort(filenames);

		int lines_per_miniatures = 6;
		int lines_per_column = 36;

		System.err.println("Edit lines_per_column and lines_per_miniature");
		System.out.println(lines_per_column + " " + lines_per_miniatures);

		for (String filename : filenames) {
			if (!filename.contains("transcription")) {
				continue;
			}
			String folio = filename.substring(msname.length()
					+ ".transcription.".length(), filename.length() - 4);

			Document doc = XMLUtil.createDocument(new File(dir, filename));
			NodeList top = doc.getDocumentElement().getChildNodes();
			int num_miniatures = 0;
			int num_rubrics = 0;
			String col = null;

			for (int i = 0; i < top.getLength(); i++) {
				if (top.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element e = (Element) top.item(i);

				if (e.getNodeName().equals("div")) {
					num_miniatures++;
				} else if (e.getNodeName().equals("cb")) {
					if (col != null) {
						System.out.println(folio + "." + col + " "
								+ num_miniatures + " " + num_rubrics);
					}

					col = e.getAttribute("n");
					num_miniatures = 0;
					num_rubrics = 0;
				} else {
					// have to check for cb again

					// TODO this might get rubrics wrong...

					NodeList l = e.getElementsByTagName("cb");

					if (l.getLength() > 0) {
						Element e2 = (Element) l.item(0);

						if (col != null) {
							System.out.println(folio + "." + col + " "
									+ num_miniatures + " " + num_rubrics);
						}

						col = e2.getAttribute("n");
						num_miniatures = 0;
						num_rubrics = 0;
					}

					l = e.getElementsByTagName("hi");

					for (int j = 0; j < l.getLength(); j++) {
						Element e2 = (Element) l.item(j);

						if (e2.getAttribute("rend").equals("rubric")) {
							num_rubrics++;
						}
					}
				}
			}

			if (col != null) {
				System.out.println(folio + "." + col + " " + num_miniatures
						+ " " + num_rubrics);
			}
		}
	}

	public static void mapFromTranscript(List<Scene> scenes, String msname,
			File fedoradatadir) throws IOException, SAXException {
		File dir = new File(fedoradatadir, msname);
		String[] filenames = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.contains("transcription");
			}

		});

		Arrays.sort(filenames);

		// Gather transcription data
		// folio.col -> lecoy_start, lecoy_end
		Map<String, int[]> data = new HashMap<String, int[]>();

		for (String filename : filenames) {
			String folio = filename.substring(msname.length()
					+ ".transcription.".length(), filename.length() - 4);

			Document doc = XMLUtil.createDocument(new File(dir, filename));
			NodeList top = doc.getDocumentElement().getChildNodes();
			String col = null;
			int lecoy_start = -1;
			int lecoy_end = -1;

			for (int i = 0; i < top.getLength(); i++) {
				if (top.item(i).getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}

				Element e = (Element) top.item(i);

				if (e.getNodeName().equals("lg")) {
					NodeList l = e.getElementsByTagName("milestone");

					if (lecoy_start == -1) {
						if (l.getLength() > 0) {
							String s = ((Element) l.item(0)).getAttribute("n");

							try {
								lecoy_start = Integer.parseInt(s);
							} catch (NumberFormatException ex) {
								System.err.println("Unable to parse lecoy in "
										+ filename + ": " + ex);
							}
						}
					}

					if (l.getLength() > 0) {
						String s = ((Element) l.item(l.getLength() - 1))
								.getAttribute("n");

						try {
							lecoy_end = Integer.parseInt(s);
						} catch (NumberFormatException ex) {
							System.err.println("Unable to parse lecoy in "
									+ filename + ": " + ex);
						}
					}

					// have to check for cb again
					// TODO this is not strictly correct...
					l = e.getElementsByTagName("cb");

					if (l.getLength() > 0) {
						Element e2 = (Element) l.item(0);

						if (col != null) {
							data.put(folio + "." + col, new int[] {
									lecoy_start, lecoy_end });
						}

						col = e2.getAttribute("n");
						lecoy_start = -1;
						lecoy_end = -1;
					}
				} else if (e.getNodeName().equals("cb")) {
					if (col != null) {
						data.put(folio + "." + col, new int[] { lecoy_start,
								lecoy_end });
					}

					col = e.getAttribute("n");
					lecoy_start = -1;
					lecoy_end = -1;
				}
			}

			if (col != null) {
				data.put(folio + "." + col,
						new int[] { lecoy_start, lecoy_end });
			}
		}

		// Do scene mapping

		for (Scene scene : scenes) {
			String foliocol_start = null;

			for (String foliocol : data.keySet()) {
				int[] lecoy_range = data.get(foliocol);

				if (scene.lecoy_start >= lecoy_range[0]
						&& scene.lecoy_start <= lecoy_range[1]) {
					foliocol_start = foliocol;
				}
			}

			if (foliocol_start == null) {
				System.err.println("Could not find scene start: " + scene.id);
				continue;
			}

			String foliocol_end = null;

			for (String foliocol : data.keySet()) {
				int[] lecoy_range = data.get(foliocol);

				if (scene.lecoy_end >= lecoy_range[0]
						&& scene.lecoy_end <= lecoy_range[1]) {
					foliocol_end = foliocol;
				}
			}

			if (foliocol_end == null) {
				System.err.println("Could not find scene end: " + scene.id);
				continue;
			}

			System.out.println(scene.id + " " + foliocol_start + " "
					+ foliocol_end);
		}
	}

	// Test the hypothesis that the number of images/rubrics a column has
	// is the average of its n neighbors
	public static void testNeighbors(ManuscriptInstance ms, int n) {
		int num_images_correct = 0;
		int num_rubrics_correct = 0;
		int num_tested = 0;
		int diff_images = 0;
		int diff_rubrics = 0;

		for (int i = 0; i < ms.foliodata.length; i++) {
			FolioData test = ms.foliodata[i];

			if (test.foliocol.equals("*")) {
				continue;
			}

			num_tested++;

			// check neighbors

			int num_neighbors = 0;
			int num_images = 0;
			int num_rubrics = 0;

			for (int j = i - n; j <= i + n; j++) {
				if (j < 0 || j >= ms.foliodata.length) {
					continue;
				}

				num_neighbors++;

				FolioData d = ms.foliodata[j];

				num_images += d.num_miniatures;
				num_rubrics += d.num_rubrics;
			}

			double avg_num_images = (double) num_images / num_neighbors;
			double avg_num_rubrics = (double) num_rubrics / num_neighbors;

			System.out.println(avg_num_images + " " + avg_num_rubrics);

			// avg_num_images = 0;
			// avg_num_rubrics = 0;

			if (test.num_miniatures == Math.rint(avg_num_images)) {
				num_images_correct++;
			}

			if (test.num_rubrics == Math.rint(avg_num_rubrics)) {
				num_rubrics_correct++;
			}

			diff_images += Math.abs(test.num_miniatures - avg_num_images);
			diff_rubrics += Math.abs(test.num_rubrics - avg_num_rubrics);
		}

		System.out.println("Num tested: " + num_tested);
		System.out.println("Num images correct: " + num_images_correct);
		System.out.println("Num rubrics correct: " + num_rubrics_correct);
		System.out.println("Image accuracy: " + (float) num_images_correct
				/ num_tested);
		System.out.println("Rubric accuracy: " + (float) num_rubrics_correct
				/ num_tested);
		System.out.println("Average image error: " + (float) diff_images
				/ num_tested);
		System.out.println("Average image error: " + (float) diff_rubrics
				/ num_tested);
	}

	private static int numLecoyLines(ManuscriptInstance ms, FolioData d) {
		return ms.lines_per_column
				- (d.num_miniatures * ms.lines_per_miniature) - d.num_rubrics;
	}

	// Return array of folio start positions, followed by the total number of
	// lecoy lines
	private static int[] guessLecoyNumbers(ManuscriptInstance ms) {
		int[] result = new int[ms.foliodata.length + 1];

		int start = 1;
		int folio = 0;

		for (FolioData d : ms.foliodata) {
			result[folio++] = start;
			start += numLecoyLines(ms, d);
		}

		result[folio] = start;

		return result;
	}

	private static void printLecoyDiff(ManuscriptInstance ms, int[] guess) {
		System.out.println("Diff between guessed and correct lecoy:");

		int folio = 0;
		for (FolioData d : ms.foliodata) {
			if (d.lecoy_start != -1) {
				System.out.println(d.foliocol + ": "
						+ (guess[folio] - d.lecoy_start));
			}

			folio++;
		}
	}

	// Divide into blocks. Each block ends with a known lecoy start.
	// Guess lecoy as normal for that block.
	// If there is a difference between guess for the end block and the correct
	// value, spread that difference evenly through the block.

	private static int[] guessLecoyNumbersUsingSync(ManuscriptInstance ms) {
		int startblock = 0;

		int[] guess = new int[ms.foliodata.length + 1];

		for (;;) {
			int endblock = -1; // inclusive

			for (int col = startblock; col < ms.foliodata.length; col++) {
				if (col == 0) {
					guess[col] = 0;
				} else {
					guess[col] = guess[col - 1]
							+ numLecoyLines(ms, ms.foliodata[col - 1]);
				}

				if (ms.foliodata[col].lecoy_start != -1) {
					endblock = col;
					break;
				}
			}

			if (endblock == -1) {
				break;
			}

			// Spread out diff

			int diff = guess[endblock] - ms.foliodata[endblock].lecoy_start;
			guess[endblock] = ms.foliodata[endblock].lecoy_start;

			while (diff != 0) {
				int n = 1;

				for (int col = startblock; col < endblock; col++) {
					if (diff > 0) {
						if (guess[col] > n) {
							guess[col] -= n++;
							diff--;
						}
					} else if (diff < 0) {
						if (col != 0) {
							guess[col] += n++;
							diff++;
						}
					} else {
						break;
					}
				}
			}

			startblock = endblock + 1;
		}

		return guess;
	}

	public static void map(List<Scene> scenes, ManuscriptInstance ms) {
		int[] guess = guessLecoyNumbers(ms);
		// int[] guess = guessLecoyNumbersUsingSync(ms);

		printLecoyDiff(ms, guess);
		printLecoyDiff(ms, guessLecoyNumbersUsingSync(ms));

		for (Scene scene : scenes) {
			String foliocol_start = null;

			for (int i = 0; i < guess.length - 1; i++) {
				if (scene.lecoy_start >= guess[i]
						&& scene.lecoy_start < guess[i + 1]) {
					foliocol_start = ms.foliodata[i].foliocol;
				}
			}

			if (foliocol_start == null) {
				System.err.println("Could not find scene start: " + scene.id);
				continue;
			}

			String foliocol_end = null;

			for (int i = 0; i < guess.length - 1; i++) {
				if (scene.lecoy_end >= guess[i]
						&& scene.lecoy_end < guess[i + 1]) {
					foliocol_end = ms.foliodata[i].foliocol;
				}
			}

			if (foliocol_end == null) {
				System.err.println("Could not find scene end: " + scene.id);
				continue;
			}

			System.out.println(scene.id + " " + foliocol_start + " "
					+ foliocol_end);
		}
	}

	public static void compareInstances(File truthdata, File testdata)
			throws IOException {
		ManuscriptInstance truth = new ManuscriptInstance(truthdata);
		ManuscriptInstance test = new ManuscriptInstance(testdata);

		int[] result = guessLecoyNumbersUsingSync(test);
		int[] result2 = guessLecoyNumbers(test);
		
		System.out.println("Using sync");
		printLecoyDiff(truth, result);
		
		System.out.println("Not using sync");
		printLecoyDiff(truth, result2);
	}

	public static void compareSceneMaps(File truthdata, File testdata)
			throws IOException {
		List<ManuscriptScene> truthlist = loadManuscriptSceneList(truthdata);
		List<ManuscriptScene> testlist = loadManuscriptSceneList(testdata);

		int num_correct = 0;
		int num_partial_correct = 0;
		int num_tested = 0;
		int loc_difference = 0;

		for (ManuscriptScene test : testlist) {
			for (ManuscriptScene truth : truthlist) {
				if (test.sceneid.equals(truth.sceneid)) {
					int test_loc_start = fromFolioCol(test.foliocol_start, 2);
					int test_loc_end = fromFolioCol(test.foliocol_end, 2);

					int truth_loc_start = fromFolioCol(truth.foliocol_start, 2);
					int truth_loc_end = fromFolioCol(truth.foliocol_end, 2);

					if (test_loc_start == truth_loc_start
							&& test_loc_end == truth_loc_end) {
						num_correct++;
					} else if (test_loc_start == truth_loc_start
							|| test_loc_end == truth_loc_end) {
						num_partial_correct++;
					}

					loc_difference += Math
							.abs(test_loc_start - truth_loc_start);

					System.err.println(test_loc_start - truth_loc_start);

					num_tested++;
					break;
				}
			}
		}

		System.out.println("Num correct: " + num_correct);
		System.out.println("Num partially correct: " + num_partial_correct);
		System.out.println("Num tesed: " + num_tested);
		System.out.println("Percent correct: " + (float) num_correct
				/ num_tested);
		System.out.println("Percent partially correct: "
				+ (float) num_partial_correct / num_tested);
		System.out.println("Average number of columns wrong: "
				+ (float) loc_difference / num_tested);
	}

	public static void generateInstanceFromTagging(File tags)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(tags));

		String line = null;
		int n = 0;
		String foliocol = null;
		int num_rubrics = 0;
		int num_miniatures = 0;
		int last_foliocol_loc = -1;

		int lines_per_miniatures = 6;
		int lines_per_column = 36;

		System.err.println("Edit lines_per_column and lines_per_miniature");
		System.out.println(lines_per_column + " " + lines_per_miniatures);

		// System.err.println(toFolioCol(fromFolioCol("10v.d", 2), 2));

		while ((line = in.readLine()) != null) {
			line = line.trim().toLowerCase();
			n++;
			if (line.length() == 0) {
				continue;
			}

			if (line.startsWith("rubric:")) {
				num_rubrics++;
			} else if (line.startsWith("[image")) {
				num_miniatures++;
			} else if (line.startsWith("[initial")) {
			} else if (line.startsWith("[folio")) {
				if (foliocol != null) {
					int loc = fromFolioCol(foliocol, 2);

					for (int i = last_foliocol_loc + 1; i < loc; i++) {
						System.out.println(toFolioCol(i, 2) + " 0 0");
					}
					last_foliocol_loc = loc;

					System.out.println(toFolioCol(fromFolioCol(foliocol, 2), 2)
							+ " " + num_miniatures + " " + num_rubrics);
				}

				int i = line.indexOf("folio");
				int j = line.indexOf("]");
				foliocol = line.substring(i + "folio".length(),
						j == -1 ? line.length() : j).trim();
				foliocol = foliocol.replaceAll("\\s+", ".");

				int loc = fromFolioCol(foliocol, 2);

				if (loc == -1) {
					System.err.println("Messed up folio col: " + foliocol);
				}

				num_rubrics = 0;
				num_miniatures = 0;
			} else {
				System.err.println("Unknown line " + n + ": " + line);
			}
		}

		if (foliocol != null) {
			int loc = fromFolioCol(foliocol, 2);

			for (int i = last_foliocol_loc; i < loc; i++) {
				System.out.println(toFolioCol(i, 2) + " 0 0");
			}

			System.out.println(toFolioCol(fromFolioCol(foliocol, 2), 2) + " "
					+ num_miniatures + " " + num_rubrics);
		}
		
		in.close();
	}

	public static String toFolioCol(int num, int cols) {
		int folio = 1 + (num / (cols * 2));
		int n = num % (cols * 2);

		char rectoverso = 'v';

		if (n / cols == 0) {
			rectoverso = 'r';
		}

		int coldiff = n % cols;
		char col;

		if (rectoverso == 'r') {
			col = (char) ('a' + coldiff);
		} else {
			col = (char) ('a' + cols + coldiff);
		}

		return String.format("%03d", folio) + rectoverso + "." + col;
	}

	public static int fromFolioCol(String s, int cols) {
		Pattern p = Pattern.compile("(\\d+)(r|v)\\.(\\w)");
		Matcher m = p.matcher(s.toLowerCase());

		if (m.find()) {
			int folio = Integer.parseInt(m.group(1));

			int loc = (folio - 1) * cols * 2;

			if (m.group(2).equals("v")) {
				loc += cols;
			}

			char col = m.group(3).toLowerCase().charAt(0);

			if (col == 'b' || col == 'd') {
				if (m.group(2).equals("r")) {
					loc += col - 'a';
				} else {
					// make sure ab
					loc += col - ('a' + cols);
				}
			}

			return loc;
		} else {
			return -1;
		}
	}

	public static void main(String[] args) throws IOException, SAXException {
		String cmd = args[0];

		if (cmd.equals("gen-instance-trans")) {
			generateInstanceTranscripts(args[1], new File(args[2]));
		} else if (cmd.equals("gen-instance-tags")) {
			generateInstanceFromTagging(new File(args[1]));
		} else if (cmd.equals("map-instance")) {
			map(loadSceneList(new File(args[1])), new ManuscriptInstance(
					new File(args[2])));
		} else if (cmd.equals("map-trans")) {
			mapFromTranscript(loadSceneList(new File(args[1])), args[2],
					new File(args[3]));
		} else if (cmd.equals("compare-instances")) {
			compareInstances(new File(args[1]), new File(args[2]));			
		} else if (cmd.equals("eval")) {
			compareSceneMaps(new File(args[1]), new File(args[2]));
		} else if (cmd.equals("test-neighbors")) {
			testNeighbors(new ManuscriptInstance(new File(args[1])), Integer
					.parseInt(args[2]));
		}
	}
}
