package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.ResourceMap;
import rosa.scanvas.model.client.SharedCanvasConstants;
import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfGraph;
import rosa.scanvas.model.client.rdf.RdfNode;
import rosa.scanvas.model.client.rdf.RdfTriple;

public class ResourceMapImpl implements ResourceMap, SharedCanvasConstants {
    protected final RdfGraph graph;

    private final String resmap_uri;
    private final String agg_uri;

    public ResourceMapImpl(RdfGraph graph) {
        this.graph = graph;
        this.resmap_uri = find_resmap_uri();
        this.agg_uri = find_agg_uri(resmap_uri);

        if (resmap_uri == null || agg_uri == null) {
            throw new RdfException("Cannot find resource map or aggregation.");
        }
    }

    private String find_resmap_uri() throws RdfException {
        List<RdfNode> nodes = graph.withRdfType(ORE_RESOURCE_MAP);

        if (nodes.size() == 0) {
            return null;
        }

        return nodes.get(0).value().stringValue();
    }

    private String find_agg_uri(String resmap_id) {
        return graph.findObjectStringValue(resmap_id, ORE_DESCRIBES);
    }

    public String uri() {
        return resmap_uri;
    }

    public String aggregation_uri() {
        return agg_uri;
    }

    // TODO too much trouble
    @Override
    public Date modified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String creatorName() {
        RdfNode node = graph.findObject(resmap_uri, DCTERMS_CREATOR);

        if (node == null) {
            return null;
        }

        if (!(node.isBlankNode() || node.isIRI())) {
            return null;
        }

        return graph.findObjectStringValue(node.value().stringValue(),
                FOAF_NAME);
    }

    @Override
    public List<String> aggregates() {
        List<String> result = new ArrayList<String>();

        for (RdfTriple triple : graph.find(agg_uri, ORE_AGGREGATES, null)) {
            result.add(triple.object().value().stringValue());
        }

        return result;
    }

    @Override
    public <T> List<Reference<T>> aggregatedReferences(String type_uri,
            Class<T> type) {
        List<Reference<T>> result = new ArrayList<Reference<T>>();

        for (RdfTriple triple : graph.find(agg_uri, ORE_AGGREGATES, null)) {
            String child_uri = triple.object().value().stringValue();

            if (graph.find(child_uri, RDF_TYPE, type_uri).size() > 0) {
                String child_label = graph.findObjectStringValue(child_uri,
                        RDFS_LABEL);

                result.add(new ReferenceImpl<T>(child_uri, type, child_label));
            }
        }

        return result;
    }
}
