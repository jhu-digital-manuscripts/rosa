package rosa.scanvas.model.client.impl;

import java.util.Iterator;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationListImpl extends ResourceMapImpl implements
        AnnotationList {
    private final List<String> annotation_uris;

    public AnnotationListImpl(RdfGraph graph) {
        super(graph);

        // TODO WRONG! ORDER!
        this.annotation_uris = aggregates();
    }

    @Override
    public String label() {
        return graph.findObjectStringValue(aggregation_uri(), RDFS_LABEL);
    }

    @Override
    public Annotation annotation(int index) {
        String uri = annotation_uris.get(index);
        return new AnnotationImpl(graph, uri);
    }

    @Override
    public int size() {
        return annotation_uris.size();
    }

    @Override
    public String forCanvas() {
        return graph.findObjectStringValue(aggregation_uri(),
                SHARED_CANVAS_FOR_CANVAS);
    }

    @Override
    public String forMotivation() {
        return graph.findObjectStringValue(aggregation_uri(),
                SHARED_CANVAS_FOR_MOTIVATION);
    }

    @Override
    public Iterator<Annotation> iterator() {
        return new Iterator<Annotation>() {
            int next = 0;

            public boolean hasNext() {
                return next < size();
            }

            public Annotation next() {
                return annotation(next++);
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
