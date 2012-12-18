package rosa.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import rosa.core.util.CSV;

public class NarrativeSections {
	public static String NAME = "narrative_sections.csv";

	private final String[][] table;

	public enum Data {
		ID, LINES, LECOY, DESCRIPTION, COMMENT
	}

	private static String normalizeSectionId(String id) {
		return id.replaceAll("\\s+", "").toLowerCase();
	}

	public NarrativeSections(Reader input, List<String> errors)
			throws IOException {
		this.table = CSV.parseTable(input);

		int n = 1;
		for (String[] row : table) {
			if (row.length > Data.values().length) {
				errors.add("Row " + n + " too long");
			}

			if (row.length < 4) {
				errors.add("Row " + n + " too short");
			}

			if (n > 1) {
				checkRange(n, row[Data.LINES.ordinal()], errors);

				if (!row[Data.LECOY.ordinal()].isEmpty()) {
					checkRange(n, row[Data.LECOY.ordinal()], errors);
				}

				row[0] = normalizeSectionId(row[0]);
			}

			n++;
		}

	}

	public NarrativeSections(File file, List<String> errors) throws IOException {
		this(new FileReader(file), errors);
	}

	private void checkRange(int row, String s, List<String> errors) {
		// TODO how to create scene for this?
		if (s.equals("a-j")) {
			return;
		}

		if (!s.matches("\\d+-\\d+")) {
			errors.add("Row " + row + " bad range " + s);
		}
	}

	public int numScenes() {
		return table.length - 1;
	}

	public String getValue(int scene, Data d) {
		// Check for ragged rows
		if (d.ordinal() >= table[scene + 1].length) {
			return "";
		}

		return Normalizer.normalize(table[scene + 1][d.ordinal()], Form.NFC);
	}

	public int[] getRangeValue(int scene, Data d) {
		String s = getValue(scene, d);

		if (s.isEmpty()) {
			return null;
		}

		// TODO Hack for stupid lecoy
		if (s.equals("a-j")) {
			return null;
		}

		String[] parts = s.split("\\-");

		return new int[] { Integer.parseInt(parts[0]),
				Integer.parseInt(parts[1]) };
	}

	public Scene[] asScenes() {
		List<Scene> result = new ArrayList<Scene>();

		for (int i = 0; i < numScenes(); i++) {
			int[] lecoy = getRangeValue(i, Data.LECOY);
			int[] lines = getRangeValue(i, Data.LINES);

			if (lecoy == null || lines == null) {
				continue;
			}

			result.add(new Scene(getValue(i, Data.ID), getValue(i,
					Data.DESCRIPTION), lecoy == null ? -1 : lecoy[0],
					lecoy == null ? -1 : lecoy[1], lines[0], lines[1]));
		}

		return result.toArray(new Scene[] {});
	}

	/**
	 * Lecoy start and end are inclusive. Scene id is normalized for comparison.
	 */
	public static class Scene {
		public final String desc;
		public final String id;
		public final int lecoy_start;
		public final int lecoy_end;
		public final int rel_line_start;
		public final int rel_line_end;

		private Scene(String id, String desc, int lecoy_start, int lecoy_end,
				int rel_line_start, int rel_line_end) {
			this.desc = desc;
			this.id = id.replaceAll("\\s+", "");
			this.lecoy_end = lecoy_end;
			this.lecoy_start = lecoy_start;
			this.rel_line_end = rel_line_end;
			this.rel_line_start = rel_line_start;
		}

		public boolean isLecoy() {
			return lecoy_start != -1;
		}
	}

	public int findIndexById(String id) {
		int numsections = numScenes();
		id = normalizeSectionId(id);

		for (int sec = 0; sec < numsections; sec++) {
			if (getValue(sec, Data.ID).equals(id)) {
				return sec;
			}
		}

		return -1;
	}
}
