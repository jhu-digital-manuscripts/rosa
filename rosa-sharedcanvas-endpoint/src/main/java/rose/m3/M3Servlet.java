package rose.m3;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;

import de.dfki.km.json.JSONUtils;
import de.dfki.km.json.jsonld.JSONLD;
import de.dfki.km.json.jsonld.JSONLDProcessingError;
import de.dfki.km.json.jsonld.impl.JenaJSONLDSerializer;

// TODO enum for endpoints...

public class M3Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RoseCollection col;

    public enum ResultFormat {
        XML("application/xml"), JSON("application/json"), JAVASCRIPT(
                "text/javascript"), N3("application/n3");

        private final String mimetype;

        ResultFormat(String format) {
            this.mimetype = format;
        }

        public String mimeType() {
            return mimetype;
        }

        public static ResultFormat find(HttpServletRequest req) {
            String type = req.getHeader("Accept");

            if (type == null || type.isEmpty() || type.startsWith("*/")) {
                // Handle case of <script src> not being able to set headers
                String jsoncallback = req.getParameter("callback");

                if (jsoncallback != null) {
                    return JAVASCRIPT;
                }

                return XML;
            }

            // TODO correctly parse Accept header

            for (ResultFormat fmt : values()) {
                if (type.contains(fmt.mimetype)) {
                    return fmt;
                }
            }

            return null;
        }
    }

    private void write(Model model, ResultFormat fmt, OutputStream os)
            throws IOException {
        if (fmt == ResultFormat.XML) {
            model.getWriter("RDF/XML-ABBREV").write(model, os, null);
        } else if (fmt == ResultFormat.JAVASCRIPT || fmt == ResultFormat.JSON) {
            JenaJSONLDSerializer serializer = new JenaJSONLDSerializer();
            try {
                Object json = JSONLD.fromRDF(model, serializer);

                String output = JSONUtils.toPrettyString(json);

                os.write(output.getBytes("UTF-8"));
            } catch (JSONLDProcessingError e) {
                throw new RuntimeException("JSON LD error", e);
            }
        } else if (fmt == ResultFormat.N3) {
            model.getWriter("N3").write(model, os, null);
        } else {
            throw new RuntimeException("Unknown format: " + fmt);
        }
    }

    /**
     * Obtain the requested resource from the HttpServletRequest. The resource
     * is encoded in the path of the request URL.
     * 
     * @param req
     *            the HttpServletRequest
     * @return resource specified by request
     */
    private static String getResource(HttpServletRequest req) {
        String path = req.getPathInfo();

        if (path == null || path.length() < 2 || path.charAt(0) != '/') {
            return null;
        }

        return path.substring(1);
    }

    public void init(ServletConfig config) throws ServletException {
        try {
            col = new RoseCollection();
        } catch (IOException e) {
            throw new ServletException(e);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        ResultFormat fmt = ResultFormat.find(req);

        if (fmt == null) {
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                    "Unknown response format requested");
            return;
        }

        resp.setContentType(fmt.mimeType());
        resp.setCharacterEncoding("UTF-8");

        String bookid = getResource(req);
        String type = null;

        ResourceMap resmap = new ResourceMap();
        OutputStream os = resp.getOutputStream();
        Model model;

        if (bookid == null) {
            model = resmap.modelCollection(req.getRequestURL().toString(), col);
        } else {
            int i = bookid.indexOf('/');

            if (i != -1) {
                type = bookid.substring(i + 1);
                bookid = bookid.substring(0, i);
            }

            RoseCollection.Book book = col.findBook(bookid);

            if (book == null) {
                resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                        "Unknown book requested: " + bookid);
                return;
            } else {
                String url = req.getRequestURL().toString();

                if (type == null) {
                    model = resmap.modelManifest(url, book);
                } else if (type.equals("sequence")) {
                    model = resmap.modelReadingSequence(url, book);
                } else if (type.equals("annotations")) {
                    model = resmap.modelAllAnnotations(url, book);
                } else if (type.equals("annotations/transcription")) {
                    model = resmap.modelTranscriptionAnnotations(url, book);
                } else if (type.equals("annotations/illustration")) {
                    model = resmap.modelIllustrationDescriptionAnnotations(url,
                            book);
                } else if (type.equals("annotations/image")) {
                    model = resmap.modelImageAnnotations(url, book);
                } else if (type.startsWith("canvas/")
                        && type.endsWith("/annotations")) {
                    int start = type.indexOf("canvas/") + "canvas/".length();
                    int end = type.indexOf("/annotations");

                    if (start >= end) {
                        resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                                "Unknown resource map requested: " + type);
                        return;
                    }

                    String image_frag = type.substring(start, end);
                    model = resmap.modelAllAnnotationsOfCanvas(url, book,
                            image_frag);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                            "Unknown resource map requested: " + type);
                    return;
                }
            }
        }

        String jsoncallback = req.getParameter("callback");

        if ((fmt == ResultFormat.JSON || fmt == ResultFormat.JAVASCRIPT)
                && jsoncallback != null) {
            os.write(jsoncallback.getBytes("UTF-8"));
            os.write('(');
        }

        write(model, fmt, os);

        if ((fmt == ResultFormat.JSON || fmt == ResultFormat.JAVASCRIPT)
                && jsoncallback != null) {
            os.write(')');
        }

        resp.flushBuffer();
    }
}
