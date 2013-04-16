package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.rdf.RdfGraph;
import rosa.scanvas.model.client.rdf.RdfTriple;

public class CanvasImpl extends BaseObject implements Canvas {
    public CanvasImpl(String uri, RdfGraph graph) {
        super(uri, graph);
    }

    @Override
    public int width() {
        return (int) graph.findObjectNumberValue(uri, EXIF_WIDTH, -1);
    }

    @Override
    public int height() {
        return (int) graph.findObjectNumberValue(uri, EXIF_HEIGHT, -1);
    }

    @Override
    public List<Reference<AnnotationList>> hasAnnotations() {
        List<Reference<AnnotationList>> result = new ArrayList<Reference<AnnotationList>>();

        for (RdfTriple triple : graph.find(uri, OA_HAS_ANNOTATIONS, null)) {
            String al_uri = triple.object().value().stringValue();
            result.add(new ReferenceImpl<AnnotationList>(al_uri,
                    AnnotationList.class, null));
        }

        return result;
    }

}
