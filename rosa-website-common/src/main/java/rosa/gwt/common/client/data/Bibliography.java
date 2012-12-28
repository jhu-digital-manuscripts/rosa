package rosa.gwt.common.client.data;

import rosa.gwt.common.client.Util;
import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Format RefWorks XML according to
 * http://wiki.library.jhu.edu/display/ROSE/Bibliographies
 * 
 * Uses google book search API to dynamically link isbns.
 */
public class Bibliography {
    private final String xml;

    public Bibliography(String xml) {
        // Fix bizarre issue with refworks apostrophes

        this.xml = xml.replace("&amp;#39;", "´");
    }

    private String getNames(Element ref, String tag, String sep) {
        StringBuilder sb = new StringBuilder();

        NodeList l = ref.getElementsByTagName(tag);

        if (l.getLength() == 0) {
            return null;
        }

        for (int i = 0; i < l.getLength(); i++) {
            if (sb.length() > 0) {
                if (l.getLength() == 2) {
                    sb.append(sep + " and ");
                } else {
                    sb.append(sep + " ");
                }
            }

            // Refworks remove spaces after commas...
            String name = Util.extractText(l.item(i));
            name = name.replace(",", ", ");

            sb.append(name);
        }

        sb.append(".");

        return sb.toString();
    }

    private void addText(com.google.gwt.dom.client.Document htmldoc,
            com.google.gwt.dom.client.Element parent, String s) {
        com.google.gwt.dom.client.Element span = htmldoc.createSpanElement();
        span.setInnerText(s);
        parent.appendChild(span);
    }

    private void addUl(com.google.gwt.dom.client.Document htmldoc,
            com.google.gwt.dom.client.Element parent, String s) {
        com.google.gwt.dom.client.Element span = htmldoc.createSpanElement();
        span.setInnerText(s);
        span.setAttribute("style", "text-decoration: underline;");
        parent.appendChild(span);
    }
    
    private void display(com.google.gwt.dom.client.Document htmldoc,
            Element ref, com.google.gwt.dom.client.Element parent) {
        String type = Util.getFirstElementValue(ref, "rt");

        if (type == null) {
            Window.alert(type);
            return;
        }

        String a1 = getNames(ref, "a1", ",");
        String a2 = getNames(ref, "a2", "");
        String pp = Util.getFirstElementValue(ref, "pp");
        String pb = Util.getFirstElementValue(ref, "pb");
        String yr = Util.getFirstElementValue(ref, "yr");
        String sp = Util.getFirstElementValue(ref, "sp");
        String op = Util.getFirstElementValue(ref, "op");
        String t1 = Util.getFirstElementValue(ref, "t1");
        String t2 = Util.getFirstElementValue(ref, "t2");
        String vo = Util.getFirstElementValue(ref, "vo");

        if (sp.matches("\\d+")) {
            sp = "p. " + sp;
        }
        
        if (type.equals("Book, Whole")) {
            addText(htmldoc, parent, a1 + " ");
            addUl(htmldoc, parent, t1 + ".");
            addText(htmldoc, parent, " " + pp + ": " + pb + ", " + yr + ".");
        } else if (type.equals("Book, Section")) {
            if (t1 == null && t2 != null) {
                t1 = t2;
                t2 = null;
            }

            if (a1 == null && a2 != null) {
                a1 = a2;
                a2 = null;
            }

            addText(htmldoc, parent, a1 + " ");

            if (t2 == null) {
                addUl(htmldoc, parent, t1 + ".");

                if (vo != null) {
                    addText(htmldoc, parent, " Vol. " + vo + ".");
                }

                addText(htmldoc, parent, " " + pp + ": " + pb + ", " + yr);

                if (sp != null && op != null) {
                    addText(htmldoc, parent, ", " + sp + "-" + op);
                } else if (sp != null) {
                    addText(htmldoc, parent, ", " + sp);
                }

                addText(htmldoc, parent, ".");
            } else {
                addText(htmldoc, parent, " \"" + t1 + ".\" ");
                addUl(htmldoc, parent, t2 + ".");

                if (a2 != null) {
                    addText(htmldoc, parent, " Ed. " + a2);
                }

                if (vo != null) {
                    addText(htmldoc, parent, " Vol. " + vo + ".");
                }

                addText(htmldoc, parent, " " + pp + ": " + pb + ", " + yr);

                if (sp != null && op != null) {
                    addText(htmldoc, parent, ", " + sp + "-" + op);
                } else if (sp != null) {
                    addText(htmldoc, parent, ", " + sp);
                }

                addText(htmldoc, parent, ".");
            }

        } else if (type.contains("Journal")) {
            String jf = Util.getFirstElementValue(ref, "jf");

            addText(htmldoc, parent, a1 + " \"" + t1 + ".\" ");
            addUl(htmldoc, parent, jf);
            addText(htmldoc, parent, " " + vo + " (" + yr + "): ");

            if (sp != null && op != null) {
                addText(htmldoc, parent, " " + sp + "-" + op + ".");
            }
        } else {
            return;
        }

    }

    private String findISBN13(String s) {
        for (String part : s.split("\\s+")) {
            if (part.length() == 13) {
                return part;
            }
        }

        return null;
    }

    private String getString(JSONObject o, String key) {

        if (o.containsKey(key)) {
            return ((JSONString) o.get(key)).stringValue();
        } else {
            return null;
        }
    }

    public Widget display() {
        final com.google.gwt.dom.client.Document htmldoc = com.google.gwt.dom.client.Document
                .get();

        if (xml.length() == 0) {
            return new Label(Labels.INSTANCE.bibliographyUnavailable());
        }

        final com.google.gwt.dom.client.Element htmldiv = htmldoc
                .createDivElement();
        htmldiv.setClassName("Bibliography");

        Document doc;

        try {
            doc = XMLParser.parse(xml);
        } catch (com.google.gwt.xml.client.DOMException e) {
            return new Label(e.getMessage());
        }

        NodeList refs = doc.getElementsByTagName("reference");
        com.google.gwt.dom.client.TableElement table = htmldoc
                .createTableElement();
        htmldiv.appendChild(table);

        StringBuilder isbns = new StringBuilder();

        for (int i = 0; i < refs.getLength(); i++) {
            Element ref = (Element) refs.item(i);

            com.google.gwt.dom.client.TableRowElement row = table.insertRow(-1);

            com.google.gwt.dom.client.Element num = row.insertCell(-1);
            com.google.gwt.dom.client.Element cit = row.insertCell(-1);
            com.google.gwt.dom.client.Element goog = row.insertCell(-1);

            num.setInnerText((i + 1) + ".");

            String isbn = Util.getFirstElementValue(ref, "sn");

            if (isbn != null) {
                isbn = findISBN13(isbn);
            }

            if (isbn != null) {
                goog.setId(isbn);

                if (isbns.length() > 0) {
                    isbns.append(",");
                }
                isbns.append(isbn);
            }

            display(htmldoc, ref, cit);
        }

        String booklookupurl = "http://books.google.com/books?jscmd=viewapi&bibkeys="
                + isbns + "&callback=";

        JsonpRequestBuilder jrb = new JsonpRequestBuilder();

        jrb.requestObject(booklookupurl, new AsyncCallback<JavaScriptObject>() {
            public void onFailure(Throwable caught) {
                Window.alert("Failure getting entity: " + caught.getMessage());
            }

            public void onSuccess(JavaScriptObject obj) {
                if (obj == null) {
                    return;
                }

                JSONObject result = new JSONObject(obj);

                for (String isbn : result.keySet()) {
                    JSONObject book = (JSONObject) result.get(isbn);

                    String thumburl = getString(book, "thumbnail_url");
                    String prevurl = getString(book, "preview_url");

                    com.google.gwt.dom.client.Element goog = htmldoc
                            .getElementById(isbn);

                    if (goog != null && prevurl != null) {
                        com.google.gwt.dom.client.AnchorElement a = htmldoc
                                .createAnchorElement();
                        a.setHref(prevurl);

                        com.google.gwt.dom.client.ImageElement img = htmldoc
                                .createImageElement();

                        if (thumburl == null) {
                            thumburl = "http://code.google.com/apis/books/images/gbs_preview_button1.gif";
                        }

                        img.setSrc(thumburl);
                        img.setTitle("Google books");

                        goog.appendChild(a);
                        a.appendChild(img);
                    }
                }
            }
        });

        return new Widget() {
            {
                setElement(htmldiv);
            }
        };
    }
}
