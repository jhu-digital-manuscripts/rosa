package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationImpl extends BaseObject implements Annotation {
    public AnnotationImpl(String uri, RdfGraph graph) {
        super(uri, graph);
    }

    @Override
    public AnnotationBody body() {
        String body_uri = graph.findObjectStringValue(uri, OA_HAS_BODY);

        if (body_uri == null) {
            return null;
        }

        return new AnnotationBodyImpl(body_uri, graph);
    }

    @Override
    public List<AnnotationTarget> targets() {
        List<AnnotationTarget> result = new ArrayList<AnnotationTarget>();
        
        for (String target_uri: graph.findObjectStringValues(uri, OA_HAS_TARGET)) {
            result.add(new AnnotationTargetImpl(target_uri, graph));
        }
        
        return result;
    }

    @Override
    public String motivatedBy() {
        return graph.findObjectStringValue(uri, OA_MOTIVATED_BY);
    }
}
