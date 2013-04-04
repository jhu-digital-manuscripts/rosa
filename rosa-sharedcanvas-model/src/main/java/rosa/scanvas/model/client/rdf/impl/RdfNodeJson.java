package rosa.scanvas.model.client.rdf.impl;

import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfNode;
import rosa.scanvas.model.client.rdf.RdfValue;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

// Must use GWT 2.5.1 or later.
// In GWT 2.5.0 if JSONOBject.get(key) is called and the key doesn't exist, the code will die with a class cast exception

public class RdfNodeJson implements RdfNode {
    private final JSONObject node;

    public RdfNodeJson(JSONObject node) throws RdfException {
        this.node = node;

        if (node == null || node.isObject() == null
                || !node.containsKey("value")) {
            throw new RdfException("Invalid node: " + node);
        }
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RdfNodeJson)) {
            return false;
        }

        return ((RdfNodeJson) o).node.equals(node);
    }

    private String as_string(JSONValue val) {
        if (val == null) {
            return null;
        }

        JSONString str = val.isString();

        if (str == null) {
            return null;
        }

        return str.stringValue();
    }

    private String[] as_string_array(JSONValue val) {
        if (val == null) {
            return null;
        }

        // Handle single string value

        if (val.isString() != null) {
            return new String[] { val.isString().stringValue() };
        }

        // Handle array of strings

        JSONArray arr = val.isArray();

        if (arr == null) {
            return null;
        }

        String[] result = new String[arr.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = as_string(arr.get(i));
        }

        return result;
    }

    private RdfValueJson get_value(String key) {
        JSONValue val = node.get(key);

        if (val == null) {
            return null;
        }

        return new RdfValueJson(node.get(key));
    }

    private String[] get_string_array(String key) {
        return as_string_array(node.get(key));
    }

    public String[] types() {
        return get_string_array("type");
    }

    // not recursive
    private boolean contains(String name, String value) {
        JSONValue val = node.get(name);

        if (val == null) {
            return false;
        }

        if (val.isString() != null) {
            return val.isString().stringValue().equals(value);
        }

        JSONArray array = val.isArray();

        if (array == null) {
            return false;
        }

        for (int i = 0; i < array.size(); i++) {
            JSONValue s = array.get(i);

            if (s.isString() != null
                    && s.isString().stringValue().equals(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isType(String type) {
        return contains("type", type);
    }

    @Override
    public String[] dataTypes() {
        return get_string_array("datatype");
    }

    @Override
    public boolean isDataType(String type) {
        return contains("datatype", type);
    }

    @Override
    public RdfValue value() {
        return get_value("value");
    }

    @Override
    public boolean isBlankNode() {
        return isType("blank node");
    }

    @Override
    public boolean isIRI() {
        return isType("IRI");
    }
    
    @Override
    public String toString() {
        return node.toString();
    }
}
