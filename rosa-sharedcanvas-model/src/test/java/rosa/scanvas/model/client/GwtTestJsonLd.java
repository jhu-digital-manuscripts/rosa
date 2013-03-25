package rosa.scanvas.model.client;

import rosa.scanvas.model.client.impl.JsonLdNode;
import rosa.scanvas.model.client.impl.JsonLdValue;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestJsonLd extends GWTTestCase {

    public String getModuleName() {
        return "rosa.scanvas.model.SharedCanvasModel";
    }

    private JSONObject parse(String json) {
        JSONValue val = JSONParser.parseLenient(json);

        return val.isObject();
    }

    public void testSimple() {
        String json = "{\n"
                + "  \"http://schema.org/name\": \"Manu Sporny\",\n"
                + "  \"http://schema.org/url\": { \"@id\": \"http://manu.sporny.org/\" },\n"
                + "  \"http://schema.org/image\": { \"@id\": \"http://manu.sporny.org/images/manu.png\" }\n"
                + "}";

        JsonLdNode node = new JsonLdNode(parse(json));

        assertEquals(null, node.id());

        JsonLdValue val1 = node.propertyValue("http://schema.org/name");

        assertTrue(val1.isString());
        assertEquals("Manu Sporny", val1.stringValue());

        JsonLdValue val2 = node.propertyValue("http://schema.org/image");

        assertTrue(val2.isNode());
        assertEquals("http://manu.sporny.org/images/manu.png", val2.nodeValue()
                .id());

        assertTrue(node.hasProperty("http://schema.org/image"));
        assertTrue(node.hasProperty("http://schema.org/name"));
        assertTrue(node.hasProperty("http://schema.org/image"));
    }

    public void testSimpleType() {
        String json = "{\n"
                + "  \"@id\": \"http://example.org/places#BrewEats\",\n"
                + "  \"@type\": \"http://schema.org/Restaurant\",\n" + "}";

        JsonLdNode node = new JsonLdNode(parse(json));

        assertEquals("http://example.org/places#BrewEats", node.id());

        String[] types = node.types();

        assertNotNull(types);
        assertEquals(1, types.length);
        assertEquals("http://schema.org/Restaurant", types[0]);

        assertTrue(node.hasType("http://schema.org/Restaurant"));
    }

    public void testSimpleContext() {
        String json = "{\n" + "  \"@context\":\n" + "  {\n"
                + "    \"name\": \"http://schema.org/name\",\n"
                + "    \"image\": {\n"
                + "      \"@id\": \"http://schema.org/image\",\n"
                + "      \"@type\": \"@id\"\n" + "    },\n"
                + "    \"homepage\": {\n"
                + "      \"@id\": \"http://schema.org/url\",\n"
                + "      \"@type\": \"@id\"\n" + "    }\n" + "  },\n"
                + "  \"name\": \"Manu Sporny\",\n"
                + "  \"homepage\": \"http://manu.sporny.org/\",\n"
                + "  \"image\": \"http://manu.sporny.org/images/manu.png\"\n"
                + "}";

        JsonLdNode node = new JsonLdNode(parse(json));

        assertEquals(null, node.id());

        assertTrue(node.hasProperty("http://schema.org/image"));
        assertTrue(node.hasProperty("http://schema.org/name"));
        assertTrue(node.hasProperty("http://schema.org/image"));

        JsonLdValue val1 = node.propertyValue("http://schema.org/name");

        assertTrue(val1.isString());
        assertEquals("Manu Sporny", val1.stringValue());

        JsonLdValue val2 = node.propertyValue("http://schema.org/image");

        assertTrue(val2.isNode());
        assertEquals("http://manu.sporny.org/images/manu.png", val2.nodeValue()
                .id());
    }
}
