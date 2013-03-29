package rosa.scanvas.model.client.rdf;

public interface RdfNode {
    String[] types();

    boolean isType(String type);

    String[] dataTypes();

    boolean isDataType(String type);

    RdfValue value();
    
    boolean isBlankNode();

    boolean isIRI();
}
