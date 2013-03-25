package rosa.scanvas.model.client.impl;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JsonLdValue {
    private final JSONValue v;

    public JsonLdValue(JSONValue v) {
        this.v = v;
    }

    public boolean isArray() {
        return v.isArray() != null;
    }

    public boolean isNode() {
        return v.isObject() != null;
    }

    public boolean isString() {
        return v.isString() != null;
    }
    
    public JsonLdNode nodeValue() {
        JSONObject o = v.isObject();

        if (o == null) {
            return null;
        }

        return new JsonLdNode(o);
    }

    public String stringValue() {
        JSONString str = v.isString();

        if (str == null) {
            return null;
        }

        return str.stringValue();
    }
}
