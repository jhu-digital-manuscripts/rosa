package rosa.scanvas.model.client.rdf;

public interface RdfValue {
    boolean isArray();

    boolean isNode();

    boolean isString();

    boolean isNumber();

    String stringValue();

    RdfNode nodeValue();

    double numberValue();
}
