package rosa.scanvas.model.client.impl;

import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.SharedCanvasConstants;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationImpl implements Annotation, SharedCanvasConstants {
    private final RdfGraph graph;
    private final String uri;

    public AnnotationImpl(RdfGraph graph, String uri) {
        this.graph = graph;
        this.uri = uri;
    }

    @Override
    public String uri() {
        return uri;
    }
    
    @Override
    public String label() {
        return graph.findObjectStringValue(uri, RDFS_LABEL);
    }

    @Override
    public AnnotationBody body() {
        String body_uri = graph.findObjectStringValue(uri, OA_HAS_BODY);

        if (body_uri == null) {
            return null;
        }

        return new AnnotationBodyImpl(graph, body_uri);
    }

    @Override
    public List<String> targets() {
        return graph.findObjectStringValues(uri, OA_HAS_TARGET);
    }

    @Override
    public String motivatedBy() {
        return graph.findObjectStringValue(uri, OA_MOTIVATED_BY);
    }
}
