package rosa.gwt.common.client.data;

import rosa.gwt.common.client.Action;
import rosa.gwt.common.client.HttpGet;
import rosa.gwt.common.client.Util;
import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

public class Book {
	public static final String IMAGES_NAME = "images.csv";
	private static final String PERMISSION_NAME = "permission";
	private static final String PERMISSION_SUFFIX = ".html";
	private static final String DESCRIPTION_NAME = "description";
	private static final String DESCRIPTION_SUFFIX = ".xml";
	private static final String TRANSCRIPTION_NAME = "transcription";

	public static final String ILLUSTRATIONS_NAME = "imagetag.csv";
	public static final String BIBLIOGRAPHY_NAME = "bibliography.xml";
	public static final String NARRATIVE_MAP_NAME = "nartag.csv";

	private final String id;
	private final String[][] images;
	private ImageTagging illus;
	private NarrativeTagging narmap;
	private Bibliography bib;

	private final int dataid;

	private final String perm;
	private final String desc;

	/**
	 * @param image
	 * @return manuscript name of an image
	 */
	public static String bookIDFromImage(String image) {
		return image.substring(0, image.indexOf('.'));
	}

	public static void load(String baseurl, final String bookid,
				final int bookdataindex, final String lc, final HttpGet.Callback<Book> topcb) {
		HttpGet.Callback<String[]> cb = new HttpGet.Callback<String[]>() {
			public void failure(String error) {
				topcb.failure(error);
			}

			public void success(String[] result) {
				try {

					if (result[0].length() == 0) {
						topcb.failure("Could not find permission");
					}

					// parse out permissions
					String perm = result[0];

					if (result[1].length() == 0) {
						topcb.failure("Could not find description");
					}

					String desc = result[1];

					// Parse ordered images
					String[][] images = Util.parseCSVTable(result[2]);

					Book book = new Book(bookid, images, perm, desc,
							bookdataindex);
					topcb.success(book);
				} catch (DOMParseException e) {
					topcb.failure("Parsing description: " + e);
				}
			}
		};

		String perm = baseurl + bookid + "/" + bookid + "." + PERMISSION_NAME
				+ "_" + lc + PERMISSION_SUFFIX;
		String desc = baseurl + bookid + "/" + bookid + "." + DESCRIPTION_NAME
				+ "_" + lc + DESCRIPTION_SUFFIX;
		String images = baseurl + bookid + "/" + bookid + "." + IMAGES_NAME;

		HttpGet.request(new String[] { perm, desc, images }, cb);
	}

	public Book(String id, String[][] images, String perm, String desc,
			int dataid) {
		this.id = id;
		this.images = images;
		this.perm = perm;
		this.desc = desc;
		this.dataid = dataid;
	}

	public String id() {
		return id;
	}

	public String getDescription() {
		return desc;
	}

	public String imageName(int index) {
		return images[index][0];
	}

	public int bookDataIndex() {
		return dataid;
	}

	public ImageTagging illustrations() {
		return illus;
	}

	public NarrativeTagging narrativeMap() {
		return narmap;
	}

	public int findImage(String image) {
		for (int i = 0; i < images.length; i++) {
			if (image.equalsIgnoreCase(images[i][0])
					|| (images[i][0].startsWith("*") && image
							.equalsIgnoreCase(images[i][0].substring(1)))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * @param image
	 * @return path to transcription of image
	 */
	public static String transcriptionPath(String image) {
		int start = image.indexOf('.');
		int end = image.lastIndexOf('.');
		String bookid = image.substring(0, start);

		return bookid + "/" + bookid + "." + TRANSCRIPTION_NAME + "."
				+ image.substring(start + 1, end) + ".xml";
	}

	public static boolean isMissingImage(String image) {
		return image.charAt(0) == '*';
	}

	public static boolean isRectoImage(String image) {
		return image.endsWith("r.tif");
	}

	public static boolean isRectoImage(int index) {
		return (index & 1) == 0;
	}

	public static boolean isVersoImage(String image) {
		return image.endsWith("v.tif");
	}

	public static boolean isBindingImage(String image) {
		return image.contains(".binding.");
	}

	public static boolean isFrontMatterImage(String image) {
		return image.contains(".frontmatter.");
	}

	public static boolean isEndMatterImage(String image) {
		return image.contains(".endmatter.");
	}

	public static boolean isFolioImage(String image) {
		return !isBindingImage(image) && !image.contains("matter.");
	}

	/**
	 * Return image name with book id and extension stripped.
	 */
	public static String shortImageName(String name) {
		int start = name.indexOf('.') + 1;

		// Also strip leading 0
		while (name.charAt(start) == '0') {
			start++;
		}

		int end = name.lastIndexOf('.');

		return name.substring(start, end);
	}

	public String imagePermissionStatement() {
		return perm;
	}

	public Widget displayDescription(Repository col) {
		com.google.gwt.dom.client.Document htmldoc = com.google.gwt.dom.client.Document
				.get();

		final com.google.gwt.dom.client.Element htmldiv = htmldoc
				.createDivElement();
		htmldiv.setClassName("BookDescription");

		com.google.gwt.dom.client.TableElement data = htmldoc
				.createTableElement();
		data.setClassName("BookDescriptionData");
		htmldiv.appendChild(data);

		// Display a header filled with the browse info in a table with two
		// columns

		Repository.Category[][] header = new Repository.Category[][] {
				{ Repository.Category.LOCATION,
						Repository.Category.NUM_FOLIOS },
				{ Repository.Category.TYPE,
						Repository.Category.NUM_ILLUSTRATIONS },
				{ Repository.Category.DATE,
						Repository.Category.TRANSCRIPTION },
				{ Repository.Category.ORIGIN,
						Repository.Category.ILLUSTRATION_TAGGING }, };

		for (Repository.Category[] row : header) {
			TableRowElement tr = data.insertRow(-1);

			for (Repository.Category d : row) {
				TableCellElement td1 = tr.insertCell(-1);
				TableCellElement td2 = tr.insertCell(-1);

				td1.setInnerText(d.display() + ": ");
				td1.setClassName("BookDescriptionDataName");

				td2.setInnerText(col.bookData(bookDataIndex(), d));
			}
		}

		// Transform the xml description

		Document doc;

		try {
			doc = XMLParser.parse(desc);
		} catch (com.google.gwt.xml.client.DOMException e) {
			return new Label(e.getMessage());
		}

		// Display table about texts

		displayTextsTable(doc, htmldoc, htmldiv);

		NodeList l = doc.getElementsByTagName("notesStmt");

		if (l.getLength() == 0) {
			com.google.gwt.dom.client.Element span = htmldoc
					.createSpanElement();
			span.setInnerText(Labels.INSTANCE.bookDescriptionUnavailable());

			span.setClassName("BookDescriptionHeader");
			htmldiv.appendChild(span);
			htmldiv.appendChild(htmldoc.createPElement());
		} else {
			Element notestat = (Element) l.item(0);
			NodeList notes = notestat.getElementsByTagName("note");

			for (int i = 0; i < notes.getLength(); i++) {
				Element note = (Element) notes.item(i);

				String title = note.getAttribute("rend");

				if (title != null && !title.isEmpty()) {
					com.google.gwt.dom.client.Element span = htmldoc
							.createSpanElement();
					span.setInnerText(title);

					span.setClassName("BookDescriptionHeader");
					htmldiv.appendChild(span);
				}

				NodeList kids = note.getChildNodes();

				for (int j = 0; j < kids.getLength(); j++) {
					htmldiv.appendChild(displayDescription(htmldoc,
							kids.item(j)));
				}
			}
		}

		return new Widget() {
			{
				setElement(htmldiv);
			}
		};
	}

	private void displayTextsTable(Document doc,
			com.google.gwt.dom.client.Document htmldoc,
			com.google.gwt.dom.client.Element parent) {
		com.google.gwt.dom.client.TableElement data = htmldoc
				.createTableElement();
		data.setClassName("BookDescriptionTextData");
		parent.appendChild(data);

		TableRowElement header = data.insertRow(-1);

		for (String name : new String[] { Labels.INSTANCE.text(),
				Labels.INSTANCE.folioRange(), Labels.INSTANCE.folios(),
				Labels.INSTANCE.illustrations() }) {
			TableCellElement td = header.insertCell(-1);

			td.setInnerText(name);
			td.setClassName("BookDescriptionDataName");
		}

		NodeList texts = doc.getElementsByTagName("msItem");

		for (int i = 0; i < texts.getLength(); i++) {
			Element text = (Element) texts.item(i);
			TableRowElement tr = data.insertRow(-1);

			TableCellElement td1 = tr.insertCell(-1);
			TableCellElement td2 = tr.insertCell(-1);
			TableCellElement td3 = tr.insertCell(-1);
			TableCellElement td4 = tr.insertCell(-1);

			String title = Util.getFirstElementValue(text, "title");

			if (title != null) {
				td1.setInnerText(title);
			}

			String folios = "";
			String illustrations = "";

			NodeList notes = text.getElementsByTagName("note");

			for (int j = 0; j < notes.getLength(); j++) {
				Element note = (Element) notes.item(j);

				if (note.getAttribute("type").equals("folios")) {
					folios = Util.extractText(note);
				} else if (note.getAttribute("type").equals("illustrations")) {
					illustrations = Util.extractText(note);
				}
			}

			td3.setInnerText(folios);
			td4.setInnerText(illustrations);

			td2.appendChild(displayLocus(htmldoc, (Element) text
					.getElementsByTagName("locus").item(0)));

		}
	}

	public static boolean isPaginatedImage(String image) {
		String s = image.substring(image.indexOf('.') + 1);
		return s.matches("\\d+\\.tif");
	}

	/**
	 * Return name of image matching fragment or -1.
	 * 
	 * @return
	 */
	public int guessImage(String frag) {
		frag = frag.trim();

		// try to guess whether or not the book is paginated
		boolean paginated = isPaginatedImage(images[images.length / 2][0]);

		if (!paginated) {
			if (frag.matches("\\d+")) {
				frag += "r";
			} else if (frag.matches("[a-zA-Z]\\d+")) {
				// Deals with printed books: A1
				frag = frag.toUpperCase() + "r";
			}
		}

		if (frag.matches("\\d[rRvV]?")) {
			frag = "00" + frag;
		} else if (frag.matches("\\d\\d[rRvV]?")) {
			frag = "0" + frag;
		}

		if (!frag.endsWith(".tif")) {
			frag += ".tif";
		}

		if (!frag.startsWith(id)) {
			frag = id + "." + frag;
		}

		return findImage(frag);
	}

	private com.google.gwt.dom.client.Node displayDescription(
			com.google.gwt.dom.client.Document htmldoc, Node node) {

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			String name = node.getNodeName();

			if (name.equals("p")) {
				com.google.gwt.dom.client.Element para = htmldoc
						.createPElement();

				for (Node n = node.getFirstChild(); n != null; n = n
						.getNextSibling()) {
					para.appendChild(displayDescription(htmldoc, n));
				}

				return para;
			} else if (name.equals("hi")) {
				com.google.gwt.dom.client.Element italic = htmldoc
						.createElement("i");

				for (Node n = node.getFirstChild(); n != null; n = n
						.getNextSibling()) {
					italic.appendChild(displayDescription(htmldoc, n));
				}

				return italic;
			} else if (name.equals("locus")) {
				return displayLocus(htmldoc, (Element) node);
			} else if (name.equals("lb")) {
				return htmldoc.createElement("br");
			} else if (name.equals("list")) {
				com.google.gwt.dom.client.TableElement table = htmldoc
						.createTableElement();

				NodeList items = ((Element) node).getElementsByTagName("item");

				for (int i = 0; i < items.getLength(); i++) {
					Element item = (Element) items.item(i);
					displayQuireItem(htmldoc, table, item);
				}

				return table;
			} else if (name.equals("material")) {
			    return htmldoc.createTextNode(Util.extractText(node));
			} else {
				Window.alert("Unhandled element: " + node.getNodeName());
				return null;
			}
		} else if (node.getNodeType() == Node.TEXT_NODE) {
			// Munch whitespace because of bug in ie
			return htmldoc.createTextNode(node.getNodeValue().replaceAll(
					"\\s+", " "));
		} else if (node.getNodeType() == Node.COMMENT_NODE) {
		    return htmldoc.createTextNode("");
		} else {
			Window.alert("Unhandled node type: " + node.getNodeName());
			return null;
		}
	}

	private void displayQuireItem(com.google.gwt.dom.client.Document htmldoc,
			TableElement table, Element item) {
		TableRowElement tr = table.insertRow(-1);

		TableCellElement td1 = tr.insertCell(-1);
		TableCellElement td2 = tr.insertCell(-1);
		TableCellElement td3 = tr.insertCell(-1);

		td1.setInnerText(item.getAttribute("n"));

		NodeList l = item.getElementsByTagName("locus");

		if (l.getLength() > 0) {
			Element locus = (Element) l.item(0);

			td2.appendChild(displayLocus(htmldoc, locus));

			for (Node n = locus.getNextSibling(); n != null; n = n
					.getNextSibling()) {
				td3.appendChild(displayDescription(htmldoc, n));
			}
		}
	}

	private com.google.gwt.dom.client.Node displayLocus(
			com.google.gwt.dom.client.Document htmldoc, Element el) {
		{
			String from = el.getAttribute("from");
			String to = el.getAttribute("to");
			String text = Util.extractText(el);

			if (from == null || to == null) {
				com.google.gwt.dom.client.Element a = htmldoc
						.createAnchorElement();

				int image = guessImage(text);

				if (image == -1) {
					return htmldoc.createTextNode(text);
				}

				a.appendChild(htmldoc.createTextNode(text));
				a.setAttribute("href", getImageURL(image));

				// TODO want to popup thumb. Need to remove wrapped widget...
				// Anchor w = Anchor.wrap(a);
				// w.addFocusHandler(new FocusHandler() {
				// public void onFocus(FocusEvent event) {
				//
				// }
				// });

				return a;
			} else {
				int fromimage = guessImage(from);
				int toimage = guessImage(to);

				if (fromimage == -1 || toimage == -1) {
					return htmldoc.createTextNode(text);
				}

				com.google.gwt.dom.client.Element span = htmldoc
						.createSpanElement();

				com.google.gwt.dom.client.Element a1 = htmldoc
						.createAnchorElement();
				com.google.gwt.dom.client.Element a2 = htmldoc
						.createAnchorElement();

				a1.setInnerText(from);
				a1.setAttribute("href", getImageURL(fromimage));

				a2.setInnerText(to);
				a2.setAttribute("href", getImageURL(toimage));

				span.appendChild(a1);
				span.appendChild(htmldoc.createTextNode("-"));
				span.appendChild(a2);

				return span;
			}
		}
	}

	private String getImageURL(int image) {
		String filename = images[image][0];

		if (filename.startsWith("*")) {
			filename = filename.substring(1);
		}

		return "#" + Action.READ_BOOK.toToken(filename);
	}

	public void setIllustrations(String data) {
		illus = new ImageTagging(this, Util.parseCSVTable(data));
	}

	public void setBibliography(String data) {
		bib = new Bibliography(data);
	}

	public Bibliography bibliography() {
		return bib;
	}

	public void setNarrativeMap(String data) {
		narmap = new NarrativeTagging(this, Util.parseCSVTable(data));
	}

	public String illustrationsPath() {
		return id + "/" + id + "." + ILLUSTRATIONS_NAME;
	}

	public String narrativeMapPath() {
		return id + "/" + id + "." + NARRATIVE_MAP_NAME;
	}

	public String bibliographyPath() {
		return id + "/" + id + "." + BIBLIOGRAPHY_NAME;
	}

	public int numImages() {
		return images.length;
	}
	
	// TODO
	public String[][] imagesTable() {
	    return images;
	}
}
