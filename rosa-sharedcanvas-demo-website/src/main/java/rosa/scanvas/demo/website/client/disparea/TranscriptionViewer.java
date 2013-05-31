package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

// TODO update refactor this
// Whenever text is added to the DOM, must normalize spacing

/**
 * Provides methods to decode TEI transcription data and embed the
 * data in an on screen viewable format.
 */
public class TranscriptionViewer {
	// Possible tags: note, rhyme, expan, add, rend, del, add

	private static void displayTranscriptionPoetry(
			com.google.gwt.dom.client.Document htmldoc,
			com.google.gwt.dom.client.Element display, Node parent,
			boolean lecoy) {
		for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
			String name = n.getNodeName();

			Element e = n.getNodeType() == Node.ELEMENT_NODE ? (Element) n
					: null;

			if (name.equals("note")) {
				if (e.hasAttribute("type")
						&& e.getAttribute("type").equals("scribalPunc")) {
					display.appendChild(span(htmldoc, n, null));
				} else {
					AnchorElement anchor = htmldoc.createAnchorElement();
					anchor.setClassName("Tooltip");
					anchor.setHref("#");
					anchor.setAttribute("onClick", "return false;");
					anchor.appendChild(htmldoc.createTextNode("*"));
					anchor.appendChild(span(htmldoc, n, null));
					display.appendChild(anchor);
				}
			} else if (name.equals("expan")) {
				display.appendChild(span(htmldoc, n, "TranscriptionExpan"));
			} else if (name.equals("hi")) {
				String rend = e.getAttribute("rend");

				if (rend != null && rend.equals("init")) {
					display
							.appendChild(span(htmldoc, n,
									"TranscriptionInitial"));
				} else if (rend != null && rend.equals("rubric")) {
					com.google.gwt.dom.client.Element span = htmldoc
							.createSpanElement();
					span.setClassName("TranscriptionRubric");

					displayTranscriptionPoetry(htmldoc, span, n, lecoy);
					display.appendChild(span);
				} else if (rend != null && rend.equals("nota")) {
					display.appendChild(span(htmldoc, n, null));
				} else {
					Window.alert("Unknown hi rend: " + rend);
				}
			} else if (name.equals("add")) {
				display.appendChild(span(htmldoc, n, "TranscriptionAdd"));
			} else if (name.equals("del")) {
				display.appendChild(span(htmldoc, n, "TranscriptionDel"));
			} else if (n.getNodeType() == Node.TEXT_NODE) {
				String s = Util.extractText(n).replaceAll("\\s+", " ");
				display.appendChild(htmldoc.createTextNode(s));
			} else if (name.equals("milestone")) {
				if (lecoy) {
					String num = e.getAttribute("n");
					display.appendChild(span(htmldoc, " L" + num,
							"TranscriptionLecoy"));
				}
			} else if (name.equals("gap")) {
			} else {
				Window.alert("Unhandled node: " + name);
			}
		}
	}

	private static com.google.gwt.dom.client.Element span(
			com.google.gwt.dom.client.Document htmldoc, String text,
			String domclass) {
		com.google.gwt.dom.client.Element span = htmldoc.createSpanElement();

		if (domclass != null) {
			span.setClassName(domclass);
		}

		span.setInnerText(text.replaceAll("\\s+", " "));

		return span;
	}

	private static com.google.gwt.dom.client.Element span(
			com.google.gwt.dom.client.Document htmldoc, Node node,
			String domclass) {
		return span(htmldoc, Util.extractText(node), domclass);
	}

	// Return current node in the node being appended to
	private static com.google.gwt.dom.client.Element displayTranscription(
			TabLayoutPanel tabs, com.google.gwt.dom.client.Document htmldoc,
			com.google.gwt.dom.client.Element display, String imagename,
			Node parent, int height, boolean lecoy) {

		for (Node n = parent.getFirstChild(); n != null; n = n.getNextSibling()) {
			if (n.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			Element el = (Element) n;
			String name = el.getNodeName();

			// Window.alert(name);

			if (name.equals("cb")) {
				SimplePanel w = new SimplePanel();
				ScrollPanel tabpanel = new ScrollPanel(w);

				display = w.getElement();
				tabs.add(tabpanel, imagename + " " + el.getAttribute("n"));

				continue;
			} else if (name.equals("lg")) {
				display = displayTranscription(tabs, htmldoc, display,
						imagename, el, height, lecoy);
				continue;
			} else if (name.equals("pb")) {
				continue;
			}

			// Handle case of content before cb
			if (display == null) {
				SimplePanel w = new SimplePanel();
				ScrollPanel tabpanel = new ScrollPanel(w);
				display = w.getElement();
				tabs.add(tabpanel, imagename);
			}

			if (name.equals("div")) {
				display.appendChild(span(htmldoc, Labels.INSTANCE.illustration()
						+ ": ", "TranscriptionExtraHeader"));
				display.appendChild(span(htmldoc, n, "TranscriptionExtra"));
				display.appendChild(htmldoc.createBRElement());
			} else if (name.equals("fw")) {
				display.appendChild(span(htmldoc, Labels.INSTANCE.catchphrase()
						+ ": ", "TranscriptionExtraHeader"));
				display.appendChild(span(htmldoc, n, "TranscriptionExtra"));
				display.appendChild(htmldoc.createBRElement());
			} else if (name.equals("l")) {

				displayTranscriptionPoetry(htmldoc, display, el, lecoy);

				// Hack to deal with milestone appearing as sibling rather than child of l
				for (Node next = n.getNextSibling();; next = next.getNextSibling()) {
					if (next == null) {
						display.appendChild(htmldoc.createBRElement());
						break;
					}
					
					if (next.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}

					// milestone will add the line break
					if (next.getNodeName().equals("milestone")) {
						break;
					} else {
						display.appendChild(htmldoc.createBRElement());
						break;
					}					
				}
			} else if (name.equals("milestone")) {
				if (lecoy) {
					String num = el.getAttribute("n");
					display.appendChild(span(htmldoc, " L" + num,
							"TranscriptionLecoy"));
				}
				
				display.appendChild(htmldoc.createBRElement());	
			} else {
				Window.alert("Unknown element " + name);
			}
		}

		return display;
	}

	public static TabLayoutPanel createTranscriptionViewer(String[] transxml,
			final String[] transnames, final int height, boolean lecoy) {
		final TabLayoutPanel tabs = new TabLayoutPanel(1.5, Unit.EM);
		tabs.addStyleName("Transcription");

		for (int i = 0; i < transxml.length; i++) {
			String xml = transxml[i];
			String name = transnames[i];

			if (xml != null) {
				try {
					Document doc = XMLParser.parse(xml);

					com.google.gwt.dom.client.Document htmldoc = com.google.gwt.dom.client.Document
							.get();

					displayTranscription(tabs, htmldoc, null, name, doc
							.getDocumentElement(), height, lecoy);
				} catch (DOMParseException e) {
					tabs.add(new Label(Labels.INSTANCE.transcriptionUnavailable()),
							name);
					// TODO
					Window.alert("Error parsing xml: " + e);
				}

			} else {
				tabs
						.add(new Label(Labels.INSTANCE.transcriptionUnavailable()),
								name);
			}
		}

		tabs.selectTab(0);

		return tabs;
	}
}
