package rosa.scanvas.model.client.impl;

import java.util.List;

import rosa.scanvas.model.client.Sequence;

public class JldManifest {
    private final JsonLdNode node;

    public JldManifest(JsonLdNode node) {
        this.node = node;
    }

    public String id() {
        return node.id();
    }

    public String date() {
        return null;
    }

    public String title() {
        return null;
    }

    public List<Sequence> sequences() {
        return null;
    }
}
