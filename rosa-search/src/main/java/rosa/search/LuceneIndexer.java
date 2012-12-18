package rosa.search;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import rosa.core.BookArchive;
import rosa.core.BookDescription;
import rosa.core.BookStructure;
import rosa.core.CharacterNames;
import rosa.core.IllustrationTitles;
import rosa.core.ImageTagging;
import rosa.core.ManuscriptArchive;
import rosa.core.NarrativeMapping;
import rosa.core.NarrativeSections;
import rosa.core.ReducedTagging;

public class LuceneIndexer {
	private final IndexWriter writer;
	private final CharacterNames charnames;
	private final IllustrationTitles illustitles;

	public LuceneIndexer(File indexdir, CharacterNames charnames,
			IllustrationTitles illustitles, boolean create) throws IOException {
		writer = new IndexWriter(indexdir, LuceneFields.getAnalyzer(), create);
		this.charnames = charnames;
		this.illustitles = illustitles;
	}

	private static void stripxml(InputSource src, final StringBuilder out)
			throws SAXException, IOException {
		XMLReader r = XMLReaderFactory.createXMLReader();

		r.setContentHandler(new DefaultHandler() {
			public void characters(char[] text, int offset, int len)
					throws SAXException {
				out.append(text, offset, len);
			}

		});

		r.parse(src);
	}

	public void index(BookArchive archive, String[] languages,
			File transchunkdir, NarrativeSections narscenes)
			throws IOException, SAXException {

		// Per book fields

		indexDescriptions(archive, languages);

		List<String> errors = new ArrayList<String>();
		ImageTagging imgtag = archive.imageTagging(errors);

		if (imgtag != null) {
			imgtag.replaceIdentifiersWithStrings(illustitles, charnames);
		}

		if (errors.size() > 0) {
			for (String s : errors) {
				System.err.println("Image tagging error for " + archive.id()
						+ ": " + s);
			}
		}

		// Per image fields

		for (String filename : archive.filenames()) {
			if (!archive.isImage(filename)) {
				continue;
			}

			Document doc = new Document();

			String trchunkname = filename
					.replace(BookArchive.IMAGE_EXT, ".xml");
			trchunkname = trchunkname.replace(archive.id(), archive.id()
					+ ".transcription");
			File trchunk = new File(transchunkdir, trchunkname);

			if (trchunk.exists()) {
				indexTranscriptionChunk(doc, trchunk);
			}

			if (imgtag != null) {
				StringBuilder chars = new StringBuilder();
				StringBuilder titles = new StringBuilder();
				StringBuilder keywords = new StringBuilder();

				for (int image : imgtag.findImageIndexes(filename, archive)) {
					for (String name : imgtag.characters(image)) {
						chars.append(name);
						chars.append(", ");
					}

					for (String title : imgtag.titles(image)) {
						titles.append(title);
						titles.append(", ");
					}

					keywords.append(imgtag.textualElements(image));
					keywords.append(", ");
					keywords.append(imgtag.architecture(image));
					keywords.append(", ");
					keywords.append(imgtag.costume(image));
					keywords.append(", ");
					keywords.append(imgtag.other(image));
					keywords.append(", ");
					keywords.append(imgtag.objects(image));
					keywords.append(", ");
					keywords.append(imgtag.landscape(image));
					keywords.append(", ");
				}

				if (chars.length() > 0) {
					doc.add(new Field(LuceneFields.ImageTag.IM_CHAR.lucenename(),
							chars.toString(), Field.Store.YES,
							Field.Index.TOKENIZED));
				}

				if (titles.length() > 0) {
					doc.add(new Field(LuceneFields.ImageTag.IM_TITLE.lucenename(),
							titles.toString(), Field.Store.YES,
							Field.Index.TOKENIZED));
				}

				if (keywords.length() > 0) {
					doc.add(new Field(LuceneFields.ImageTag.IM_KEYWORD.lucenename(),
							keywords.toString(), Field.Store.YES,
							Field.Index.TOKENIZED));
				}
			}

			String book = BookArchive.getID(filename);
			String image = BookArchive.getName(filename);

			doc.add(new Field(LuceneFields.Base.BOOK.lucenename(), book,
					Field.Store.YES, Field.Index.TOKENIZED));
			doc.add(new Field(LuceneFields.Base.IMAGE.lucenename(), image,
					Field.Store.YES, Field.Index.TOKENIZED));

			if (archive instanceof ManuscriptArchive) {
				if (((ManuscriptArchive) archive).isFolioImage(filename)) {
					String folio = ManuscriptArchive.findFolio(filename);
					if (folio != null) {
						folio = folio.replaceAll("^0+|r|v", "");

						doc.add(new Field(LuceneFields.Base.IMAGE_ALTERNATES
								.lucenename(), folio, Field.Store.NO,
								Field.Index.TOKENIZED));
					}
				}
			}

			NarrativeMapping nartag = archive.narrativeTagging(null);

			if (nartag != null) {

				StringBuilder trans = new StringBuilder();
				StringBuilder sectionids = new StringBuilder();
				StringBuilder sectiondesc = new StringBuilder();

				for (NarrativeMapping.Scene scene : nartag.findScenesInImage(
						filename, archive)) {

					if (scene.startLineTranscription() != null) {
						trans.append(scene.startLineTranscription() + ", ");
					}

					sectionids.append(scene.id() + " ");

					int sec = narscenes.findIndexById(scene.id());

					if (sec != -1) {
						sectiondesc.append(narscenes.getValue(sec,
								NarrativeSections.Data.DESCRIPTION)
								+ ", ");
					} else {
						System.err
								.println("Warning: Could not find scene description for "
										+ scene.id());
					}
				}

				if (trans.length() > 0) {
					doc.add(new Field(
							LuceneFields.NarrativeTag.START_LINE_TRANSCRIPTION
									.lucenename(), trans.toString(),
							Field.Store.YES, Field.Index.TOKENIZED));
				}

				if (sectionids.length() > 0) {
					doc.add(new Field(LuceneFields.NarrativeTag.SECTION_ID
							.lucenename(), sectionids.toString(),
							Field.Store.NO, Field.Index.TOKENIZED));
				}

				if (sectiondesc.length() > 0) {
					doc.add(new Field(LuceneFields.NarrativeTag.SECTION_DESCRIPTION
							.lucenename(), sectiondesc.toString(),
							Field.Store.YES, Field.Index.TOKENIZED));
				}
			}

			// Index rubrics from reduced tagging in the transcription rubric
			// field

			BookStructure.Side side = findReducedTaggingSide(archive, filename);

			if (side != null) {
				StringBuffer rubrics = new StringBuffer();

				addRubrics(rubrics, side.column1());
				addRubrics(rubrics, side.column2());

				if (rubrics.length() > 0) {
					// System.out.println(filename + " " + rubrics);

					doc.add(new Field(LuceneFields.Transcription.TR_RUBRIC
							.lucenename(), rubrics.toString(), Field.Store.YES,
							Field.Index.TOKENIZED));
				}
			}

			writer.addDocument(doc);
		}
	}

	private BookStructure.Side findReducedTaggingSide(BookArchive archive,
			String filename) throws IOException {
		ReducedTagging redtag = archive.reducedTagging(null);

		if (redtag == null) {
			return null;
		}

		for (BookStructure.Folio folio : redtag.structure().folios()) {
			String test = archive.guessImageName(folio.name + "r");

			if (filename.equals(test)) {
				return folio.recto;
			}

			test = archive.guessImageName(folio.name + "v");

			if (filename.equals(test)) {
				return folio.verso;
			}
		}

		return null;
	}

	private void addRubrics(StringBuffer rubrics, BookStructure.Column col) {
		for (BookStructure.Item item : col.items()) {
			if (item instanceof BookStructure.Rubric) {
				String rubric = ((BookStructure.Rubric) item).text;

				// remove / used to indicate abbreviations
				rubric = rubric.replaceAll("/", "");

				rubrics.append(rubric + ", ");
			}
		}
	}

	private void indexDescriptions(BookArchive archive, String[] languages)
			throws IOException, SAXException {
		Document doc = new Document();

		doc.add(new Field(LuceneFields.Base.BOOK.lucenename(), archive.id(),
				Field.Store.YES, Field.Index.TOKENIZED));

		for (String lc : languages) {
			indexDescription(doc, archive, lc);
		}

		writer.addDocument(doc);
	}

	private void indexDescription(Document doc, BookArchive archive, String lc)
			throws IOException, SAXException, IOException {
		BookDescription desc = archive.description(lc);

		if (desc == null) {
			return;
		}

		for (LuceneFields.Description field : LuceneFields.Description.values()) {
			String value = null;

			switch (field) {
			case DS_REPOSITORY:
				value = desc.repository();
				break;
			case DS_SHELFMARK:
				value = desc.shelfmark();
				break;
			case DS_CITY:
				value = desc.currentLocation();
				break;
			case DS_DATE:
				value = desc.date();
				break;
			case DS_ORIGIN:
				value = desc.origin();
				break;
			case DS_HEIGHT:
				value = "" + desc.height();
				break;
			case DS_WIDTH:
				value = "" + desc.width();
				break;
			case DS_TYPE:
				value = desc.type();
				break;
			case DS_NUM_ILLUSTRATIONS:
				value = desc.numIllustrations() + "";
				break;
			case DS_NUM_FOLIOS:
				value = desc.numFolios() + "";
				break;
			case DS_TEXT:
				StringBuilder sb = new StringBuilder();
				stripxml(new InputSource(new FileReader(desc.file())), sb);
				value = sb.toString();
				break;
			}

			if (value != null) {
				doc.add(new Field(field.lucenename(lc), value, Field.Store.YES,
						Field.Index.TOKENIZED));
			}
		}
	}

	private void indexTranscriptionChunk(Document doc, File file)
			throws IOException, SAXException {

		final StringBuffer poetry = new StringBuffer();
		final StringBuffer line = new StringBuffer();
		final StringBuffer lecoy = new StringBuffer();
		final StringBuffer rubric = new StringBuffer();
		final StringBuffer catchphrase = new StringBuffer();
		final StringBuffer illus = new StringBuffer();
		final StringBuffer note = new StringBuffer();

		XMLReader r = XMLReaderFactory.createXMLReader();

		r.setContentHandler(new DefaultHandler() {
			StringBuffer cur = null;

			public void startElement(String uri, String localName,
					String qName, Attributes atts) throws SAXException {
				if (localName.equals("lg") || localName.equals("expan")
						|| localName.equals("add") || localName.equals("del")
						|| localName.equals("rdg") || localName.equals("app")
						|| localName.equals("cb") || localName.equals("pb")
						|| localName.equals("div") || localName.equals("gap")
						|| localName.equals("desc")) {
					return;
				}

				if (localName.equals("note")
						&& getValue(atts, "type").equals("scribalPun")) {
					return;
				}

				if (localName.equals("note")
						&& getValue(atts, "type").equals("character")) {
					illus.append(" ");
					cur = illus;
					return;
				}

				if (localName.equals("note")) {
					cur = note;
					return;
				}

				if (localName.equals("figure")) {
					illus.append(" ");
					cur = illus;
					return;
				}

				if (localName.equals("l")) {
					line.append(" " + getValue(atts, "n"));
					poetry.append(" ");
					cur = poetry;
					return;
				}

				if (localName.equals("milestone")) {
					lecoy.append(" " + getValue(atts, "n"));
					return;
				}

				if (localName.equals("hi")
						&& getValue(atts, "rend").equals("init")) {
					return;
				}

				if (localName.equals("hi")
						&& getValue(atts, "rend").equals("nota")) {
					return;
				}

				if (localName.equals("hi")
						&& getValue(atts, "rend").equals("rubric")) {
					rubric.append(" ");
					cur = rubric;
					return;
				}

				if (localName.equals("fw")) {
					catchphrase.append(" ");
					cur = catchphrase;
					return;
				}

				if (localName.equals("head")) {
					return;
				}

				System.err.println("Not handled " + localName);
			}

			public void characters(char[] text, int offset, int len)
					throws SAXException {
				if (cur != null) {
					cur.append(text, offset, len);
				}
			}
		});

		r.parse(new InputSource(new FileReader(file)));

		Field field;

		field = new Field(LuceneFields.Transcription.TR_POETRY.lucenename(), poetry
				.toString(), Field.Store.YES, Field.Index.TOKENIZED);
		doc.add(field);

		if (catchphrase.length() > 0) {
			field = new Field(LuceneFields.Transcription.TR_CATCHPHRASE.lucenename(),
					catchphrase.toString(), Field.Store.YES,
					Field.Index.TOKENIZED);
			doc.add(field);
		}

		if (rubric.length() > 0) {
			field = new Field(LuceneFields.Transcription.TR_RUBRIC.lucenename(),
					rubric.toString(), Field.Store.YES, Field.Index.TOKENIZED);
			doc.add(field);
		}

		if (illus.length() > 0) {
			field = new Field(
					LuceneFields.Transcription.TR_ILLUSTRATION.lucenename(), illus
							.toString(), Field.Store.YES, Field.Index.TOKENIZED);
			doc.add(field);
		}

		field = new Field(LuceneFields.Transcription.TR_LECOY.lucenename(), lecoy
				.toString(), Field.Store.NO, Field.Index.TOKENIZED);
		doc.add(field);

		field = new Field(LuceneFields.Transcription.TR_LINE.lucenename(), line
				.toString(), Field.Store.NO, Field.Index.TOKENIZED);
		doc.add(field);

		if (note.length() > 0) {
			field = new Field(LuceneFields.Transcription.TR_NOTE.lucenename(), note
					.toString(), Field.Store.YES, Field.Index.TOKENIZED);
			doc.add(field);
		}
	}

	private static String getValue(Attributes atts, String name) {
		for (int i = 0; i < atts.getLength(); i++) {
			if (atts.getLocalName(i).equals(name)) {
				return atts.getValue(i);
			}
		}

		return null;
	}

	public void finish() throws IOException {
		writer.optimize();
		writer.close();
	}
}
