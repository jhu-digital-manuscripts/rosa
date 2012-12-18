package rosa.core;

import java.util.ArrayList;
import java.util.List;

// TODO fixup field access

/**
 * Structure of a book. Info about items in each column of each folio.
 * Assumption of two columns. Also have to handle items spanning columns.
 */

public class BookStructure {
	public static final String SUFFIX = "redtag.txt";
	
	final List<Folio> folios; // in manuscript order

	public BookStructure() {
		this.folios = new ArrayList<Folio>();
	}

	public static class Folio {
		public final String name;
		public Side recto;
		public Side verso;

		public Folio(String name, int collines) {
			this.name = name;
			this.recto = new Side(name + "r", collines);
			this.verso = new Side(name + "v", collines);
		}

		public String toString() {
			return name + (recto == null ? "" : " [" + recto + "]")
					+ (verso == null ? "" : " [" + verso + "]");
		}
	}

	public static class Column {
		private final Side parent;
		private final List<Item> items;
		int first_line_lecoy;
		String first_line;
		public final int lines_per_column;

		public Column(Side parent, int lines_per_column) {
			this.first_line_lecoy = -1;
			this.parent = parent;
			this.lines_per_column = lines_per_column;
			this.items = new ArrayList<Item>();
		}

		public List<Item> items() {
			return items;
		}

		public int firstLineLecoy() {
			return first_line_lecoy;
		}

		public String firstLineTranscribed() {
			return first_line;
		}

		public Side parent() {
			return parent;
		}

		public int totalLines() {
			return lines_per_column;
		}

		public int linesOfPoetry() {
			return lines_per_column - itemLines();
		}

		public int itemLines() {
			int count = 0;

			for (Item item : items) {
				if (item instanceof Initial) {
					continue;
				}

				count += item.lines;
			}

			for (Item item : parent.spanning) {
				if (item instanceof Initial) {
					continue;
				}

				count += item.lines;
			}

			return count;
		}

		public char columnLetter() {
			if (parent.folio().endsWith("r")) {
				if (parent.column1() == this) {
					return 'a';
				} else {
					return 'b';
				}
			} else {
				if (parent.column1() == this) {
					return 'c';
				} else {
					return 'd';
				}
			}
		}

		public String toString() {
			return "" + parent.folio + "." + columnLetter();
		}
	}

	public static class Side {
		private final String folio;
		List<Item> spanning; // Spans two columns
		Column col1;
		Column col2;

		public Side(String folio, int collines) {
			this.folio = folio;
			this.col1 = new Column(this, collines);
			this.col2 = new Column(this, collines);
			this.spanning = new ArrayList<Item>();
		}

		public Column column1() {
			return col1;
		}

		public Column column2() {
			return col2;
		}

		public String folio() {
			return folio;
		}

		public String toString() {
			return folio + (col1 == null ? "" : " [" + col1 + "]")
					+ (col2 == null ? "" : " [" + col2 + "]");
		}

		public List<Item> spanningItems() {
			return spanning;
		}
	}

	public static class Item {
		public final int lines;

		protected Item(int lines) {
			this.lines = lines;
		}
	}

	public static class Rubric extends Item {
		public final String text;

		public Rubric(String text, int lines) {
			super(lines);
			this.text = text;
		}
	}

	public static class Heading extends Item {
		public final String text;

		public Heading(String text, int lines) {
			super(lines);
			this.text = text;
		}
	}

	public static class Initial extends Item {
		public final String character;
		public final boolean empty;

		public Initial(String c, boolean empty, int lines) {
			super(lines);
			this.character = c;
			this.empty = empty;
		}
	}

	public static class Blank extends Item {
		public Blank(int lines) {
			super(lines);
		}
	}

	public static class Image extends Item {
		public Image(int lines) {
			super(lines);
		}
	}

	/**
	 * @return folios in manuscript order
	 */
	public List<Folio> folios() {
		return folios;
	}

	/**
	 * @return columns in manuscript order
	 */
	public List<Column> columns() {
		List<Column> cols = new ArrayList<Column>();

		for (Folio folio : folios) {
			if (folio.recto != null) {
				if (folio.recto.col1 != null) {
					cols.add(folio.recto.col1);
				}

				if (folio.recto.col2 != null) {
					cols.add(folio.recto.col2);
				}
			}

			if (folio.verso != null) {
				if (folio.verso.col1 != null) {
					cols.add(folio.verso.col1);
				}

				if (folio.verso.col2 != null) {
					cols.add(folio.verso.col2);
				}
			}
		}

		return cols;
	}
}
