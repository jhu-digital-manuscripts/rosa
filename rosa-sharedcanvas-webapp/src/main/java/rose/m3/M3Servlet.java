package rose.m3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFWriter;

// TODO enum for endpoints...

public class M3Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// TODO auth issues
	// private static final String ROSE_DATA_URL =
	// "http://rosetest.library.jhu.edu/data/";
	private static final String ROSE_DATA_URL = "http://romandelarose.org/data/";
	private static final String BOOKS_EN_CSV = "books.csv";

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

		public RDFWriter writer(Model model) {
			if (this == XML) {
				return model.getWriter("RDF/XML-ABBREV");
			} else if (this == JAVASCRIPT || this == JSON) {
				return new JsonJenaWriter();
			} else if (this == N3) {
				return model.getWriter("N3");
			} else {
				return null;
			}
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
			InputStream is = new URL(ROSE_DATA_URL + BOOKS_EN_CSV).openStream();
			col = new RoseCollection(new CSVSpreadSheet(new InputStreamReader(
					is, "UTF-8")), ROSE_DATA_URL);
			is.close();
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
			model = resmap.model(req.getRequestURL().toString(), col);
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
				if (type == null) {
					model = resmap.modelManifest(
							req.getRequestURL().toString(), book);
				} else if (type.equals("seq")) {
					model = resmap.modelReadingSequence(req.getRequestURL()
							.toString(), book);
				} else if (type.equals("trans")) {
					model = resmap.modelTranscriptionAnnotations(req
							.getRequestURL().toString(), book);
				} else if (type.equals("illus")) {
					model = resmap.modelIllustrationDescriptionAnnotations(req
							.getRequestURL().toString(), book);
				} else if (type.equals("images")) {
					model = resmap.modelImageAnnotations(req.getRequestURL()
							.toString(), book);
				} else {
					resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE,
							"Unknown resource map type requested: " + type);
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

		fmt.writer(model).write(model, os, null);

		if ((fmt == ResultFormat.JSON || fmt == ResultFormat.JAVASCRIPT)
				&& jsoncallback != null) {
			os.write(')');
		}

		resp.flushBuffer();
	}
}
