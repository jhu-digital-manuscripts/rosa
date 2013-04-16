package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.AnnotationSelector;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationTargetImpl extends BaseObject implements
        AnnotationTarget {

    public AnnotationTargetImpl(String uri, RdfGraph graph) {
        super(uri, graph);
    }

    @Override
    public boolean isSpecificResource() {
        return isType(OA_SPECIFIC_RESOURCE);
    }

    @Override
    public String hasSource() {
        return graph.findObjectStringValue(uri, OA_HAS_SOURCE);
    }

    @Override
    public AnnotationSelector hasSelector() {
        String selector_uri = graph.findObjectStringValue(uri, OA_HAS_SELECTOR);
        
        if (selector_uri == null) {
            return null;
        }
        
        return new AnnotationSelectorImpl(selector_uri, graph);
    }

}
