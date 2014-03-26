package iiif;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IIIFSerializer {

    public void toXML(ImageInfo info, OutputStream os, String baseUri)
            throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        String ns = "http://library.stanford.edu/iiif/image-api/ns/";
        Document doc = docBuilder.newDocument();
        Element root = doc.createElementNS(ns, "info");
        doc.appendChild(root);

        Element id = doc.createElementNS(ns, "identifier");
        id.setTextContent(info.getId());
        root.appendChild(id);
	// As it turns out, "@" is an invalid starting character for an xml tag.
	// Element atId = doc.createElementNS(ns, "@id");
	// atId.setTextContent(baseUri + "/" + URLEncoder.encode(info.getId()));
	// root.appendChild(atId);

	// Element atContext = doc.createElementNS(ns, "@context");
        // atContext.setTextContent("http://library.stanford.edu/iiif/image-api/1.1/context.json");
        // root.appendChild(atContext);

        Element width = doc.createElementNS(ns, "width");
        width.setTextContent(info.getWidth() + "");
        root.appendChild(width);

        Element height = doc.createElementNS(ns, "height");
        height.setTextContent(info.getHeight() + "");
        root.appendChild(height);

        if (info.getTileWidth() > 0 && info.getTileHeight() > 0) {
            Element tile_width = doc.createElementNS(ns, "tile_width");
            tile_width.setTextContent(info.getTileWidth() + "");
            root.appendChild(tile_width);

            Element tile_height = doc.createElementNS(ns, "tile_height");
            tile_height.setTextContent(info.getTileHeight() + "");
            root.appendChild(tile_height);
        }

        int[] scales = info.getScaleFactors();

        if (scales != null && scales.length > 0) {
            Element scale_factors = doc.createElementNS(ns, "scale_factors");
            root.appendChild(scale_factors);

            for (int scale : scales) {
                Element scale_factor = doc.createElementNS(ns, "scale_factor");
                scale_factor.setTextContent("" + scale);
                scale_factors.appendChild(scale_factor);
            }

        }

        ImageFormat[] fmts = info.getFormats();

        if (fmts != null && fmts.length > 0) {
            Element formats = doc.createElementNS(ns, "formats");
            root.appendChild(formats);

            for (ImageFormat fmt : fmts) {
                Element format = doc.createElementNS(ns, "format");
                format.setTextContent(fmt.name().toLowerCase());
                formats.appendChild(format);
            }
        }

        Quality[] quals = info.getQualities();

        if (quals != null && quals.length > 0) {
            Element qualities = doc.createElementNS(ns, "qualities");
            root.appendChild(qualities);

            for (Quality qual : quals) {
                Element quality = doc.createElementNS(ns, "quality");
                quality.setTextContent(qual.name().toLowerCase());
                qualities.appendChild(quality);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        StreamResult result = new StreamResult(os);

        transformer.transform(source, result);
    }

    public void toJSON(ImageInfo info, OutputStream os, String baseUri) throws JSONException, IOException {
        JSONObject root = new JSONObject();
        
	root.put("@context", "http://library.stanford.edu/iiif/image-api/1.1/context.json");
        root.put("@id", baseUri + "/" + URLEncoder.encode(info.getId()));
	root.put("identifier", URLEncoder.encode(info.getId()));
        root.put("width", info.getWidth());
        root.put("height", info.getHeight());
	root.put("profile", "http://library.stanford.edu/iiif/image-api/compliance.html#level1");
        
        if (info.getTileWidth() > 0 && info.getTileHeight() > 0) {
            root.put("tile_width", info.getTileWidth());
            root.put("tile_height", info.getTileHeight());
        }
        
        int[] scales = info.getScaleFactors();

        if (scales != null && scales.length > 0) {
            JSONArray scale_factors = new JSONArray();
            root.put("scale_factors", scale_factors);

            for (int scale : scales) {
                scale_factors.put(scale);
            }

        }

        ImageFormat[] fmts = info.getFormats();

        if (fmts != null && fmts.length > 0) {
            JSONArray formats = new JSONArray();
            root.put("formats", formats);

            for (ImageFormat fmt: fmts) {
                formats.put(fmt.name().toLowerCase());
            }
        }

        Quality[] quals = info.getQualities();

        if (quals != null && quals.length > 0) {
            JSONArray qualities = new JSONArray();
            root.put("qualities", qualities);

            for (Quality qual: quals) {
                qualities.put(qual.name().toLowerCase());
            }
        }
        
        OutputStreamWriter wos = new OutputStreamWriter(os);
        root.write(wos);
        wos.flush();
    }
}
