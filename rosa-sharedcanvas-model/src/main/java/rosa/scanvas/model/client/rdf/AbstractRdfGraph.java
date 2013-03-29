package rosa.scanvas.model.client.rdf;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRdfGraph implements RdfGraph {
    private static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    public List<RdfTriple> find(String subject, String predicate, String object) {
        List<RdfTriple> result = new ArrayList<RdfTriple>();

        for (RdfTriple triple : this) {
            if (subject != null && !subject.equals(triple.subjectId())) {
                continue;
            }

            if (predicate != null && !predicate.equals(triple.predicateId())) {
                continue;
            }

            if (object != null
                    && !object.equals(triple.object().value().stringValue())) {
                continue;
            }

            result.add(triple);
        }

        return result;
    }

    public RdfNode findObject(String subject, String predicate) {
        List<RdfTriple> result = find(subject, predicate, null);

        if (result.size() == 0) {
            return null;
        }

        return result.get(0).object();
    }

    public List<RdfNode> withRdfType(String type) {
        List<RdfNode> result = new ArrayList<RdfNode>();

        for (RdfTriple triple : this) {
            if (triple.predicate().isIRI()
                    && triple.predicate().value().stringValue()
                            .equals(RDF_TYPE_URI)) {
                if (triple.object().value().stringValue().equals(type)) {
                    result.add(triple.subject());
                }
            }
        }

        return result;
    }
}
