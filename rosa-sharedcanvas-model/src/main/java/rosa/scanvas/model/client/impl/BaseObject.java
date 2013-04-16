package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.SharedCanvasConstants;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class BaseObject implements SharedCanvasConstants {
    protected final RdfGraph graph;
    protected final String uri;

    public BaseObject(String uri, RdfGraph graph) {
        this.uri = uri;
        this.graph = graph;
    }

    public String uri() {
        return uri;
    }
    
    public String label() {
        return graph.findObjectStringValue(uri, RDFS_LABEL);
    }
    
    public boolean isType(String type_uri) {
        return !graph.find(uri, RDF_TYPE, type_uri).isEmpty();
    }
    
    public boolean isText() {
        return isType(DCMI_TEXT);
    }

    public boolean isImage() {
        return isType(DCMI_IMAGE);
    }
    
    public boolean hasTextContent() {
        return isType(CNT_CONTENT_AS_TEXT);
    }
    
    public String textContent() {
        return graph.findObjectStringValue(uri, CNT_CHARS);
    }
    
    public String format() {
        return graph.findObjectStringValue(uri, DC_FORMAT);
    }

    public String conformsTo() {
        return graph.findObjectStringValue(uri, DCTERMS_CONFORMS_TO);
    }
}
