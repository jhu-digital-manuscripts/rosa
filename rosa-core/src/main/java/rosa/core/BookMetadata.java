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

/**
 * Work in progress to abstract out metadata from tei prose description.
 */

public class BookMetadata {
    public static String ROSE_TEXT_ID = "rose";

    private final Document doc;

    public BookMetadata(Reader input) throws IOException, SAXException {
        doc = XMLUtil.createDocument(new InputSource(input));
    }

    public BookMetadata(File file) throws IOException, SAXException {
        doc = XMLUtil.createDocument(file);
    }
    
    public enum Type {
        MANUSCRIPT, PRINTED_BOOK;
    }

    public static class Text {
        private final Element text_root;

        private Text(Element msitem) {
            this.text_root = msitem;
        }

        public int linesPerColumn() {
            return getInteger(text_root, "linesPerColumn");
        }

        public int columnsPerFolio() {
            return getInteger(text_root, "columnsPerFolio");
        }

        public int leavesPerGathering() {
            return getInteger(text_root, "leavesPerGathering");
        }

        public int numIllustrations() {
            return getInteger(text_root, "illustrations");
        }

        public int numPages() {
            return getInteger(text_root, "folios");
        }

        public String id() {
            return getString(text_root, "id");
        }
        
        public String title() {
            return getString(text_root, "title");
        }
        
        public String firstPage() {
            return getString(text_root, "firstFolio");
        }

        public String lastPage() {
            return getString(text_root, "lastFolio");
        }
    }

    public Text[] texts() {
        NodeList l = doc.getElementsByTagName("text");

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

    public String date() {
        return getString(doc.getDocumentElement(), "date");
    }

    public int dateYearStart() {
        return getInteger(doc.getDocumentElement(), "dateYearStart");
    }

    public int dateYearEnd() {
        return getInteger(doc.getDocumentElement(), "dateYearEnd");
    }

    public String location() {
        return getString(doc.getDocumentElement(), "location");
    }

    public String repository() {
        return getString(doc.getDocumentElement(), "repository");
    }

    public String shelfmark() {
        return getString(doc.getDocumentElement(), "shelfmark");
    }

    public String origin() {
        return getString(doc.getDocumentElement(), "origin");
    }

    public int width() {
        return getInteger(doc.getDocumentElement(), "width");
    }

    public int height() {
        return getInteger(doc.getDocumentElement(), "height");
    }

    public String dimensions() {
        int width = width();
        int height = height();

        return (height == -1 || width == -1) ? null : width + "x" + height
                + " mm";
    }

    private static String getString(Element el, String name) {
        return firstElementValue(el, name);
    }

    private static int getInteger(Element el, String name) {
        String s = getString(el, name);

        if (s == null || s.isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Property " + name + "not an integer "
                    + s);
        }

    }

    public int numIllustrations() {
        return getInteger(doc.getDocumentElement(), "illustrations");
    }

    public int numPages() {
        return getInteger(doc.getDocumentElement(), "numPages");
    }

    public Type type() {
        String s = getString(doc.getDocumentElement(), "type");
        
        if (s.equals("")) {
            
        }
        
        throw new RuntimeException();
    }

    public String commonName() {
        return getString(doc.getDocumentElement(), "commonName");
    }

    public String material() {
        return getString(doc.getDocumentElement(), "material");
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

            if (dateYearStart() == -1) {
                errors.add("Date notBefore attr not set");
            }

            if (dateYearEnd() == -1) {
                errors.add("Date notAfter attr not set");
            }

            if (location() == null) {
                errors.add("Current location not set");
            }

            width();
            height();

            if (material() == null) {
                errors.add("Material not set");
            }

            if (numPages() == -1) {
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

                if (text.numPages() == -1) {
                    errors.add("Text: Num folios not set");
                }

                if (text.numIllustrations() == -1) {
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
