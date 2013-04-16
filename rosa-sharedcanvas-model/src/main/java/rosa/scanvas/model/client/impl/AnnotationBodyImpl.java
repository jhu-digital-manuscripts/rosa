package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class AnnotationBodyImpl extends BaseObject implements AnnotationBody {
    public AnnotationBodyImpl(String uri, RdfGraph graph) {
        super(uri, graph);
    }

    @Override
    public boolean isChoice() {
        return isType(OA_CHOICE);
    }

    @Override
    public boolean isComposite() {
        return isType(OA_COMPOSITE);
    }

    @Override
    public AnnotationBody defaultItem() {
        String def_uri = graph.findObjectStringValue(uri, OA_DEFAULT);

        if (def_uri == null) {
            return null;
        }

        return new AnnotationBodyImpl(def_uri, graph);
    }

    @Override
    public List<AnnotationBody> otherItems() {
        List<AnnotationBody> result = new ArrayList<AnnotationBody>();

        for (String item_uri : graph.findObjectStringValues(uri, OA_ITEM)) {
            result.add(new AnnotationBodyImpl(item_uri, graph));
        }

        return result;
    }
}
