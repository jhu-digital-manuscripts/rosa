package rosa.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rosa.core.util.CSV;

/**
 * Maps narrative sections to a manuscript.
 * 
 * Two formats are supported, text and CSV. The text format is created by humans
 * and the scenes are assumed to be correctly assigned.
 */
public class NarrativeMapping {
	public static final String MANUAL_SUFFIX = "nartag.txt";
	public static final String AUTOMATIC_SUFFIX = "nartag.csv";

	private final ArrayList<Scene> scenes;

	public static class Scene {
		private final String id;
		private final String start_folio;
		private final String start_folio_col;
		private final int start_line_offset;
		private final String end_folio;
		private final String end_folio_col;
		private final int end_line_offset;
		private final int start_lecoy;
		private String start_trans;
		public final boolean correct;

		public Scene(String id, String start_folio, String start_folio_col,
				int start_line_offset, String end_folio, String end_folio_col,
				int end_line_offset, String start_trans, boolean correct,
				int start_lecoy) {
			this.end_folio = end_folio;
			this.end_folio_col = end_folio_col;
			this.end_line_offset = end_line_offset;
			this.id = id;
			this.start_folio = start_folio;
			this.start_folio_col = start_folio_col;
			this.start_line_offset = start_line_offset;
			this.start_trans = start_trans;
			this.correct = correct;
			this.start_lecoy = start_lecoy;
		}

		public String id() {
			return id;
		}

		public String startFolio() {
			return start_folio;
		}

		public String startFolioCol() {
			return start_folio_col;
		}

		public int startLineOffset() {
			return start_line_offset;
		}

		public String endFolio() {
			return end_folio;
		}

		public String endFolioCol() {
			return end_folio_col;
		}

		public int endLineOffset() {
			return end_line_offset;
		}

		public String startLineTranscription() {
			return start_trans;
		}
		
		public int startLineLecoy() {
			return start_lecoy;
		}
	}

	public NarrativeMapping(File input, boolean textformat, List<String> errors)
			throws IOException {
		this(new FileReader(input), textformat, errors);
	}

	public NarrativeMapping() {
		this.scenes = new ArrayList<Scene>();
	}

	private static Scene createScene(String idinfo, String startinfo,
			String startlineoffsetinfo, String endinfo,
			String endlineoffsetinfo, String trans, List<String> errors,
			int line, boolean correct, int start_lecoy) {
		String id = null;
		String start_folio = null;
		String start_folio_col = null;
		int start_line_offset = 0;
		String end_folio = null;
		String end_folio_col = null;
		int end_line_offset = 0;

		String[] start = startinfo.split("\\.");
		String[] end = endinfo.split("\\.");

		if (start.length != 2 || end.length != 2) {
			errors.add("Line " + line + ": Malformed folio.col: " + line);
			return null;
		}

		id = idinfo.replaceAll("\\s+", "");
		
		start_folio = start[0];
		start_folio_col = start[1];
		end_folio = end[0];
		end_folio_col = end[1];

		try {
			start_line_offset = Integer.parseInt(startlineoffsetinfo);
			end_line_offset = Integer.parseInt(endlineoffsetinfo);
		} catch (NumberFormatException e) {
			errors.add("Line " + line + ": Error parsing line offset " + line);
			return null;
		}

		if (!start_folio_col.matches("a|b|c|d")
				|| !end_folio_col.matches("a|b|c|d")) {
			errors.add("Line " + line + ": Malformed column: " + line);
			return null;
		}

		return new Scene(id, start_folio, start_folio_col, start_line_offset,
				end_folio, end_folio_col, end_line_offset, trans, correct,
				start_lecoy);
	}

	private static void loadFromText(ArrayList<Scene> scenes, Reader input,
			List<String> errors) throws IOException {
		if (errors == null) {
			errors = new ArrayList<String>();
		}

		BufferedReader in = new BufferedReader(input);
		String line;

		int n = 0;
		String[] parts = null;

		Pattern lecoypat = Pattern.compile("^(.*)\\s+L?(\\d+)\\s*$");

		while ((line = in.readLine()) != null) {
			line = line.trim();
			n++;
			if (line.length() == 0 || line.startsWith("#")) {
				continue;
			}

			if (line.startsWith("[") && line.endsWith("]")) {
				parts = line.substring(1, line.length() - 1).split("\\s+");

				if (parts.length != 5) {
					errors.add("Line " + n + ": Malformed: " + line);
					parts = null;
					continue;
				}
			} else if (line.endsWith("]") || line.startsWith("[")) {
				errors.add("Line " + n + ": Missing [ or ]: " + line);
				parts = null;
			} else {
				if (parts == null) {
					errors
							.add("Line " + n + ": Double transcriptions? "
									+ line);
					continue;
				}

				Matcher m = lecoypat.matcher(line);

				if (!m.matches()) {
					errors.add("Line " + n + ": Missing lecoy " + line);
					continue;
				}
 
				String trans = m.group(1).trim();
				Integer lecoy = Integer.parseInt(m.group(2));
				
				//System.out.println(line);
				//System.out.println(trans + ": " + lecoy);

				//System.err.println("scene " + parts[0] + " " + parts[1] + " " + parts[3]);
				

				Scene scene = createScene(parts[0], parts[1], parts[2],
						parts[3], parts[4], trans, errors, n, true, lecoy);
				parts = null;
				
				if (scene != null) {
					scenes.add(scene);
				}
			}
		}
	}

	private static void loadFromCSV(ArrayList<Scene> scenes, Reader input,
			List<String> errors) throws IOException {
		String[][] table = CSV.parseTable(input);
		int n = 1;

		for (String[] row : table) {
			if (row.length != 8) {
				throw new IOException("Line " + n + " malformed");
			}

			Scene scene = createScene(row[0], row[1], row[2], row[3], row[4],
					row[5], errors, n, Boolean.parseBoolean(row[6]), Integer.parseInt(row[7]));

			if (scene != null) {
				scenes.add(scene);
			}

			n++;
		}
	}

	public NarrativeMapping(Reader input, boolean textformat,
			List<String> errors) throws IOException {
		this();

		if (textformat) {
			loadFromText(scenes, input, errors);
		} else {
			loadFromCSV(scenes, input, errors);
		}
	}

	public List<Scene> scenes() {
		return scenes;
	}

	public void writeInTextFormat(FileOutputStream os) {
		PrintStream out = new PrintStream(os);

		for (Scene scene : scenes) {
			out.println("[" + scene.id() + " " + scene.startFolio() + "."
					+ scene.startFolioCol() + " " + scene.startLineOffset()
					+ " " + scene.endFolio() + "." + scene.endFolioCol() + " "
					+ scene.endLineOffset() + "]");

			if (scene.startLineTranscription() != null) {
				out.println(scene.startLineTranscription() + " L" + scene.start_lecoy);
			}

			out.println();
		}

		out.flush();
	}

	public void writeInCSVFormat(FileOutputStream os) {
		PrintStream out = new PrintStream(os);

		for (Scene scene : scenes) {
			out.println(CSV.escape(scene.id())
					+ ","
					+ CSV.escape(scene.startFolio() + "."
							+ scene.startFolioCol())
					+ ","
					+ CSV.escape("" + scene.startLineOffset())
					+ ","
					+ CSV.escape(scene.endFolio() + "." + scene.endFolioCol())
					+ ","
					+ CSV.escape("" + scene.endLineOffset())
					+ ","
					+ (scene.startLineTranscription() != null ? CSV
							.escape(scene.startLineTranscription()) : "") + ","
					+ (scene.correct ? "1" : "0") + "," + scene.start_lecoy);
		}

		out.flush();
	}

	public List<Scene> findScenesInImage(String filename, BookArchive archive) {
		List<Scene> result = new ArrayList<Scene>();

		for (Scene scene : scenes) {
			String s = scene.start_folio;
			s = archive.guessImageName(s);

			if (s != null && s.equals(filename)) {
				result.add(scene);
			}
		}

		return result;
	}

	public void check(NarrativeSections secs, BookArchive archive,
			List<String> errors) {
		for (NarrativeMapping.Scene scene : scenes()) {
			String s1 = archive.guessImageName(scene.startFolio());
			String s2 = archive.guessImageName(scene.endFolio());

			if (s1 == null || s2 == null) {
				errors.add("Can't match scene folio to filenames: "
						+ scene.id() + " " + scene.startFolio() + "-"
						+ scene.endFolio());
			}

			int sec = secs.findIndexById(scene.id());

			if (sec == -1) {
				errors
						.add("Could not find scene description for "
								+ scene.id());
			}
		}
	}
}
