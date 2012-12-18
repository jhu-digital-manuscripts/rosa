package rosa.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import rosa.core.BookStructure.Folio;
import rosa.core.BookStructure.Side;

public class ReducedTagging {
	public static final String SUFFIX = "redtag.txt";
	private final BookStructure struct;

	public ReducedTagging(Reader input, List<String> errors) throws IOException {
		if (errors == null) {
			errors = new ArrayList<String>();
		}

		this.struct = new BookStructure();

		BufferedReader in = new BufferedReader(input);
		String line = null;
		int n = 0;
		List<BookStructure.Item> items = null;
		BookStructure.Column col = null;
		int collines = -1;

		while ((line = in.readLine()) != null) {
			line = line.trim();
			n++;
			if (line.length() == 0 || line.startsWith("#")) {
				continue;
			}

			// System.err.println(n + " " + line);

			String normline = line;
			if (line.startsWith("[") && line.endsWith("]")) {
				normline = line.substring(1, line.length() - 1);
			}

			normline = normline.replaceAll(":", " ");
			String[] parts = normline.split("\\s+");

			if (parts.length == 0) {
				continue;
			}

			try {
				if (parts[0].equalsIgnoreCase("rubric")
						|| parts[0].equalsIgnoreCase("r")) {
					if (parts.length < 3) {
						errors.add("Line " + n + ": "
								+ "Rubric missing lines and text: " + line);
						continue;
					}

					int lines = Integer.parseInt(parts[1]);

					if (items != null) {
						String text = normline.replaceFirst("^\\s*[Rr][a-z]*\\s*\\d+\\s*", "").trim();
						items.add(new BookStructure.Rubric(text, lines));
					}
				} else if (parts[0].equalsIgnoreCase("heading")
						|| parts[0].equalsIgnoreCase("h")) {
					if (parts.length < 3) {
						errors.add("Line " + n + ": "
								+ "Heading missing lines and text: " + line);
						continue;
					}

					int lines = Integer.parseInt(parts[1]);

					if (items != null) {
						items.add(new BookStructure.Heading(normline.substring(
								normline.indexOf(' ')).trim(), lines));
					}
				} else if (parts[0].equalsIgnoreCase("lecoy")) {
					if (parts.length < 3) {
						errors.add("Line " + n + ": "
								+ "Lecoy missing text and number: " + line);
						continue;
					}

					if (col == null) {
						errors
								.add("Line " + n + ": " + "No folio yet: "
										+ line);
						continue;
					}

					col.first_line_lecoy = Integer
							.parseInt(parts[parts.length - 1]);
					col.first_line = normline.substring(normline.indexOf(' '),
							normline.lastIndexOf(' ')).trim();
				} else if (parts[0].equalsIgnoreCase("columnlines")) {
					if (parts.length != 2) {
						errors.add("Line " + n + ": "
								+ "Column without lines: " + line);
						continue;
					}

					collines = Integer.parseInt(parts[1]);
				} else if (parts[0].equalsIgnoreCase("image") || parts[0].equalsIgnoreCase("m")) {
					if (parts.length != 2) {
						errors.add("Line " + n + ": " + "Image without lines: "
								+ line);
						continue;
					}

					int lines = Integer.parseInt(parts[1]);

					if (items != null) {
						items.add(new BookStructure.Image(lines));
					}
				} else if (parts[0].equalsIgnoreCase("blank")
						|| parts[0].equalsIgnoreCase("b")) {
					if (parts.length != 2) {
						errors.add("Line " + n + ": " + "Blank without lines: "
								+ line);
						continue;
					}

					int lines = Integer.parseInt(parts[1]);

					if (items != null) {
						items.add(new BookStructure.Blank(lines));
					}
				} else if (parts[0].equalsIgnoreCase("initial")
						|| parts[0].equalsIgnoreCase("i")) {
					if (parts.length < 2) {
						errors.add("Line " + n + ": "
								+ "Initial without letter: " + line);
						continue;
					}

					int lines = 0;
					boolean empty = false;

					if (parts.length == 3) {
						empty = parts[2].equalsIgnoreCase("(empty)");

						if (!empty) {
							lines = Integer.parseInt(parts[2]);
						}
					}

					if (parts[1].length() > 1) {
						errors.add("Line " + n
								+ ": Initial more than one character: "
								+ parts[1]);
						continue;
					}

					if (items != null) {
						items.add(new BookStructure.Initial(parts[1], empty,
								lines));
					}
				} else if (parts[0].equalsIgnoreCase("folio")
						|| parts[0].equalsIgnoreCase("page")
						|| parts[0].equalsIgnoreCase("f")) {
					if (parts.length != 3) {
						errors.add("Line " + n + ": "
								+ "Folio needs name and column: " + line);
						continue;
					}

					String sidename = parts[1].toLowerCase();
					String c = parts[2].toLowerCase();

					if (!sidename.endsWith("r") && !sidename.endsWith("v")) {
						errors.add("Folio malformed: " + sidename);
						continue;
					}

					String leafname = sidename.substring(0,
							sidename.length() - 1);
					BookStructure.Folio leaf = null;

					for (BookStructure.Folio l : struct.folios) {
						if (l.name.equals(leafname)) {
							leaf = l;
							break;
						}
					}

					if (leaf == null) {
						leaf = new Folio(leafname, collines);
						struct.folios.add(leaf);
					}

					Side side = null;

					if (sidename.endsWith("r")) {
						side = leaf.recto;
					} else {
						side = leaf.verso;
					}

					if (c.equals("a") || c.equals("c")) {
						col = side.col1;
						items = col.items();
					} else if (c.equals("b") || c.equals("d")) {
						col = side.col2;
						items = col.items();
					} else if (c.equals("ab") || c.equals("cd")) {
						items = side.spanning;
					} else {
						errors.add("Line " + n + ": Malformed column: " + line);
					}
				} else {
					errors.add("Line " + n + ": Malformed: " + line);
					continue;
				}
			} catch (NumberFormatException e) {
				errors.add("Line " + n + ": Error parsing number: " + line);
			}
		}

		if (collines == -1) {
			errors.add("columnlines must be set");
		}

		// Insert missing folios. Only works for ms.

		if (struct.folios().size() > 0
				&& !struct.folios.get(0).name.matches("\\d+")) {
			// Not a manuscript
			return;
		}

		for (int i = 0; i < struct.folios.size();) {
			Folio f2 = struct.folios.get(i);

			int n2 = Integer.parseInt(f2.name);
			int n1 = 0;

			if (i > 0) {
				Folio f1 = struct.folios.get(i - 1);

				n1 = Integer.parseInt(f1.name);
			}

			while (++n1 < n2) {
				Folio missing = new Folio("" + n1, collines);
				struct.folios.add(i++, missing);
			}

			i++;
		}
	}

	public BookStructure structure() {
		return struct;
	}

	public boolean check(BookArchive archive, List<String> errors) {
		boolean success = true;

		for (BookStructure.Folio leaf : struct.folios()) {
			String filename = archive.guessImageName(leaf.name + "r");

			if (filename == null) {
				// maybe recto is missing
				filename = archive.guessImageName(leaf.name + "v");

				if (filename == null) {
					if (errors != null) {
						errors.add("Can't match folio to filename: "
								+ leaf.name);
					}

					success = false;
				}
			}
		}

		return success;
	}

	public static void generateTemplate(String bookid, int numfolios,
			PrintStream output) {
		System.out.println("# " + bookid);
		System.out.println();
		System.out.println("columnlines 40");
		System.out.println();

		for (int i = 0; i < numfolios; i++) {
			String s = ManuscriptArchive.toFolio(i + 1, 'r');
			System.out.println("folio " + s + " a");
			System.out.println();

			System.out.println("folio " + s + " b");
			System.out.println();

			s = ManuscriptArchive.toFolio(i + 1, 'v');
			System.out.println("folio " + s + " c");
			System.out.println();

			System.out.println("folio " + s + " d");
			System.out.println();
		}
	}
}
