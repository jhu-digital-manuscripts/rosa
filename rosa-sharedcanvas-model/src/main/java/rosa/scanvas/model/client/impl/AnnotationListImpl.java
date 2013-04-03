package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationListImpl extends ResourceMapImpl implements
        AnnotationList {

    public AnnotationListImpl(RdfGraph graph) {
        super(graph);
    }

    @Override
    public String label() {
        return graph.findObjectStringValue(aggregation_uri(), RDFS_LABEL);
    }

    @Override
    public Annotation annotation(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int length() {
        // TODO Auto-generated method stub
        return 0;
    }

}
