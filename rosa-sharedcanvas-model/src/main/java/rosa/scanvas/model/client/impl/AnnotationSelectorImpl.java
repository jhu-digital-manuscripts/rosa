package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.AnnotationSelector;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationSelectorImpl extends BaseObject implements
        AnnotationSelector {

    public AnnotationSelectorImpl(String uri, RdfGraph graph) {
        super(uri, graph);
    }

    @Override
    public boolean isFragmentSelector() {
        return isType(OA_FRAGMENT_SELECTOR);
    }

    @Override
    public String fragmentValue() {
        return graph.findObjectStringValue(uri, RDF_VALUE);
    }

    @Override
    public boolean isSvgSelector() {
        return isType(OA_SVG_SELECTOR);
    }
}
