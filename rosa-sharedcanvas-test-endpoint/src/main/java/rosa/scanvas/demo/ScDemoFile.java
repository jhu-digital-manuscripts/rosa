package rosa.scanvas.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class ScDemoFile {
	
	/**
	 * Separate URL path into tokens
	 * @param path
	 * @return
	 */
	private String[] parse(String path) {

		if (path.length()>0 && path.charAt(0)=='/') {
			path = path.substring(1);
		}

		String[] parts = path.split("/");

		for (int i=0; i<parts.length; i++) {
			try {
				parts[i] = URLDecoder.decode(parts[i], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		return parts;
	}
	
	/**
	 * controls the output
	 * @param path URL path
	 * @param in InputStream
	 * @param o OutputStream
	 * @throws IOException
	 * @throws RuntimeException
	 */
	private void processRequest(String path, InputStream in, OutputStream o) 
			throws IOException, RuntimeException {
		final String[] aggregate = {
				"f13v-Transcriptions.n3",
				"f14r-Transcriptions.n3",
				"f14v-Transcriptions.n3",
				"f15r-Transcriptions.n3",
				"f15v-Transcriptions.n3",
				"f16r-Transcriptions.n3",
		};
		String[] parts = parse(path);
		Model model = ModelFactory.createDefaultModel();
		/*if (parts.length == 0) {
			
			in = this.getClass().getClassLoader().getResourceAsStream(
					"ManifestCollection.n3");
			JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
			
		} else */if (parts.length == 1) {
			if (parts[0].equals("manifest")) {
				in = this.getClass().getClassLoader().getResourceAsStream(
						"Manifest.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
			} else if (parts[0].equals("sequence")) {
				in = this.getClass().getClassLoader().getResourceAsStream(
						"NormalSequence.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
			} else if (parts[0].equals("annotations")) {
				/*String[] agr = new String[aggregate.length+2];
				System.arraycopy(aggregate, 0, agr, 0, aggregate.length);
				agr[agr.length-1] = "ImageAnnotations.n3";
				agr[agr.length-2] = "IllustrationDescription.n3";
				
				model = JsonldJenaUtils.generateAggregateModel(agr, "N3");
				JsonldJenaUtils.writeJsonldFromModel(model, o);*/
				in = this.getClass().getClassLoader().getResourceAsStream(
						"Annotations.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
			} else if (parts[0].equals("") || parts[0] == null) {
				in = this.getClass().getClassLoader().getResourceAsStream(
						"ManifestCollection.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
			} else {
				throw new IOException("Unknown resource requested: " + path);
			}
		} else if (parts.length == 2) {
			// ../sequence/read
			if (parts[0].equals("sequence")) {
				if (parts[1].equals("read")) {
					in = this.getClass().getClassLoader().getResourceAsStream(
							"NormalSequence.n3");
					JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
				} else if (parts[2].equals("sequence2")) {
					in = this.getClass().getClassLoader().getResourceAsStream(
							"Sequence2.n3");
					JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
				} else {
					throw new IOException("Invalid Path: " + path);
				} 
			// ../annotations/image
			// ../annotations/illustration
			// ../annotations/transcription
			} else if (parts[0].equals("annotations")) {
				if (parts[1].equals("image")) {
					in = this.getClass().getClassLoader().getResourceAsStream(
							"ImageAnnotations.n3");
					JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
				} else if (parts[1].equals("transcription")) {
					model = JsonldJenaUtils.generateAggregateModel(aggregate, "N3");
					JsonldJenaUtils.writeJsonldFromModel(model, o);
				} else if (parts[1].equals("illustration")) {
					in = this.getClass().getClassLoader().getResourceAsStream(
							"IllustrationDescription.n3");
					JsonldJenaUtils.writeJsonldFromStream(in, o, "N3");
				} else {
					throw new IOException("Unknown "+parts[0]+" requested: "+parts[1]);
				}
			} 
		} else if (parts.length == 3) {
			// ../canvas/CANV_ID/annotations
			// ../canvas/CANV_ID/transcriptions
			if (parts[0].toLowerCase().equals("canvas")) {
				handleCanvas(parts[1], parts[2], model, o);
			}
		} else {
			throw new IOException("Invalid path: "+path);
		}		
		
	}
	
	
	private void handleCanvas(String canv, String type, Model model, OutputStream out) 
			throws IOException {
		
		try {
			// TODO handle if page specifier does not have numbers as first characters
			/*Integer page = Integer.parseInt(
					canv.substring(0, canv.length()-2));*/
			String side = canv.substring(canv.length()-1).toLowerCase();
			
			if (!side.equals("r") && !side.equals("v")) {
				throw new IOException("Invalid canvas specified: "+canv+
						" Canvas must end in 'r' or 'v'");
			}
			
			if (type.equals("annotations")) {
				/*model = JsonldJenaUtils.singleCanvasAggregateModel(canv, "N3");
				JsonldJenaUtils.writeJsonldFromModel(model, out);*/
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(
						"f"+canv+"-Annotations.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, out, "N3");
			} else if (type.equals("transcriptions")) {
				InputStream in = this.getClass().getClassLoader().getResourceAsStream(
						"f"+canv+"-Transcriptions.n3");
				JsonldJenaUtils.writeJsonldFromStream(in, out, "N3");
			} else {
				throw new IOException("Invalid type specified: "+type);
			}
			
		} catch (NumberFormatException e) {
			throw new IOException("Invalid canvas specified: "+canv);
		}
		
	}
	
	
	
	/**
	 * Gets file specified in URL path, outputs as JSON-LD
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void retrieveFile(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		response.setContentType("application/javascript");
		response.setCharacterEncoding("UTF-8");
		
		OutputStream o = response.getOutputStream();

		// extract filename from path
		String context = request.getContextPath();
		StringBuffer sb = request.getRequestURL();
		int i = sb.indexOf(context);

		if (i == -1) {
			throw new ServletException("Cannot find " + context + " in " + sb);
		}

		String path = sb.substring(i + context.length());
		InputStream in = null;

		try {
			
			String jsoncallback = request.getParameter("callback");
			if (jsoncallback != null) {
				o.write(jsoncallback.getBytes("UTF-8"));
				o.write('(');
			}
			processRequest(path, in, o);
			
			if (jsoncallback != null) {
				o.write(')');
			}
			
		} catch (IOException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
		} catch (RuntimeException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					e.getMessage());
		} finally {
			if (in != null) {
				in.close();
			}
		}

		o.flush();
		o.close();
		response.flushBuffer();
	}
}
