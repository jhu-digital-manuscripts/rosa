package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class SequenceImpl extends ResourceMapImpl implements Sequence {
    public SequenceImpl(RdfGraph graph) {
        super(graph);
    }

    @Override
    public String label() {
        return graph.findObjectStringValue(aggregation_uri(), RDFS_LABEL);
    }

    @Override
    public String readingDirection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Canvas canvas(int index) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int length() {
        // TODO Auto-generated method stub
        return 0;
    }
}
