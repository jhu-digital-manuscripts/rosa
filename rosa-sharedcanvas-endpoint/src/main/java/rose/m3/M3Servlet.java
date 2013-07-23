package rose.m3;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;

import de.dfki.km.json.JSONUtils;
import de.dfki.km.json.jsonld.JSONLD;
import de.dfki.km.json.jsonld.JSONLDProcessingError;
import de.dfki.km.json.jsonld.JSONLDProcessor.Options;
import de.dfki.km.json.jsonld.impl.JenaJSONLDSerializer;

public class M3Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int MAX_CACHE_SIZE = 100;
    // Request URI -> output
    private static final Map<String, byte[]> cache = new ConcurrentHashMap<String, byte[]>();

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

    // TODO Eventually cache all output methods

    private void write(Model model, ResultFormat fmt, OutputStream os,
            String cache_key) throws IOException {
        if (fmt == ResultFormat.XML) {
            model.getWriter("RDF/XML-ABBREV").write(model, os, null);
        } else if (fmt == ResultFormat.JAVASCRIPT || fmt == ResultFormat.JSON) {
            JenaJSONLDSerializer serializer = new JenaJSONLDSerializer();
            try {
                Object json = JSONLD.fromRDF(model, serializer);

                Map<String, Object> context = new HashMap<String, Object>();

                context.put("sc", "http://www.shared-canvas.org/ns/");
                context.put("ore", "http://www.openarchives.org/ore/terms/");
                context.put("foaf", "http://xmlns.com/foaf/0.1/");
                context.put("oa", "http://www.w3.org/ns/oa#");
                context.put("exif", "http://www.w3.org/2003/12/exif/ns/");
                context.put("dcmi", "http://purl.org/dc/dcmitype/");
                context.put("cnt", "http://www.w3.org/2011/content#");
                context.put("dcterms", "http://purl.org/dc/terms/");
                context.put("dc", "http://purl.org/dc/elements/1.1/");
                context.put("rdf",
                        "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
                context.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
                context.put("xsd", "http://www.w3.org/2001/XMLSchema#");

                // TODO Make this configurable
                context.put("rose.data", "http://romandelarose.org/data/");
                context.put("rose.sc", "http://rosetest.library.jhu.edu/sc/");

                Options opts = new Options();
                opts.optimize = true;
                opts.graph = true;
                opts.useRdfType = true;

                // Must compact to turn into a graph and use context
                json = JSONLD.compact(json, context, opts);

                // JSONUtils.toPrettyString(json);

                byte[] output = JSONUtils.toString(json).getBytes("UTF-8");
                os.write(output);

                if (cache.size() > MAX_CACHE_SIZE) {
                    cache.clear();
                }

                cache.put(cache_key, output);
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

    private Model createModel(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String bookid = getResource(req);
        String type = null;

        ResourceMap resmap = new ResourceMap();
        Model model;

        String url = req.getRequestURL().toString();

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if (bookid == null) {
            model = resmap.modelCollection(url, col);
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
                return null;
            } else {
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
                        return null;
                    }

                    String image_frag = type.substring(start, end);
                    model = resmap.modelAllAnnotationsOfCanvas(url, book,
                            image_frag);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
                            "Unknown resource map requested: " + type);
                    return null;
                }
            }
        }

        return model;
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

        String cache_key = req.getRequestURL().toString() + fmt.name();

        byte[] cache_output = cache.get(cache_key);

        OutputStream os = resp.getOutputStream();

        String jsoncallback = req.getParameter("callback");

        if ((fmt == ResultFormat.JSON || fmt == ResultFormat.JAVASCRIPT)
                && jsoncallback != null) {
            os.write(jsoncallback.getBytes("UTF-8"));
            os.write('(');
        }

        if (cache_output == null) {
            Model model = createModel(req, resp);

            if (model != null) {
                write(model, fmt, os, cache_key);
            }
        } else {
            os.write(cache_output);
        }

        if ((fmt == ResultFormat.JSON || fmt == ResultFormat.JAVASCRIPT)
                && jsoncallback != null) {
            os.write(')');
        }

        resp.flushBuffer();
    }
}
