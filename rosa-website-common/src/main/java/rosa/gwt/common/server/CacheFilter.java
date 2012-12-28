package rosa.gwt.common.server;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Set expires header of urls containing .cache. to one year and urls in
 * starting with /data/ and images to one week.
 */

// TODO use ImageResource for images

public class CacheFilter implements Filter {
	private static long year = 31556926000l;
	private static long week = 604800000;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpreq = (HttpServletRequest) request;

		String uri = httpreq.getRequestURI();

		if (!uri.contains(".nocache.")) {
			if (uri.contains(".cache.")) {
				HttpServletResponse httpresp = (HttpServletResponse) response;
				httpresp.setDateHeader("Expires", new Date().getTime() + year);
			} else if (uri.contains("/data/") || uri.endsWith(".gif")
					|| uri.endsWith(".jpg") || uri.endsWith(".png")) {
				HttpServletResponse httpresp = (HttpServletResponse) response;
				httpresp.setDateHeader("Expires", new Date().getTime() + week);
			}
		}

		filterChain.doFilter(request, response);
	}

	public void destroy() {
	}

	public void init(FilterConfig config) throws ServletException {
	}
}
