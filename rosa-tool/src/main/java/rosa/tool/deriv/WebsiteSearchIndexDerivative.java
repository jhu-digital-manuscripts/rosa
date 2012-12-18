package rosa.tool.deriv;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import rosa.core.BookArchive;
import rosa.core.NarrativeSections;
import rosa.tool.Config;
import rosa.search.LuceneIndexer;

/**
 * Update search index for website. Must be run after website-data is updated.
 */
public class WebsiteSearchIndexDerivative extends Derivative {
	public static String NAME = "website-search-index";

	public WebsiteSearchIndexDerivative(Config site, PrintStream report)
			throws IOException {
		super(site, report);
	}

	public String name() {
		return NAME;
	}

	public boolean update(boolean force) {
		try {
			LuceneIndexer indexer = new LuceneIndexer(site.searchIndexPath(), col.loadCharacterNames(null), col.loadIllustrationTitles(null), true);
			
			List<String> errors = new ArrayList<String>();
			NarrativeSections narscenes = col.loadNarrativeScenes(errors);
			
			for (String s : errors) {
				report.println("Narrative scene list error: " + s);
			}
			
			for (String msname : col.books()) {
				BookArchive archive = col.loadArchive(msname);

				report.println("Indexing " + msname);

				File transchunkdir = new File(site.dataPath(), msname);
				indexer.index(archive, col.languages(), transchunkdir, narscenes);
			}

			indexer.finish();
		} catch (IOException e) {
			reportError("IO error while indexing for rose search", e);
			return false;
		} catch (SAXException e) {
			reportError("XML error while indexing for rose search", e);
			return false;
		}

		return true;
	}

	public boolean check(PrintStream report) {
		// TODO
		return true;
	}

	public boolean validate() {
		return check(report);
	}

	public boolean check(BookArchive archive) {
		report.println("Operation not supported on BookArchive");
		return false;
	}

	public boolean update(BookArchive archive, boolean force) {
		report.println("Operation not supported on BookArchive");
		return false;
	}

	public boolean validate(BookArchive archive) {
		report.println("Operation not supported on BookArchive");
		return false;
	}
}
