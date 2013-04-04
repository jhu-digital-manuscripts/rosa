package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Range;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.rdf.RdfGraph;
import rosa.scanvas.model.client.rdf.RdfNode;

public class SequenceImpl extends ResourceMapImpl implements Sequence {
    private final List<String> canvas_uris;

    public SequenceImpl(RdfGraph graph) {
        super(graph);

        // TODO WRONG ORDER!!

        this.canvas_uris = new ArrayList<String>();

        for (RdfNode node : graph.findRdfTypes(SHARED_CANVAS_CANVAS)) {
            canvas_uris.add(node.value().stringValue());
        }
    }

    @Override
    public String label() {
        return graph.findObjectStringValue(aggregation_uri(), RDFS_LABEL);
    }

    @Override
    public String readingDirection() {
        return graph.findObjectStringValue(aggregation_uri(),
                SHARED_CANVAS_READING_DIR);
    }

    @Override
    public Canvas canvas(int index) {
        return new CanvasImpl(graph, canvas_uris.get(index));
    }

    @Override
    public int size() {
        return canvas_uris.size();
    }

    @Override
    public Range hasContentRange() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Canvas> iterator() {
        return new Iterator<Canvas>() {
            int next = 0;

            @Override
            public boolean hasNext() {
                return next < size();
            }

            @Override
            public Canvas next() {
                return canvas(next++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
