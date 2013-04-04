package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.SharedCanvasConstants;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationBodyImpl implements AnnotationBody,
        SharedCanvasConstants {
    private final RdfGraph graph;
    private final String uri;

    public AnnotationBodyImpl(RdfGraph graph, String uri) {
        this.graph = graph;
        this.uri = uri;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public boolean isChoice() {
        return isType(OA_CHOICE);
    }

    @Override
    public boolean isComposite() {
        return isType(OA_COMPOSITE);
    }

    @Override
    public boolean isText() {
        return isType(DCMI_TEXT);
    }

    @Override
    public boolean isImage() {
        return isType(DCMI_IMAGE);
    }

    @Override
    public String textContent() {
        return graph.findObjectStringValue(uri, CNT_CHARS);
    }

    @Override
    public AnnotationBody defaultItem() {
        String def_uri = graph.findObjectStringValue(uri, OA_DEFAULT);

        if (def_uri == null) {
            return null;
        }

        return new AnnotationBodyImpl(graph, def_uri);
    }

    @Override
    public List<AnnotationBody> otherItems() {
        List<AnnotationBody> result = new ArrayList<AnnotationBody>();

        for (String item_uri : graph.findObjectStringValues(uri, OA_ITEM)) {
            result.add(new AnnotationBodyImpl(graph, item_uri));
        }

        return result;
    }

    @Override
    public String format() {
        return graph.findObjectStringValue(uri, DC_FORMAT);
    }

    @Override
    public String conformsTo() {
        return graph.findObjectStringValue(uri, DCTERMS_CONFORMS_TO);
    }

    @Override
    public boolean isType(String type_uri) {
        return !graph.find(uri, RDF_TYPE, type_uri).isEmpty();
    }

}
