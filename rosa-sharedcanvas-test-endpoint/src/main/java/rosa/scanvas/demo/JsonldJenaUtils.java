package rosa.scanvas.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import org.apache.commons.io.input.BOMInputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.dfki.km.json.JSONUtils;
import de.dfki.km.json.jsonld.JSONLD;
import de.dfki.km.json.jsonld.JSONLDProcessingError;
import de.dfki.km.json.jsonld.impl.JenaJSONLDSerializer;

public class JsonldJenaUtils {
	
	/**
	 * Returns the canvas RDF Resource. 
	 * If no Resources exist, a NoSuchElementException is thrown.
	 * If more than one Resources are found, only the first is returned
	 * @param canvas String holding the canvas name. EX: 13v
	 * @param lang RDF language. EX: N3
	 * @return the canvas Resource
	 * @throws NoSuchElementException This is thrown if no references to the canvas are found. Note this is an unchecked exception
	 * @throws IOException
	 */
	private static Resource getCanvasUrn(String canvas, String lang) 
			throws NoSuchElementException, IOException{
		
		Model sequenceModel = ModelFactory.createDefaultModel();
		
		InputStream in = (JsonldJenaUtils.class).getClassLoader()
				.getResourceAsStream("NormalSequence.n3");
		sequenceModel.read(in, null, lang);
		
		StmtIterator statementIt = sequenceModel.listStatements(
				null, RDFS.label, /*"f"+*/canvas);
		
		in.close();
		return statementIt.next().getSubject();
	}
	
	
	private static Model getTargetedAnnotationLists(Resource canvRes, String lang) 
			throws IOException {
		Model model = ModelFactory.createDefaultModel();
		
		InputStream manIn = (JsonldJenaUtils.class).getClassLoader()
				.getResourceAsStream("Manifest.n3");
		model.read(manIn, null, lang);
		manIn.close();
		
		Property forCanvas = model.getProperty(
				"http://www.shared-canvas.org/ns/", "forCanvas");
		ResIterator rit = model.listResourcesWithProperty(
				forCanvas, canvRes);
		
		while (rit.hasNext()){
		
			Property describedBy = model.getProperty(
					"http://www.openarchives.org/ore/terms/", "isDescribedBy");
			NodeIterator nit = model.listObjectsOfProperty(rit.next(), describedBy);
			
			model.removeAll();
			Model subModel = ModelFactory.createDefaultModel();
			// 
			while (nit.hasNext()) {
				String targetedRes = nit.next().toString();
				String[] file = targetedRes.split("/");
				
				InputStream in = (JsonldJenaUtils.class).getClassLoader()
						.getResourceAsStream("f"+
								file[file.length-2]+"-"+file[file.length-1]+".n3");
				subModel.read(in, null, lang);
				
				model.add(subModel);
				subModel.removeAll();
				
				in.close();
			}
		}
		
		return model;
	}
	
	public static Model singleCanvasAggregateModel(String canvas, String lang) 
			throws IOException {
		Model model = ModelFactory.createDefaultModel();
		
		try {
			
			// read Sequence to get canvas urn
			Resource canvRes = getCanvasUrn(canvas, lang);
			
			// search Manifest for targeted annotation lists
			model = getTargetedAnnotationLists(canvRes, lang);
		} catch (NoSuchElementException e) {
			throw new IOException("Specified canvas does not exist: "+canvas,e);
		}
		
		// get non-targeted lists for references to this canvas
		InputStream in = (JsonldJenaUtils.class).getClassLoader()
				.getResourceAsStream("ImageAnnotations.n3");
		model.add(model.read(in, null, lang));
		in.close();
		
		in = (JsonldJenaUtils.class).getClassLoader()
				.getResourceAsStream("IllustrationDescription.n3");
		model.add(model.read(in, null, lang));
		in.close();
		
		// TODO search non-targeted lists?
		
		return model;
	}
	
	/**
	 * Generate a single Jena model from several different files, output it to 
	 * specified OutputStream
	 * @param aggr String[] String array containing all relevant RDF files "name.extension"
	 * @param out OutputStream
	 * @param type an instance of ScDemoFile class
	 * @throws IOException
	 */
	public static Model generateAggregateModel(String[] aggr, String lang) 
			throws IOException {
		
		Model model = ModelFactory.createDefaultModel();
		Model subModel = ModelFactory.createDefaultModel();
		
		for (int i=0; i<aggr.length; i++) {
			InputStream in = (JsonldJenaUtils.class).getClassLoader().getResourceAsStream(
					aggr[i]);
			BOMInputStream bIn = new BOMInputStream(in, false);
			
			subModel.read(bIn, null, lang);
			model = model.add(subModel);
			
			subModel.removeAll();
			bIn.close();
			in.close();
		}
		
		return model;
		
	}
	
	/**
	 * Use the JSON-LD processor from dfki.km.json to write output in 
	 * JSON-LD from a give InputStream
	 * @param in InputStream to file
	 * @param out OutputStream to Http response
	 * @param lang String representing the RDF language of the input data (in all caps). Ex: "N3" or "TURTLE"
	 * @throws IOException
	 */
	public static void writeJsonldFromStream(InputStream in, OutputStream out, String lang) 
			throws IOException {
		BOMInputStream bIn = null;
		
		try {
			bIn = new BOMInputStream(in,false);
			
			Model model = ModelFactory.createDefaultModel();
			model.read(bIn, null, lang);
			
			writeJsonldFromModel(model, out);
			
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		} finally {
			if (bIn != null) {
				bIn.close();
			}
		}
	}
	
	/**
	 * Write JSON-LD data from a Jena model to an OutputStream
	 * @param model Jena Model
	 * @param out OutputStream
	 * @throws JSONLDProcessingError
	 * @throws IOException
	 */
	public static void writeJsonldFromModel(Model model, OutputStream out) 
			throws IOException {
		JenaJSONLDSerializer serializer = new JenaJSONLDSerializer();
		
		try {
			Object json = JSONLD.fromRDF(model, serializer);
			String output = JSONUtils.toPrettyString(json);
			
			out.write(output.getBytes("UTF-8"));
		} catch (JSONLDProcessingError e) {
			throw new IOException("JSON-LD error: "+e.getMessage(),e);
		}
	}
	
}
