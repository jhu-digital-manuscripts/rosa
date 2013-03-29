package rosa.scanvas.model.client.rdf;

public interface RdfTriple {
    RdfNode subject();

    RdfNode predicate();

    RdfNode object();

    String subjectId();

    String predicateId();
}
