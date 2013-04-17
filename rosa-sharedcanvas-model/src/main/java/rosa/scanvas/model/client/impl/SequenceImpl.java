package rosa.scanvas.model.client.impl;

import java.util.Iterator;
import java.util.List;

import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Range;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class SequenceImpl extends ResourceMapImpl implements Sequence {
    private final List<String> canvas_uris;

    public SequenceImpl(RdfGraph graph) {
        super(graph);

        // TODO: Hack to deal with json-ld serialization issues
        String list_head = graph.findObjectStringValue(aggregation_uri(),
                SHARED_CANVAS_HAS_ORDER);

        if (list_head == null) {
            this.canvas_uris = aggregates();
        } else {
            this.canvas_uris = graph.listToStringValues(list_head);
        }
    }

    @Override
    public String readingDirection() {
        return graph.findObjectStringValue(aggregation_uri(),
                SHARED_CANVAS_READING_DIR);
    }

    @Override
    public Canvas canvas(int index) {
        return new CanvasImpl(canvas_uris.get(index), graph);
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
