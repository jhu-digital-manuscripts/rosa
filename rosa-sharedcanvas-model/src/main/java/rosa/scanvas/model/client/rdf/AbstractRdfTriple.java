package rosa.scanvas.model.client.rdf;

public abstract class AbstractRdfTriple implements RdfTriple {
    public String subjectId() {
        return subject().value().stringValue();
    }

    public String predicateId() {
        return predicate().value().stringValue();
    }
}
