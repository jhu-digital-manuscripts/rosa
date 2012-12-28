package rosa.gwt.common.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class NarrativeTagging {
	private final String[][] table;
	private final int map[]; // For each section index, give image index in book

	// images

	public enum Data {
		SECTION_ID, START_FOLIO_COL, START_LINE_OFFSET, END_FOLIO_COL, END_LINE_OFFSET, TRANSCRIPTION, CORRECT, TRANSCRIPTION_LECOY;
	}

	public String startFolio(int index) {
		return getValue(index, Data.START_FOLIO_COL).split("\\.")[0];
	}

	public boolean isChecked(int index) {
		return getValue(index, Data.CORRECT).equals("1");
	}

	public String startColumn(int index) {
		return getValue(index, Data.START_FOLIO_COL).split("\\.")[1];
	}

	public NarrativeTagging(Book book, String[][] table) {
		this.table = table;
		this.map = new int[numSections()];

		for (int section = 0; section < map.length; section++) {
			String folio = startFolio(section);

			if (folio != null) {
				map[section] = book.guessImage(folio);
			}
		}
	}

	public int numSections() {
		return table.length;
	}

	public String getValue(int index, Data d) {
		// Check for ragged rows
		if (d.ordinal() >= table[index].length) {
			return "";
		}

		return table[index][d.ordinal()];
	}

	public Widget displaySection(int index, NarrativeSectionsTable narsecs) {
		Document htmldoc = com.google.gwt.dom.client.Document.get();
		final Element htmldiv = htmldoc.createDivElement();

		Element titlespan = htmldoc.createSpanElement();
		titlespan.setClassName("ImageDescriptionLabel");

		String secid = getValue(index, Data.SECTION_ID);
		int sec = narsecs.findSection(secid);

		String text = secid + (isChecked(index) ? ": " : "? ");

		if (sec != -1) {
			text += narsecs.getDescription(sec);
		}

		titlespan.setInnerText(text);

		htmldiv.appendChild(titlespan);
		htmldiv.appendChild(htmldoc.createBRElement());

		String trans = getValue(index, Data.TRANSCRIPTION);
		String translecoy = getValue(index, Data.TRANSCRIPTION_LECOY);

		if (!trans.isEmpty()) {
			Element span = htmldoc.createSpanElement();
			span.setInnerText(trans);
			htmldiv.appendChild(span);
		}

		if (!translecoy.isEmpty()) {
			Element span = htmldoc.createSpanElement();
			span.setClassName("TranscriptionLecoy");
			span.setInnerText(" L" + translecoy);
			htmldiv.appendChild(span);
		}

		return new Widget() {
			{
				setElement(htmldiv);
			}
		};
	}

	/**
	 * Return sections indexes for given image in a book.
	 */
	public List<Integer> findImageIndexes(Book book, int image) {
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < map.length; i++) {
			if (map[i] == image) {
				result.add(i);
			}
		}

		return result;
	}
}
