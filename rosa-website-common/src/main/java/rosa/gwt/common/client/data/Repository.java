package rosa.gwt.common.client.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import rosa.gwt.common.client.Util;
import rosa.gwt.common.client.resource.Labels;

public class Repository {
	private final Map<String, Book> books; // loaded books: id -> Book
	private final String[][] bookdata;

	public enum Category {
		ID(null), REPOSITORY(Labels.INSTANCE.repository()), SHELFMARK(
				Labels.INSTANCE.shelfmark()), COMMON_NAME(Labels.INSTANCE
				.commonName()), LOCATION(Labels.INSTANCE.currentLocation()), DATE(
				Labels.INSTANCE.date()), ORIGIN(Labels.INSTANCE.origin()), TYPE(
				Labels.INSTANCE.type()), NUM_ILLUSTRATIONS(Labels.INSTANCE
				.numIllustrations()), NUM_FOLIOS(Labels.INSTANCE.numFolios()), TRANSCRIPTION(
				Labels.INSTANCE.transcription()), ILLUSTRATION_TAGGING(
				Labels.INSTANCE.illustrationDescription()), NARRATIVE_TAGGING(
				Labels.INSTANCE.narrativeSections()), BIBLIOGRAPHY(null);

		private final String display;

		private Category(String display) {
			this.display = display;
		}

		public String display() {
			return display;
		}
	}

	private void labelNonePartialComplete(String[] data, Category d) {
		String s = data[d.ordinal()];

		if (s.equals("0")) {
			data[d.ordinal()] = Labels.INSTANCE.none();
		} else if (s.equals("1")) {
			data[d.ordinal()] = Labels.INSTANCE.partial();
		} else {
			data[d.ordinal()] = Labels.INSTANCE.complete();
		}
	}

	public Repository(String[][] bookdata) {
		this.books = new HashMap<String, Book>();
		this.bookdata = bookdata;

		// Do some post-processing of bookdata

		for (String[] data : bookdata) {
			for (int i = 0; i < data.length; i++) {
				if (data[i].isEmpty()) {
					data[i] = Labels.INSTANCE.unknownValue();
				}
			}

			labelNonePartialComplete(data, Category.TRANSCRIPTION);
			labelNonePartialComplete(data, Category.NARRATIVE_TAGGING);
			labelNonePartialComplete(data, Category.ILLUSTRATION_TAGGING);
		}

		// sort by fullname

		Arrays.sort(bookdata, new Comparator<String[]>() {
			public int compare(String[] d1, String[] d2) {
				String n1 = d1[Category.REPOSITORY.ordinal()]
						+ d1[Category.SHELFMARK.ordinal()];
				String n2 = d2[Category.REPOSITORY.ordinal()]
						+ d2[Category.SHELFMARK.ordinal()];

				return Util.compareStringsPossiblyEndingWithNumbers(n1, n2);
			}
		});
	}

	public Map<String, Book> loadedBooks() {
		return books;
	}

	public int numBooks() {
		return bookdata.length;
	}

	public int findBookByID(String id) {
		for (int i = 0; i < numBooks(); i++) {
			if (bookData(i, Category.ID).equals(id)) {
				return i;
			}
		}

		return -1;
	}

	public String bookData(int book, Category category) {
		return bookdata[book][category.ordinal()];
	}

	public String fullBookName(int book) {
		return bookData(book, Category.REPOSITORY) + ", "
				+ bookData(book, Category.SHELFMARK);
	}

	/**
	 * Mappings sorted in way appropriate to category.
	 * 
	 * @param cat
	 * @return category value -> list of books sorted by full name
	 */
	public TreeMap<String, List<Integer>> browse(Category category) {
		Comparator<String> cmp;

		if (category == Repository.Category.NUM_FOLIOS
				|| category == Repository.Category.NUM_ILLUSTRATIONS) {
			// numeric sort

			cmp = new Comparator<String>() {
				public int compare(String s1, String s2) {
					try {
						return Integer.parseInt(s1) - Integer.parseInt(s2);
					} catch (NumberFormatException e) {
						return s1.compareTo(s2);
					}
				}
			};
		} else {
			cmp = new Comparator<String>() {
				public int compare(String s1, String s2) {
					return Util.compareStringsPossiblyEndingWithNumbers(s1, s2);
				}
			};
		}

		TreeMap<String, List<Integer>> result = new TreeMap<String, List<Integer>>(
				cmp);

		for (int book = 0; book < numBooks(); book++) {
			String data = bookData(book, category);

			List<Integer> books = result.get(data);

			if (books == null) {
				books = new ArrayList<Integer>();
				result.put(data, books);
			}

			books.add(book);
		}

		return result;
	}

}
