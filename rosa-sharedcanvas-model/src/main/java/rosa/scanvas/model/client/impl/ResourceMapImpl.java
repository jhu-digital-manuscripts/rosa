package rosa.scanvas.model.client.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rosa.scanvas.model.client.ResourceMap;
import rosa.scanvas.model.client.rdf.RdfDataset;
import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfNode;
import rosa.scanvas.model.client.rdf.RdfTriple;

public class ResourceMapImpl implements ResourceMap, SharedCanvasConstants {
    private final RdfDataset ds;
    private final String resmap_uri;
    private final String agg_uri;

    public ResourceMapImpl(RdfDataset ds) {
        this.ds = ds;
        this.resmap_uri = find_resmap_uri();
        this.agg_uri = find_agg_uri(resmap_uri);

        if (resmap_uri == null || agg_uri == null) {
            throw new RdfException("Cannot find resource map or aggregation.");
        }
    }

    private String find_resmap_uri() throws RdfException {
        List<RdfNode> nodes = ds.defaultGraph().withRdfType(ORE_RESOURCE_MAP);

        if (nodes.size() == 0) {
            return null;
        }

        return nodes.get(0).value().stringValue();
    }

    private String find_agg_uri(String resmap_id) {
        RdfNode node = ds.defaultGraph().findObject(resmap_id, ORE_DESCRIBES);

        if (node == null) {
            return null;
        }

        return node.value().stringValue();
    }

    public String url() {
        return resmap_uri;
    }

    public String aggregation() {
        return agg_uri;
    }

    // TODO too much trouble
    @Override
    public Date modified() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String creatorName() {
        RdfNode node = ds.defaultGraph()
                .findObject(resmap_uri, DCTERMS_CREATOR);

        if (node == null) {
            return null;
        }

        if (!(node.isBlankNode() || node.isIRI())) {
            return null;
        }

        RdfNode name = ds.defaultGraph().findObject(node.value().stringValue(),
                FOAF_NAME);

        if (name == null) {
            return null;
        }

        return name.value().stringValue();
    }

    @Override
    public List<String> aggregates() {
        List<String> result = new ArrayList<String>();

        for (RdfTriple triple : ds.defaultGraph().find(agg_uri, ORE_AGGREGATES,
                null)) {
            result.add(triple.object().value().stringValue());
        }

        return result;
    }

    public String rights() {
        RdfNode node = ds.defaultGraph().findObject(resmap_uri, DC_RIGHTS);

        if (node == null) {
            return null;
        }

        return node.value().stringValue();
    }
}
