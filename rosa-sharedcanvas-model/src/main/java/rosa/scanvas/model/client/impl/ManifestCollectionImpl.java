package rosa.scanvas.model.client.impl;

import java.util.List;

import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.rdf.RdfGraph;

public class ManifestCollectionImpl extends ResourceMapImpl implements
        ManifestCollection {

    public ManifestCollectionImpl(RdfGraph graph) {
        super(graph);
    }

    @Override
    public List<Reference<Manifest>> manifests() {
        return aggregatedReferences(SHARED_CANVAS_MANIFEST, Manifest.class);
    }
}
