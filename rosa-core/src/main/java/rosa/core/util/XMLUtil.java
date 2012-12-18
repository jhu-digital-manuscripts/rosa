package rosa.core.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtil {
	private static DocumentBuilder docBuilder;
	private static SAXTransformerFactory transformerFactory;

	public static Document createDocument(InputSource input)
			throws IOException, SAXException {

		if (docBuilder == null) {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			try {
				docBuilder = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}

		return docBuilder.parse(input);
	}

	public static Document createDocument() throws SAXException {
		if (docBuilder == null) {
			DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();
			fact.setNamespaceAware(true);
			try {
				docBuilder = fact.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}

		return docBuilder.newDocument();
	}

	public static Document createDocument(String xml) throws IOException,
			SAXException {
		return createDocument(new InputSource(new StringReader(xml)));
	}

	public static Document createDocument(File file) throws IOException,
			SAXException {
		return createDocument(new InputSource(new FileReader(file)));
	}

	public static String DateToXSD_DateTime(Date d) {
		DatatypeFactory f;
		try {
			f = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);

		return f.newXMLGregorianCalendar(cal).toXMLFormat();
	}

	public static Date XSD_DateTime_ToDate(String datetime)
			throws ParseException {
		DatatypeFactory f;

		try {
			f = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		return f.newXMLGregorianCalendar(datetime).toGregorianCalendar()
				.getTime();
	}

	public static TransformerHandler newTransformerHandler()
			throws TransformerConfigurationException {
		if (transformerFactory == null) {
			transformerFactory = (SAXTransformerFactory) SAXTransformerFactory
					.newInstance();
		}

		return transformerFactory.newTransformerHandler();
	}

	// TODO more general serialize
	public static String toString(Node n) throws IOException,
			TransformerException {
		TransformerHandler hd = newTransformerHandler();
		Transformer serializer = hd.getTransformer();

		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(n);
		StringWriter w = new StringWriter();
		StreamResult result = new StreamResult(w);

		serializer.transform(source, result);

		return w.toString();
	}

	public static String extractText(Node n) {
		StringBuffer buf = new StringBuffer();
		extractText(n, buf);

		return buf.toString().trim();
	}

	private static void extractText(Node n, StringBuffer buf) {
		if (n.getNodeType() == Node.TEXT_NODE) {
			buf.append(n.getNodeValue());
		}

		for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
			extractText(n, buf);
		}
	}

	public static void removeChildren(Node parent) {
		for (;;) {
			Node n = parent.getFirstChild();

			if (n == null) {
				break;
			}

			parent.removeChild(n);
		}
	}

	public static Element findElement(Document doc, String elementNS,
			String elementName, String attrName, String attrValue) {
		return findElement(doc.getDocumentElement(), elementNS, elementName,
				attrName, attrValue);
	}

	public static Element findElement(Element parent, String elementNS,
			String elementName, String attrName, String attrValue) {
		NodeList l = parent.getElementsByTagNameNS(elementNS, elementName);

		for (int i = 0; i < l.getLength(); i++) {
			Element e = (Element) l.item(i);
			String val = e.getAttribute(attrName);

			if (val != null && val.equals(attrValue)) {
				return e;
			}
		}

		return null;
	}
}
