package rosa.scanvas.model.client.rdf;

public interface RdfValue {
    boolean isArray();

    boolean isNode();

    boolean isString();

    String stringValue();

    RdfNode nodeValue();
}
