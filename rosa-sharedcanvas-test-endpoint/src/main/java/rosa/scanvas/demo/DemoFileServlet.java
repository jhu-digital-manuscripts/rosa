package rosa.scanvas.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DemoFileServlet extends HttpServlet {
	
	/*
	 * Example: 
	 * .../rosa-sharedcanvas-test-endpoint/Manifest/
	 * .../rosa-sharedcanvas-test-endpoint/annotations/(ANNO_TYPE)
	 * .../rosa-sharedcanvas-test-endpoint/sequence/(SEQ_TYPE)
	 * .../rosa-sharedcanvas-test-endpoint/canvas/CANVAS_ID/annotations
	 */
	
	public void doGet (HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		ScDemoFile scDemo = new ScDemoFile();
		scDemo.retrieveFile(request, response);
		
	}
}