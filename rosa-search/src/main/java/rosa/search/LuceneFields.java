package rosa.search;

import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.index.memory.PatternAnalyzer;

/**
 * Match lucene fields to analyzers. Awkwardly this is a duplicate of rose.server.LuceneFields because
 * of build dependency issues.
 */
public class LuceneFields {
	private static Analyzer english = new SnowballAnalyzer("English");
	private static Analyzer french = new SnowballAnalyzer("French");
	
	// Tokenizes on spaces and . while removing excess 0's
	private static Analyzer imagename = new PatternAnalyzer(Pattern.compile("\\s+|^0*|\\.0*"), true, null);
	
	// Tokenizes on spaces
	private static Analyzer string = new PatternAnalyzer(Pattern.compile("\\s+"), true, null);

	public static Analyzer getAnalyzer() {
		PerFieldAnalyzerWrapper wrapper = new PerFieldAnalyzerWrapper(string);

		for (Base field : Base.values()) {
			wrapper.addAnalyzer(field.lucenename(), getAnalyzer(field.type(), null));
		}

		for (String lc : Description.languages()) {
			for (Description field : Description.values()) {
				wrapper.addAnalyzer(field.lucenename(lc), getAnalyzer(field
						.type(), lc));
			}
		}

		for (ImageTag field : ImageTag.values()) {
			wrapper.addAnalyzer(field.lucenename(), getAnalyzer(field.type(), null));
		}

		for (NarrativeTag field : NarrativeTag.values()) {
			wrapper.addAnalyzer(field.lucenename(), getAnalyzer(field.type(), null));
		}

		for (Transcription field : Transcription.values()) {
			wrapper.addAnalyzer(field.lucenename(), getAnalyzer(field.type(), null));
		}

		return wrapper;
	}

	private static Analyzer getAnalyzer(Type type, String lc) {
		if (type == Type.ENGLISH) {
			return english;
		} else if (type == Type.FRENCH) {
			return french;
		} else if (type == Type.OLD_FRENCH) {
			return french;
		} else if (type == Type.TEXT) {
			if (lc != null) {
				if (lc.equals("en")) {
					return english;
				} else if (lc.equals("fr")) {
					return french;
				}
			}

			return string;
		} else if (type == Type.IMAGE_NAME) {
			return imagename;						
		} else {
			return string;
		}
	}

	public enum Type {
		OLD_FRENCH, ENGLISH, FRENCH, TEXT, STRING, NUMBER, IMAGE_NAME;
	}

	public enum Transcription {
		TR_POETRY(Type.OLD_FRENCH), TR_RUBRIC(Type.OLD_FRENCH), TR_ILLUSTRATION(
				Type.ENGLISH), TR_LECOY(Type.STRING), TR_NOTE(
				Type.ENGLISH), TR_LINE(Type.NUMBER), TR_CATCHPHRASE(
				Type.OLD_FRENCH);

		private final Type type;

		private Transcription(Type type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public Type type() {
			return type;
		}
	};

	public enum Description {
		DS_REPOSITORY(Type.TEXT), DS_SHELFMARK(Type.TEXT), DS_CITY(
				Type.TEXT), DS_DATE(Type.TEXT), DS_ORIGIN(
				Type.TEXT), DS_HEIGHT(Type.NUMBER), DS_WIDTH(
				Type.NUMBER), DS_TYPE(Type.TEXT), DS_NUM_ILLUSTRATIONS(
				Type.NUMBER), DS_NUM_FOLIOS(Type.NUMBER), DS_TEXT(
				Type.TEXT);

		private final Type type;

		private Description(Type type) {
			this.type = type;
		}

		public String lucenename(String lc) {
			return name().toLowerCase() + "_" + lc;
		}

		public static String[] languages() {
			return new String[] { "en", "fr" };
		}

		public Type type() {
			return type;
		}
	}

	public enum ImageTag {
		IM_TITLE(Type.ENGLISH), IM_CHAR(Type.OLD_FRENCH), IM_KEYWORD(
				Type.ENGLISH);

		private final Type type;

		private ImageTag(Type type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public Type type() {
			return type;
		}
	}

	public enum Base {
		BOOK(Type.STRING), IMAGE(Type.IMAGE_NAME), IMAGE_ALTERNATES(Type.STRING);

		private final Type type;

		private Base(Type type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public Type type() {
			return type;
		}
	}

	public enum NarrativeTag {
		SECTION_ID(Type.STRING), START_LINE_TRANSCRIPTION(Type.OLD_FRENCH), SECTION_DESCRIPTION(Type.ENGLISH);

		private final Type type;

		private NarrativeTag(Type type) {
			this.type = type;
		}

		public String lucenename() {
			return name().toLowerCase();
		}

		public Type type() {
			return type;
		}
	}
}
