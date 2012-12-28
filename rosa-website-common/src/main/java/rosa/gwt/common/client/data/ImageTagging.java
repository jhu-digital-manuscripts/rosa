package rosa.gwt.common.client.data;

import java.util.ArrayList;
import java.util.List;

import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class ImageTagging {
	private final String[][] table;
	private final int map[]; // For each illustration index, give image index in

	private enum Column {
		IMAGE_ID(null), FOLIO(null), TITLES(Labels.INSTANCE.illustrationTitle()), TEXTUAL_ELEMENTS(
				Labels.INSTANCE.textualElements()), INITIALS(Labels.INSTANCE.initials()), CHARACTERS(
				Labels.INSTANCE.illustrationChar()), COSTUME(Labels.INSTANCE.costume()), OBJECTS(
				Labels.INSTANCE.objects()), LANDSCAPE(Labels.INSTANCE.landscape()), ARCHITECTURE(
				Labels.INSTANCE.architecture()), OTHER(Labels.INSTANCE.other());

		private final String label;

		private Column(String label) {
			this.label = label;
		}

		public String label() {
			return label;
		}
	}

	public ImageTagging(Book book, String[][] table) {
		this.table = table;
		this.map = new int[numIllustrations()];

		for (int illus = 0; illus < map.length; illus++) {
			String folio = value(illus, Column.FOLIO);

			if (folio != null) {
				map[illus] = book.guessImage(folio);
			}
		}
	}

	public int numIllustrations() {
		return table.length - 1;
	}

	private String value(int imageindex, Column d) {
		// Check for ragged rows
		if (d.ordinal() >= table[imageindex + 1].length) {
			return "";
		}

		return table[imageindex + 1][d.ordinal()];
	}

	public Widget displayImage(int index) {
		Document htmldoc = com.google.gwt.dom.client.Document.get();
		final Element htmldiv = htmldoc.createDivElement();

		htmldiv.appendChild(htmldoc.createPElement());

		displayImageKeywords(htmldoc, htmldiv, index, Column.TITLES);
		displayImageKeywords(htmldoc, htmldiv, index, Column.TEXTUAL_ELEMENTS);
		displayImageKeywords(htmldoc, htmldiv, index, Column.INITIALS);
		displayImageKeywords(htmldoc, htmldiv, index, Column.CHARACTERS);
		displayImageKeywords(htmldoc, htmldiv, index, Column.COSTUME);
		displayImageKeywords(htmldoc, htmldiv, index, Column.OBJECTS);
		displayImageKeywords(htmldoc, htmldiv, index, Column.LANDSCAPE);
		displayImageKeywords(htmldoc, htmldiv, index, Column.ARCHITECTURE);
		displayImageKeywords(htmldoc, htmldiv, index, Column.OTHER);

		return new Widget() {
			{
				setElement(htmldiv);
			}
		};
	}

	private void displayImageKeywords(Document htmldoc, Element container,
			int index, Column d) {
		String val = value(index, d);

		if (val.isEmpty()) {
			return;
		}

		com.google.gwt.dom.client.Element label = htmldoc.createSpanElement();
		label.setClassName("ImageDescriptionLabel");
		label.setInnerText(d.label() + ": ");

		com.google.gwt.dom.client.Element text = htmldoc.createSpanElement();
		text.setClassName("ImageDescriptionText");
		text.setInnerText(val);

		container.appendChild(label);
		container.appendChild(text);
		container.appendChild(htmldoc.createBRElement());
	}

	/**
	 * Return illustration indexes for given image in a book.
	 */
	public List<Integer> findImageIndexes(int image) {
		List<Integer> result = new ArrayList<Integer>();

		for (int i = 0; i < map.length; i++) {
			if (map[i] == image) {
				result.add(i);
			}
		}

		return result;
	}
}
