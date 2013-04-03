package rosa.scanvas.model.client.rdf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rosa.scanvas.model.client.SharedCanvasConstants;

public abstract class AbstractRdfGraph implements RdfGraph,
        SharedCanvasConstants {
    private Map<String, List<RdfTriple>> subject_index = null;

    /**
     * Call to index triples by subject
     */
    protected void indexTriples() {
        subject_index = new HashMap<String, List<RdfTriple>>();

        for (RdfTriple triple : this) {
            String subject = triple.subjectId();

            List<RdfTriple> triples = subject_index.get(subject);

            if (triples == null) {
                triples = new ArrayList<RdfTriple>();
                subject_index.put(subject, triples);
            }

            triples.add(triple);
        }
    }

    public List<RdfTriple> find(String subject, String predicate, String object) {
        List<RdfTriple> result = new ArrayList<RdfTriple>();

        if (subject_index == null || subject == null) {
            for (RdfTriple triple : this) {
                if (subject != null && !subject.equals(triple.subjectId())) {
                    continue;
                }

                if (predicate != null
                        && !predicate.equals(triple.predicateId())) {
                    continue;
                }

                if (object != null
                        && !object
                                .equals(triple.object().value().stringValue())) {
                    continue;
                }

                result.add(triple);
            }
        } else {
            List<RdfTriple> triples = subject_index.get(subject);

            if (triples != null) {
                for (RdfTriple triple : triples) {
                    if (predicate != null
                            && !predicate.equals(triple.predicateId())) {
                        continue;
                    }

                    if (object != null
                            && !object.equals(triple.object().value()
                                    .stringValue())) {
                        continue;
                    }

                    result.add(triple);
                }
            }
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

    public String findObjectStringValue(String subject, String predicate) {
        RdfNode node = findObject(subject, predicate);

        if (node == null) {
            return null;
        }

        return node.value().stringValue();
    }

    public List<RdfNode> withRdfType(String type) {
        List<RdfNode> result = new ArrayList<RdfNode>();

        for (RdfTriple triple : this) {
            if (triple.predicate().isIRI()
                    && triple.predicate().value().stringValue()
                            .equals(RDF_TYPE)) {
                if (triple.object().value().stringValue().equals(type)) {
                    result.add(triple.subject());
                }
            }
        }

        return result;
    }
}
