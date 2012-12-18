package rosa.search;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;

import rosa.search.SearchResult.SearchMatch;

public class LuceneSearcher {
	private final QueryParser parser;
	private final IndexSearcher searcher;
	private final Analyzer analyzer;

	public LuceneSearcher(File indexdir) throws IOException {
		this.searcher = new IndexSearcher(indexdir.getPath());
		this.analyzer = LuceneFields.getAnalyzer();
		this.parser = new QueryParser(LuceneFields.Base.IMAGE.lucenename(), analyzer);
	}

	@SuppressWarnings("unchecked")
	public SearchResult search(String query, int matchoffset, int maxmatches)
			throws ParseException, IOException {
		Query q = searcher.rewrite(parser.parse(query));
		Hits hits = searcher.search(q);

		matchoffset = Math.min(matchoffset, hits.length());
		int matchend = Math.min(matchoffset + maxmatches, hits.length());
		SearchMatch[] matches = new SearchMatch[matchend - matchoffset];

		Highlighter hilighter = new Highlighter(new QueryScorer(q));

		// Only check fields of terms for highlights

		@SuppressWarnings("rawtypes")
		Set terms = new HashSet();
		q.extractTerms(terms);
		@SuppressWarnings("rawtypes")
		Set fields = new HashSet();

		for (Object o : terms) {
			Term t = (Term) o;
			fields.add(t.field());
		}

		for (int i = 0; i < matches.length; i++) {
			Document doc = hits.doc(matchoffset + i);

			String book = doc.get(LuceneFields.Base.BOOK.lucenename());
			String image = doc.get(LuceneFields.Base.IMAGE.lucenename());

			String loc = book;

			if (image != null) {
				loc += "." + image;
			}

			matches[i] = new SearchMatch(loc);

			for (Object o : doc.getFields()) {
				Field field = (Field) o;

				if (field.isStored() && fields.contains(field.name())) {
					String snippet = hilighter.getBestFragment(analyzer, field
							.name(), field.stringValue());

					if (snippet != null) {
						matches[i].addSnippet(field.name(), snippet);
					}
				}
			}
		}

		return new SearchResult(matchoffset, hits.length(), matches);
	}

	public void close() throws IOException {
		searcher.close();
	}
}
