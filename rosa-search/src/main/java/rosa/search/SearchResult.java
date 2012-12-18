package rosa.search;

import java.util.ArrayList;
import java.io.Serializable;

public class SearchResult implements Serializable {
	public int total;
	public int offset;
	public SearchMatch[] matches;

	public SearchResult() {
	}

	public SearchResult(int offset, int total, SearchMatch[] matches) {
		this.offset = offset;
		this.total = total;
		this.matches = matches;
	}

	/**
	 * The location is either a 'manuscript id' or 'manuscript id'.'image name'
	 * Snippets contains field name, html snippet pairs.
	 */
	public static class SearchMatch implements Serializable {
		public String loc;
		public ArrayList<String> snippets;

		public SearchMatch() {
		}

		public SearchMatch(String loc) {
			this.loc = loc;
			this.snippets = new ArrayList<String>(4);
		}

		public void addSnippet(String field, String snippet) {
			snippets.add(field);
			snippets.add(snippet);
		}
	}
}
