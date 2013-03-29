package rosa.scanvas.model.client.jsonld;

import java.util.List;

import rosa.scanvas.model.client.ResourceMap;
import rosa.scanvas.model.client.impl.ResourceMapImpl;
import rosa.scanvas.model.client.impl.SharedCanvasConstants;
import rosa.scanvas.model.client.jsonld.JsonLd.Callback;
import rosa.scanvas.model.client.rdf.RdfDataset;
import rosa.scanvas.model.client.rdf.RdfGraph;
import rosa.scanvas.model.client.rdf.RdfNode;
import rosa.scanvas.model.client.rdf.RdfTriple;
import rosa.scanvas.model.client.rdf.impl.RdfDatasetJson;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestJsonLd extends GWTTestCase {

    public String getModuleName() {
        return "rosa.scanvas.model.SharedCanvasModel";
    }

    private JavaScriptObject parse(String json) {
        JSONValue val = JSONParser.parseLenient(json);
        return val.isObject().getJavaScriptObject();
    }

    public void testHasProcessor() {
        assertTrue(JsonLd.hasProcessor());
    }

    /**
     * Check simple JSON-LD with three triples each with the same blank node
     * subject.
     */
    private void check_simple_rdf(JsArray<JavaScriptObject> result) {
        // System.err.println(new JSONObject(result).toString());

        assertEquals(2, result.length());

        JavaScriptObject error = result.get(0);
        JavaScriptObject rdf = result.get(1);

        assertNull(error);
        assertNotNull(rdf);

        RdfDataset dataset = new RdfDatasetJson(new JSONObject(rdf));

        RdfGraph graph = dataset.defaultGraph();

        assertNotNull(graph);
        assertEquals(3, graph.size());

        String subject = null;

        for (RdfTriple triple : graph) {
            assertNotNull(triple.subject());
            assertNotNull(triple.predicate());
            assertNotNull(triple.object());

            assertTrue(triple.subject().isBlankNode());

            assertFalse(triple.predicate().isType("Moo"));
            assertTrue(triple.predicate().isType("IRI"));
            assertFalse(triple.predicate().isDataType("Blah"));

            String[] subject_types = triple.subject().types();
            assertNotNull(subject_types);
            assertEquals(1, subject_types.length);
            assertEquals("blank node", subject_types[0]);

            assertFalse(triple.object().isType("http://example.com/blah"));

            if (subject == null) {
                subject = triple.subjectId();
                assertNotNull(subject);
            } else {
                assertEquals(subject, triple.subjectId());
            }

            assertTrue(triple.subject().value().isString());
            assertFalse(triple.subject().value().isArray());
            assertFalse(triple.subject().value().isNode());
            
            assertNotNull(triple.object().value());
            assertNotNull(triple.object().value().stringValue());
        }

        assertEquals(3, graph.find(null, null, null).size());
        assertEquals(3, graph.find(subject, null, null).size());

        {
            List<RdfTriple> triples = graph.find(null,
                    "http://schema.org/image", null);

            assertEquals(1, triples.size());

            RdfTriple triple = triples.get(0);

            assertTrue(triple.object().value().isString());
            assertEquals("http://manu.sporny.org/images/manu.png", triple
                    .object().value().stringValue());
            assertTrue(triple.object().isType("IRI"));
        }

        {
            List<RdfTriple> triples = graph.find(null, null, "Manu Sporny");

            assertEquals(1, triples.size());

            RdfTriple triple = triples.get(0);

            assertEquals("http://schema.org/name", triple.predicateId());

            assertTrue(triple.object().isType("literal"));
            assertTrue(triple.object().isDataType(
                    "http://www.w3.org/2001/XMLSchema#string"));
        }

        {
            RdfNode object = graph.findObject(null, "http://schema.org/url");

            assertTrue(object.value().isString());
            assertEquals("http://manu.sporny.org/", object.value()
                    .stringValue());
        }
    }

    public void testSimple() {
        String json = "{\n"
                + "  \"http://schema.org/name\": \"Manu Sporny\",\n"
                + "  \"http://schema.org/url\": { \"@id\": \"http://manu.sporny.org/\" },\n"
                + "  \"http://schema.org/image\": { \"@id\": \"http://manu.sporny.org/images/manu.png\" }\n"
                + "}";

        JsonLd.toRdf(parse(json), new Callback() {
            @Override
            public void finished(JsArray<JavaScriptObject> result) {
                check_simple_rdf(result);
            }
        });
    }

    public void testSimpleWithContext() {
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

        JsonLd.toRdf(parse(json), new Callback() {
            @Override
            public void finished(JsArray<JavaScriptObject> result) {
                check_simple_rdf(result);
            }
        });
    }

    private void check_simple_resouce_map(JsArray<JavaScriptObject> result) {
        System.err.println(new JSONObject(result).toString());

        assertEquals(2, result.length());

        JavaScriptObject error = result.get(0);
        JavaScriptObject rdf = result.get(1);

        assertNull(error);
        assertNotNull(rdf);

        RdfDataset dataset = new RdfDatasetJson(new JSONObject(rdf));

        // Some problems with find to test
        
        {
            List<RdfTriple> triples = dataset.defaultGraph().find(
                    "http://example.com/Cow/bessie",
                    "http://example.com/ChewingCud", null);
            assertEquals(0, triples.size());
        }

        {
            List<RdfTriple> triples = dataset.defaultGraph().find(
                    "http://rosetest.library.jhu.edu/sc/Douce195",
                    SharedCanvasConstants.DCTERMS_CREATOR, null);
            assertEquals(1, triples.size());
        }
        
        ResourceMap resmap = new ResourceMapImpl(dataset);

        assertEquals("http://rosetest.library.jhu.edu/sc/Douce195",
                resmap.url());
        assertEquals("http://rosetest.library.jhu.edu/sc/Douce195#aggregation",
                resmap.aggregation());

        assertEquals("Roman de la Rose Digital Library", resmap.creatorName());

        List<String> aggregates = resmap.aggregates();

        assertEquals(2, aggregates.size());

        assertTrue(aggregates
                .contains("http://rosetest.library.jhu.edu/sc/Douce195/sequence"));
        assertTrue(aggregates
                .contains("http://rosetest.library.jhu.edu/sc/Douce195/annotations"));
    }

    public void testSimpleResourceMap() {
        String json = "{\"@graph\": [{\n"
                + "  \"@type\" : [ \"http://purl.org/dc/terms/AgentClass\" ],\n"
                + "  \"http://xmlns.com/foaf/0.1/name\" : [ {\n"
                + "    \"@value\" : \"Roman de la Rose Digital Library\"\n"
                + "  } ],\n"
                + "  \"@id\" : \"_:t0\"\n"
                + "}, {\n"
                + "  \"http://purl.org/dc/terms/creator\" : [ {\n"
                + "    \"@id\" : \"_:t0\"\n"
                + "  } ],\n"
                + "  \"@type\" : [ \"http://www.openarchives.org/ore/terms/ResourceMap\" ],\n"
                + "  \"http://purl.org/dc/terms/modified\" : [ {\n"
                + "    \"@type\" : \"http://www.w3.org/2001/XMLSchema#dateTime\",\n"
                + "    \"@value\" : \"2013-03-22T15:12:54.467Z\"\n"
                + "  } ],\n"
                + "  \"http://www.openarchives.org/ore/terms/describes\" : [ {\n"
                + "    \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195#aggregation\"\n"
                + "  } ],\n"
                + "  \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195\"\n"
                + "}, {\n"
                + "  \"http://purl.org/dc/elements/1.1/rights\" : [ {\n"
                + "    \"@value\" : \"&#169; Bodleian Library, University of Oxford\"\n"
                + "  } ],\n"
                + "  \"http://www.openarchives.org/ore/terms/isDescribedBy\" : [ {\n"
                + "    \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195\"\n"
                + "  } ],\n"
                + "  \"http://purl.org/dc/elements/1.1/title\" : [ {\n"
                + "    \"@value\" : \"Douce 195\"\n"
                + "  } ],\n"
                + "  \"@type\" : [ \"http://www.shared-canvas.org/ns/Manifest\", \"http://www.openarchives.org/ore/terms/Aggregation\" ],\n"
                + "  \"http://purl.org/dc/elements/1.1/source\" : [ {\n"
                + "    \"@value\" : \"Bodleian Library\"\n"
                + "  } ],\n"
                + "  \"http://www.openarchives.org/ore/terms/aggregates\" : [ {\n"
                + "    \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195/sequence\"\n"
                + "  }, {\n"
                + "    \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195/annotations\"\n"
                + "  } ],\n"
                + "  \"http://www.w3.org/2000/01/rdf-schema#label\" : [ {\n"
                + "    \"@value\" : \"Bodleian Library, Douce 195\"\n"
                + "  } ],\n"
                + "  \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195#aggregation\",\n"
                + "  \"http://purl.org/dc/elements/1.1/date\" : [ {\n"
                + "    \"@value\" : \"15th century\"\n"
                + "  } ]\n"
                + "}, {\n"
                + "  \"@type\" : [ \"http://www.w3.org/ns/oa#Annotation\" ],\n"
                + "  \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195/annotations\"\n"
                + "}, {\n"
                + "  \"@type\" : [ \"http://www.shared-canvas.org/ns/Sequence\" ],\n"
                + "  \"@id\" : \"http://rosetest.library.jhu.edu/sc/Douce195/sequence\"\n"
                + "}]}";

        JsonLd.toRdf(parse(json), new Callback() {
            @Override
            public void finished(JsArray<JavaScriptObject> result) {
                check_simple_resouce_map(result);
            }
        });
    }

}
