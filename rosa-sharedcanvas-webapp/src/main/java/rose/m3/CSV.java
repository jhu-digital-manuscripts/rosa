package rose.m3;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

/**
 * Parse comma separated values with '\' used as an escape. Spaces between
 * values are ignored.
 */
public class CSV {
	public static String[] parse(String csv) {
		List<String> vals = new ArrayList<String>();
		boolean quoted = false;

		StringBuffer val = new StringBuffer();

		for (int i = 0; i < csv.length(); i++) {
			char c = csv.charAt(i);

			if (c == '\"') {
				if (quoted) {
					quoted = false;
				} else {
					quoted = true;
				}

				if (i > 0 && csv.charAt(i - 1) == '\"') {
					val.append(c);
				}
			} else if (quoted) {
				val.append(c);
			} else if (c == ',') {
				vals.add(val.toString().trim());
				val.setLength(0);
			} else {
				val.append(c);
			}
		}

		vals.add(val.toString().trim());

		return (String[]) vals.toArray(new String[] {});
	}

	public static String escape(String val) {
		val = val.replaceAll("\\\"", "\"\"");

		if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
			return "\"" + val + "\"";
		} else {
			return val;
		}
	}

	public static String[][] parseTable(Reader input) throws IOException {
		boolean quoted = false;
		List<List<String>> table = new ArrayList<List<String>>();
		StringBuilder cell = new StringBuilder();
		List<String> row = new ArrayList<String>();

		for (;;) {
			int c = input.read();
			int last = -1;

			if (c == -1) {
				break;
			} else if (quoted) {
				if (c == '\"') {
					quoted = false;
				} else {
					cell.append((char) c);
				}
			} else if (c == '\n') {
				row.add(cell.toString().trim());
				cell.setLength(0);

				table.add(new ArrayList<String>(row));
				row.clear();
			} else if (c == '\"') {
				if (c == last) {
					cell.append((char) c);
				}

				quoted = true;
			} else if (c == ',') {
				row.add(cell.toString().trim());
				cell.setLength(0);
			} else if (c == '\r') {
			} else {
				cell.append((char) c);
			}

			last = c;
		}

		if (cell.length() > 0) {
			row.add(cell.toString().trim());
		}

		if (row.size() > 0) {
			table.add(row);
		}

		String[][] result = new String[table.size()][];

		for (int i = 0; i < table.size(); i++) {
			result[i] = table.get(i).toArray(new String[] {});
		}

		return result;
	}

	/**
	 * Trim whitespace from each value and convert contiguous whitespace in a
	 * value to a single space and also normalize characters.
	 * 
	 * @param table
	 */
	public static void normalizeWhiteSpaceAndCharacters(String[][] table) {
		for (String[] row : table) {
			for (int i = 0; i < row.length; i++) {
				row[i] = row[i].replaceAll("\\s+", " ").trim();
				row[i] = Normalizer.normalize(row[i], Form.NFC);
			}
		}
	}

	public static void write(String[][] table, Writer out) throws IOException {
		for (String[] row : table) {
			for (int i = 0; i < row.length; i++) {
				out.append(escape(row[i]));

				if (i != row.length - 1) {
					out.append(',');
				}
			}

			out.append('\n');
		}
		
		out.flush();
	}
}
