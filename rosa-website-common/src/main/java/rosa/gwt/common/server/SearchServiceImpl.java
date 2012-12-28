package rosa.gwt.common.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.lucene.queryParser.ParseException;

import rosa.gwt.common.client.RPCException;
import rosa.gwt.common.client.SearchService;
import rosa.search.SearchResult;
import rosa.search.LuceneSearcher;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements
		SearchService {
	private final static int MAX_MATCHES = 30;
	private LuceneSearcher searcher;

	public void init() throws ServletException {
		String s = getServletConfig().getInitParameter("index");

		if (s == null) {
			throw new ServletException("Index not specified");
		}

		File indexpath = new File(getServletContext().getRealPath(s));

		try {
			searcher = new LuceneSearcher(indexpath);
		} catch (IOException e) {
			throw new ServletException(e);
		}
	}

	public SearchResult search(String query, int offset, int maxmatches)
			throws RPCException {
		if (offset < 0 || maxmatches < 0) {
			throw new RPCException("Offset and maxmatches must be > 0");
		}

		if (maxmatches > MAX_MATCHES) {
			throw new RPCException("Max matches must be  <= " + MAX_MATCHES);
		}

		try {
			return searcher.search(query, offset, maxmatches);
		} catch (ParseException e) {
			throw new RPCException("Parse error: " + e + " while parsing: "
					+ query);
		} catch (IOException e) {
			throw new RPCException(e);
		}
	}

	public void destroy() {
		if (searcher != null) {
			try {
				searcher.close();
			} catch (IOException e) {
			}
		}
	}
}
