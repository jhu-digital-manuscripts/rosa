package rosa.scanvas.model.client.rdf.impl;

import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfNode;
import rosa.scanvas.model.client.rdf.RdfValue;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class RdfValueJson implements RdfValue {
    private final JSONValue v;

    public RdfValueJson(JSONValue v) {
        this.v = v;
    }

    public boolean isArray() {
        return v.isArray() != null;
    }

    public boolean isNode() {
        return v.isString() == null && v.isArray() == null;
    }

    public boolean isString() {
        return v.isString() != null;
    }

    public RdfNode nodeValue() throws RdfException {
        JSONObject o = v.isObject();

        if (o == null) {
            return null;
        }

        return new RdfNodeJson(o);
    }

    public String stringValue() {
        JSONString str = v.isString();

        if (str == null) {
            return null;
        }

        return str.stringValue();
    }

    public String toString() {
        return v.toString();
    }
}
