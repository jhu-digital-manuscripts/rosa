package rosa.core;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import rosa.core.util.XMLUtil;

public class BookDescription {
	public static String ROSE_TEXT_ID = "rose";

	private final Document doc;
	private final File file;

	public BookDescription(Reader input) throws IOException, SAXException {
		doc = XMLUtil.createDocument(new InputSource(input));
		this.file = null;
	}

	public BookDescription(File file) throws IOException, SAXException {
		doc = XMLUtil.createDocument(file);
		this.file = file;
	}

	public static class Text {
		private final Element msitem;

		private Text(Element msitem) {
			this.msitem = msitem;
		}

		public int linesPerColumn() {
			return getIntegerNote(msitem, "linesPerColumn");
		}

		public int columnsPerFolio() {
			return getIntegerNote(msitem, "columnsPerFolio");
		}

		public int leavesPerGathering() {
			return getIntegerNote(msitem, "leavesPerGathering");
		}

		public int numIllustrations() {
			return getIntegerNote(msitem, "illustrations");
		}

		public int numFolios() {
			return getIntegerNote(msitem, "folios");
		}

		public String id() {
			return getStringNote(msitem, "textid");
		}
	}

	public Text[] texts() {
		NodeList l = doc.getElementsByTagName("msItem");

		Text[] result = new Text[l.getLength()];

		for (int i = 0; i < result.length; i++) {
			result[i] = new Text((Element) l.item(i));
		}

		return result;
	}

	private static String firstElementValue(Element el, String name) {
		NodeList list = el.getElementsByTagName(name);

		if (list.getLength() == 0) {
			return null;
		}

		return list.item(0).getTextContent();
	}

	private static int firstElementAttributeIntegerValue(Element el,
			String name, String attr) {
		NodeList list = el.getElementsByTagName(name);

		if (list.getLength() == 0) {
			return -1;
		}

		String s = ((Element) list.item(0)).getAttribute(attr);

		if (s == null || s.isEmpty()) {
			return -1;
		}

		return Integer.parseInt(s.trim());
	}

	public File file() {
		return file;
	}

	public String date() {
		return firstElementValue(doc.getDocumentElement(), "date");
	}

	public int yearStart() {
		return firstElementAttributeIntegerValue(doc.getDocumentElement(),
				"date", "notBefore");
	}

	public int yearEnd() {
		return firstElementAttributeIntegerValue(doc.getDocumentElement(),
				"date", "notAfter");
	}

	public String currentLocation() {
		return firstElementValue(doc.getDocumentElement(), "settlement");
	}

	public String repository() {
		return firstElementValue(doc.getDocumentElement(), "repository");
	}

	public String shelfmark() {
		return firstElementValue(doc.getDocumentElement(), "idno");
	}

	public String origin() {
		return firstElementValue(doc.getDocumentElement(), "pubPlace");
	}

	public int width() {
		String s = firstElementValue(doc.getDocumentElement(), "width");

		return s == null || s.isEmpty() ? -1 : Integer.parseInt(s.trim());
	}

	public int height() {
		String s = firstElementValue(doc.getDocumentElement(), "height");

		return s == null || s.isEmpty() ? -1 : Integer.parseInt(s.trim());
	}

	public String dimensions() {
		int width = width();
		int height = height();

		return (height == -1 || width == -1) ? null : width + "x" + height
				+ " mm";
	}

	private static String getStringNote(Element el, String type) {
		Element e = XMLUtil.findElement(el, "*", "note", "type", type);

		if (e == null) {
			return null;
		}

		return e.getTextContent().trim();
	}

	private static int getIntegerNote(Element el, String type) {
		String s = getStringNote(el, type);

		if (s == null || s.isEmpty()) {
			return -1;
		}

		return Integer.parseInt(s.trim());
	}

	public int numIllustrations() {
		return getIntegerNote(doc.getDocumentElement(), "illustrations");
	}

	public Document document() {
		return doc;
	}

	public int numFolios() {
		NodeList list = doc.getElementsByTagName("measure");

		if (list.getLength() == 0) {
			return -1;
		}

		Element e = (Element) list.item(0);
		String s = e.getAttribute("quantity").trim();

		if (s.isEmpty()) {
			return -1;
		}

		return Integer.parseInt(s);
	}

	public String type() {
		return getStringNote(doc.getDocumentElement(), "format");
	}

	public String commonName() {
		return getStringNote(doc.getDocumentElement(), "commonName");
	}

	public String material() {
		return getStringNote(doc.getDocumentElement(), "material");
	}

	public void check(List<String> errors) {
		try {
			if (commonName() == null) {
				errors.add("Common name not set");
			}

			if (type() == null) {
				errors.add("Type set");
			}

			if (date() == null) {
				errors.add("Date not set");
			}

			if (yearStart() == -1) {
				errors.add("Date notBefore attr not set");
			}

			if (yearEnd() == -1) {
				errors.add("Date notAfter attr not set");
			}

			if (currentLocation() == null) {
				errors.add("Current location not set");
			}

			width();
			height();

			if (material() == null) {
				errors.add("Material not set");
			}

			if (numFolios() == -1) {
				errors.add("Num folios not set");
			}

			if (texts().length == 0) {
				errors.add("No texts");
			}

			for (Text text : texts()) {
				if (text.linesPerColumn() == -1) {
					errors.add("Text: Lines per column not set");
				}

				if (text.columnsPerFolio() == -1) {
					errors.add("Text: Columns per folio not set");
				}

				if (text.numFolios() == -1) {
					errors.add("Text: Num folios not set");
				}
				
				if (text.numIllustrations()== -1) {
					errors.add("Text: Num illus not set");
				}
				
				
				if (text.id() == null) {
					errors.add("Text: Id not set");
				}
			}

			if (numIllustrations() == -1) {
				errors.add("Num illustrations not set");
			}

			if (type() == null) {
				errors.add("Type not set or not correct");
			}

			if (origin() == null) {
				errors.add("Origin not set");
			}

			if (repository() == null) {
				errors.add("Repository not set");
			}
		} catch (NumberFormatException e) {
			errors.add("Error parsing integer value: " + e.getMessage());
		}
	}
}
