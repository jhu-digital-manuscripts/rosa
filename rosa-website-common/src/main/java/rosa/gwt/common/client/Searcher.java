package rosa.gwt.common.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import rosa.gwt.common.client.resource.Labels;
import rosa.search.SearchResult;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Wraps SearchService in a user friendly abstraction. User fields and user
 * queries that are mapped to lucene fields and lucene queries.
 */
public class Searcher {
    // FIXME: Awful hack
    public static String LC = "en";

	private final Map<String, SearchResult> searchCache; // url -> result

	private final static int MAX_SUBSTRING_EXPANSIONS = 10;

	private final static String[][] oldfrenchspelling = new String[][] {
			{ "i", "j", "y" }, { "i", "j", "g" }, { "v", "u" },
			{ "c", "q", "k", "cc" }, { "s", "\u00E7", "ss", "z" } };

	private final SearchServiceAsync searchservice;

	// ALL is treated specially to match all lucene fields used by other
	// UserFields
	public enum UserField {
		ALL(Labels.INSTANCE.allFields()), POETRY(
				Labels.INSTANCE.linesOfVerse(), Transcription.TR_POETRY,
				NarrativeTag.START_LINE_TRANSCRIPTION), RUBRIC(Labels.INSTANCE
				.rubric(), Transcription.TR_RUBRIC), ILLUSTRATION_TITLE(
				Labels.INSTANCE.illustrationTitle(),
				Transcription.TR_ILLUSTRATION, ImageTag.IM_TITLE), LECOY(
				Labels.INSTANCE.lecoy(), Transcription.TR_LECOY), NOTE(
				Labels.INSTANCE.criticalNote(), Transcription.TR_NOTE), ILLUSTRATION_CHAR(
				Labels.INSTANCE.illustrationChar(), ImageTag.IM_CHAR), ILLUSTRATION_KEYWORDS(
				Labels.INSTANCE.illustrationKeywords(), ImageTag.IM_KEYWORD), DESCRIPTION(
				Labels.INSTANCE.bookDescription(), Description.DS_TEXT), IMAGE(
				Labels.INSTANCE.imageName(), Base.IMAGE, Base.IMAGE_ALTERNATES), NARRATIVE_SECTION(
				Labels.INSTANCE.narrativeSections(), NarrativeTag.SECTION_ID,
				NarrativeTag.SECTION_DESCRIPTION);

		public final String display;
		public final LuceneField[] fields;

		private UserField(String display, LuceneField... fields) {
			this.display = display;
			this.fields = fields;
		}

		// TODO build map?
		public static UserField findByLuceneField(String lucenename) {
			for (UserField uf : values()) {
				for (LuceneField f : uf.fields) {
					if (f.lucenename().equals(lucenename)) {
						return uf;
					}
				}
			}

			return null;
		}
	}

	public Searcher(SearchServiceAsync searchservice) {
		this.searchCache = new HashMap<String, SearchResult>();
		this.searchservice = searchservice;
	}

	public enum LuceneFieldType {
		OLD_FRENCH, ENGLISH, FRENCH, TEXT, STRING, NUMBER;
	}

	private interface LuceneField {
		public String lucenename();

		public LuceneFieldType type();
	}

	private enum Transcription implements LuceneField {
		TR_POETRY(LuceneFieldType.OLD_FRENCH), TR_RUBRIC(
				LuceneFieldType.OLD_FRENCH), TR_ILLUSTRATION(
				LuceneFieldType.ENGLISH), TR_LECOY(LuceneFieldType.TEXT), TR_NOTE(
				LuceneFieldType.ENGLISH), TR_LINE(LuceneFieldType.NUMBER), TR_CATCHPHRASE(
				LuceneFieldType.OLD_FRENCH);

		private final LuceneFieldType type;

		private Transcription(LuceneFieldType type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public LuceneFieldType type() {
			return type;
		}
	};

	private enum Description implements LuceneField {
		DS_REPOSITORY(LuceneFieldType.TEXT), DS_SHELFMARK(LuceneFieldType.TEXT), DS_CITY(
				LuceneFieldType.TEXT), DS_DATE(LuceneFieldType.TEXT), DS_ORIGIN(
				LuceneFieldType.TEXT), DS_HEIGHT(LuceneFieldType.NUMBER), DS_WIDTH(
				LuceneFieldType.NUMBER), DS_TYPE(LuceneFieldType.TEXT), DS_NUM_ILLUSTRATIONS(
				LuceneFieldType.NUMBER), DS_NUM_FOLIOS(LuceneFieldType.NUMBER), DS_TEXT(
				LuceneFieldType.TEXT);

		private final LuceneFieldType type;

		private Description(LuceneFieldType type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase() + "_" + LC;
		}

		public LuceneFieldType type() {
			return type;
		}
	}

	private enum ImageTag implements LuceneField {
		IM_TITLE(LuceneFieldType.ENGLISH), IM_CHAR(LuceneFieldType.OLD_FRENCH), IM_KEYWORD(
				LuceneFieldType.ENGLISH);

		private final LuceneFieldType type;

		private ImageTag(LuceneFieldType type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public LuceneFieldType type() {
			return type;
		}
	}

	private enum Base implements LuceneField {
		BOOK(LuceneFieldType.STRING), IMAGE(LuceneFieldType.STRING), IMAGE_ALTERNATES(
				LuceneFieldType.STRING);

		private final LuceneFieldType type;

		private Base(LuceneFieldType type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public LuceneFieldType type() {
			return type;
		}
	}

	private enum NarrativeTag implements LuceneField {
		SECTION_ID(LuceneFieldType.STRING), START_LINE_TRANSCRIPTION(
				LuceneFieldType.OLD_FRENCH), SECTION_DESCRIPTION(
				LuceneFieldType.ENGLISH);

		private final LuceneFieldType type;

		private NarrativeTag(LuceneFieldType type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public LuceneFieldType type() {
			return type;
		}
	}

	/**
	 * Return a user or lucene query which exactly matches a string.
	 */
	public static String createLiteralQuery(String s) {
		StringBuilder sb = new StringBuilder(s.length());

		sb.append("\"");
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == '&' || c == '|' || c == '(' || c == ')' || c == '}'
					|| c == '{' || c == '[' || c == ']' || c == ':' || c == '^'
					|| c == '!' || c == '\"' || c == '+' || c == '-'
					|| c == '~' || c == '*' || c == '?' || c == '\\') {
				sb.append('\\');
			}

			sb.append(c);
		}
		sb.append("\"");

		return sb.toString();
	}

	// Tokenize a user query into lucene terms and phrases.
	// Phrases will be surrounded by "
	// Unsupported lucene characters are escaped.
	// Always returns valid lucene terms

	public static List<String> parseUserQuery(String query) {
		List<String> luceneterms = new ArrayList<String>();

		final int TERM = 1;
		final int SEP = 2;
		final int PHRASE = 3;
		final int PHRASE_PROXIMITY = 4;

		int state = SEP;
		boolean escaped = false;
		StringBuilder luceneprefix = new StringBuilder();
		StringBuilder luceneterm = new StringBuilder();
		StringBuilder lucenesuffix = new StringBuilder();

		int numquotes = 0;

		for (int i = 0; i < query.length(); i++) {
			char c = query.charAt(i);

			// System.err.println(c + " [" + luceneprefix + "] [" + luceneterm +
			// "] [" + lucenesuffix + "]" );

			if (state == PHRASE_PROXIMITY) {
				if (Character.isDigit(c)) {
					lucenesuffix.append(c);
				} else {
					addQueryTerm(luceneterms, luceneprefix, luceneterm,
							lucenesuffix);

					// push back
					i--;
					state = SEP;
				}
			} else if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
				if (state == TERM) {
					addQueryTerm(luceneterms, luceneprefix, luceneterm,
							lucenesuffix);
					state = SEP;
				} else if (state == PHRASE) {
					luceneterm.append(' ');
				}
			} else if (escaped) {
				escaped = false;
				luceneterm.append('\\');
				luceneterm.append(c);
			} else if (c == '\\') {
				escaped = true;
			} else if (state == SEP && (c == '+' || c == '-')) {
				if (luceneprefix.length() == 0) {
					luceneprefix.append(c);
				}
			} else if (c == '"') {
				numquotes++;

				if (state == PHRASE) {
					lucenesuffix.append(c);

					// Check for ~num

					if (i + 2 < query.length() && query.charAt(i + 1) == '~'
							&& Character.isDigit(query.charAt(i + 2))) {
						lucenesuffix.append(query.charAt(i + 1));
						lucenesuffix.append(query.charAt(i + 2));
						i += 2;
						state = PHRASE_PROXIMITY;
					} else {
						addQueryTerm(luceneterms, luceneprefix, luceneterm,
								lucenesuffix);
						state = SEP;
					}
				} else {
					if (luceneterm.length() > 0) {
						addQueryTerm(luceneterms, luceneprefix, luceneterm,
								lucenesuffix);
					}

					luceneprefix.append(c);
					state = PHRASE;
				}
			} else if (c == '&' || c == '|' || c == '(' || c == ')' || c == '}'
					|| c == '{' || c == '[' || c == ']' || c == ':' || c == '^'
					|| c == '!' || c == '~' || c == '+' || c == '-') {
				luceneterm.append('\\');
				luceneterm.append(c);
			} else {
				if (state == SEP) {
					state = TERM;
				}

				luceneterm.append(c);
			}
		}

		// Phrase started, but didn't end with quote, add quote.
		if ((numquotes & 1) > 0) {
			lucenesuffix.append('\"');
		}

		addQueryTerm(luceneterms, luceneprefix, luceneterm, lucenesuffix);

		// System.err.println("parsed user query into num terms "
		// + luceneterms.size());

		return luceneterms;
	}

	private static void addQueryTerm(List<String> terms,
			StringBuilder luceneprefix, StringBuilder term,
			StringBuilder lucenesuffix) {

		if (term.length() > 0) {
			terms.add(luceneprefix.toString() + term + lucenesuffix);

			// System.err.println("Added: " + terms.get(terms.size() - 1));
		}

		term.setLength(0);
		luceneprefix.setLength(0);
		lucenesuffix.setLength(0);
	}

	public void searchCollection(final UserField[] userfields,
			final String[] userqueries, final String[] restrictedbookids,
			final int offset, final int max, String[][] charnames,
			final AsyncCallback<SearchResult> cb) {

		String lucenequery = createLuceneQuery(userfields, userqueries,
				restrictedbookids, charnames);
		searchCollection(lucenequery, offset, max, cb);
	}

	private void searchCollection(final String lucenequery, final int offset,
			final int max, final AsyncCallback<SearchResult> cb) {
		final String key = lucenequery + ":" + offset + ":" + max;

		SearchResult result = (SearchResult) searchCache.get(key);

		if (result != null) {
			cb.onSuccess(result);
			return;
		}

		searchservice.search(lucenequery, offset, max,
				new AsyncCallback<SearchResult>() {
					public void onFailure(Throwable caught) {
						cb.onFailure(caught);
					}

					public void onSuccess(SearchResult result) {
						searchCache.put(key, result);
						cb.onSuccess(result);
					}
				});
	}

	private void buildLuceneQuery(StringBuilder sb, LuceneField[] fields,
			List<String> terms, String[][] charnames) {

		String oldfrquery = buildLuceneQuery(terms, LuceneFieldType.OLD_FRENCH, charnames);
		String textquery = buildLuceneQuery(terms, LuceneFieldType.TEXT, charnames);
		String simplequery = buildLuceneQuery(terms, LuceneFieldType.STRING, charnames);

		for (LuceneField field : fields) {
			addFieldQuery(sb, field, oldfrquery, textquery, simplequery);
			sb.append(' ');
		}
	}

	/**
	 * Create lucene query given specified user queries in user fields. The
	 * lucene query is restricted to only include results matching the given
	 * book ids.
	 * 
	 * @param userfields
	 * @param userqueries
	 * @param restrictedbookids
	 * @return
	 */
	private String createLuceneQuery(UserField[] userfields,
			String[] userqueries, String[] restrictedbookids, String[][] charnames) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < userfields.length; i++) {
			UserField uf = userfields[i];
			String userquery = userqueries[i];

			if (uf == null || userquery == null) {
				continue;
			}

			List<String> terms = parseUserQuery(userquery);

			if (uf == UserField.ALL) {
				for (UserField uf2 : UserField.values()) {
					if (uf2 != UserField.ALL) {
						buildLuceneQuery(sb, uf2.fields, terms, charnames);
					}
				}
			} else {
				buildLuceneQuery(sb, uf.fields, terms, charnames);
			}
		}

		if (restrictedbookids != null && restrictedbookids.length > 0) {
			sb.insert(0, '(');
			sb.append(") && " + Base.BOOK.lucenename() + ":(");
			for (String bookid : restrictedbookids) {
				sb.append(createLiteralQuery(bookid));
				sb.append(' ');
			}
			sb.append(')');
		}

		return sb.toString();
	}

	private static void addFieldQuery(StringBuilder sb, LuceneField field,
			String oldfrquery, String textquery, String simplequery) {
		sb.append(field.lucenename());
		sb.append(":(");

		if (field.type() == LuceneFieldType.OLD_FRENCH) {
			sb.append(oldfrquery);
		} else if (field.type() == LuceneFieldType.NUMBER
				|| field.type() == LuceneFieldType.STRING) {
			sb.append(simplequery);
		} else {
			sb.append(textquery);
		}

		sb.append(")");
	}

	private String buildLuceneQuery(List<String> terms, LuceneFieldType type, String[][] charnames) {
		StringBuilder sb = new StringBuilder();

		for (String term : terms) {
			if (!term.trim().isEmpty()) {
				expandLuceneTerm(sb, charnames, term, type);
				sb.append(' ');
			}
		}

		return sb.toString();
	}

	private static void expandLuceneTerm(StringBuilder sb,
			String[][] charnames, String term, LuceneFieldType type) {
		if (type == LuceneFieldType.OLD_FRENCH) {
			// expand char names and then spellings

			boolean bool = isLuceneBoolean(term);
			boolean fuzzy = isLuceneFuzzy(term);

			if (bool || fuzzy) {
				sb.append('(');
				sb.append(term);
				sb.append(')');
				sb.append(' ');
			} else {
				// Need two hashsets because of concurrent modification

				HashSet<String> terms = expandWords(charnames, term);
				HashSet<String> result = new HashSet<String>(terms);

				for (int i = 0; i < oldfrenchspelling.length; i++) {
					for (String s : terms) {
						expandVariants(result, oldfrenchspelling[i], s);
					}
				}

				combineTerms(sb, result);
			}
		} else if (type == LuceneFieldType.NUMBER
				|| type == LuceneFieldType.STRING) {
			sb.append('(');
			sb.append(term);
			sb.append(')');
			sb.append(' ');
		} else {
			// expand char names

			combineTerms(sb, expandWords(charnames, term));
		}
	}

	private static void combineTerms(StringBuilder sb, Collection<String> terms) {
		sb.append('(');

		for (String s : terms) {
			sb.append(s);
			sb.append(' ');
		}

		sb.append(')');
	}

	private static boolean isLucenePhrase(String term) {
		return term.matches("(\\+|\\-)?\\\".*\\\"(~\\d+)?");
	}

	private static boolean isLuceneBoolean(String term) {
		return term.startsWith("+") || term.startsWith("-");
	}

	private static boolean isLuceneFuzzy(String term) {
		return term.endsWith("~") || term.contains("*") || term.contains("?");
	}

	/**
	 * @param expansion
	 *            List of variants. Variants is list of words
	 * @param lucene
	 *            term
	 * @return set of lucene terms
	 */

	private static HashSet<String> expandWords(String[][] expansion, String term) {
		boolean phrase = isLucenePhrase(term);

		HashSet<String> result = new HashSet<String>();
		result.add(term);

		next: for (String[] variants : expansion) {
			for (String variant : variants) {
				if (variant.isEmpty()) {
					continue;
				}

				if (phrase) {
					if (term.contains(variant)) {
						expandVariants(result, variants, term);
						continue next;
					}
				} else {
					if (term.equalsIgnoreCase(variant)) {
						for (String v : variants) {
							if (v != variant && !v.isEmpty()) {
								result.add(createLiteralQuery(v));
							}
						}

						continue next;
					}
				}
			}
		}

		return result;
	}

	private static void expandVariants(Collection<String> result,
			String[] variants, String string) {

		result.add(string);

		for (int i = 0; i < string.length(); i++) {
			boolean foundexpansion = false;

			for (String sub : variants) {
				i = string.indexOf(sub, i);

				if (i == -1) {
					continue;
				}

				String start = string.substring(0, i);
				String end = string.substring(i + sub.length());

				for (String s : variants) {
					result.add(start + s + end);
				}

				if (result.size() > MAX_SUBSTRING_EXPANSIONS) {
					return;
				}
			}

			if (!foundexpansion) {
				return;
			}
		}
	}
}
