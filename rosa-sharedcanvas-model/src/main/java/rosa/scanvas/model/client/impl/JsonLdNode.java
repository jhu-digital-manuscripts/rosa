package rosa.scanvas.model.client.impl;

import java.util.Map;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JsonLdNode {
    private final JSONObject o;
    private Map<String, String> term_defs; // term -> IRI

    public JsonLdNode(JSONObject o) {
        this.o = o;
        this.term_defs = null;
    }

    private void update_term_defs() {
        if (term_defs != null) {
            return;
        }
        
        
        
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

    private JsonLdValue get_value(String key) {
        JSONValue val = o.get(key);

        if (val == null) {
            return null;
        }

        return new JsonLdValue(val);
    }

    private String get_string(String key) {
        return as_string(o.get(key));
    }

    private String[] get_string_array(String key) {
        return as_string_array(o.get(key));
    }

    public String id() {
        return get_string("@id");
    }

    public String language() {
        return get_string("@language");
    }

    public String container() {
        return get_string("@language");
    }

    public String[] types() {
        return get_string_array("@type");
    }

    public boolean hasType(String type) {
        for (String t : types()) {
            if (t.equals(type)) {
                return true;
            }
        }

        return false;
    }

    // public JsonLdNode context() {
    // return get_node("@context");
    // }

    // public boolean isJsonLdValueObject() {
    // return o.containsKey("@value");
    // }

    public boolean hasProperty(String iri) {
        return o.containsKey(iri);
    }

    public JsonLdValue propertyValue(String iri) {
        if (o.isObject() != null) {
            return get_value(iri);
        }

        return null;
    }
}
