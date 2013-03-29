package rosa.scanvas.model.client.rdf.impl;

import rosa.scanvas.model.client.rdf.AbstractRdfTriple;
import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfNode;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class RdfTripleJson extends AbstractRdfTriple {
    private final JSONObject triple;

    public RdfTripleJson(JSONObject triple) throws RdfException {
        this.triple = triple;

        if (triple == null || triple.isObject() == null
                || !triple.containsKey("subject")
                || !triple.containsKey("predicate")
                || !triple.containsKey("object")) {
            throw new RdfException("Invalid triple: " + triple);
        }
    }

    @Override
    public int hashCode() {
        return triple.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RdfTripleJson)) {
            return false;
        }

        return ((RdfTripleJson) o).triple.equals(triple);
    }

    public String toString() {
        return triple.toString();
    }

    private RdfNode get(String name) throws RdfException {
        JSONValue val = triple.get(name);

        if (val == null) {
            return null;
        }

        return new RdfNodeJson(val.isObject());
    }

    @Override
    public RdfNode subject() throws RdfException {
        return get("subject");
    }

    @Override
    public RdfNode predicate() throws RdfException {
        return get("predicate");
    }

    @Override
    public RdfNode object() throws RdfException {
        return get("object");
    }

}
