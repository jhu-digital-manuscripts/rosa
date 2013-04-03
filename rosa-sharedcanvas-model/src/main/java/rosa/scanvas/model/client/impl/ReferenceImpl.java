package rosa.scanvas.model.client.impl;

import rosa.scanvas.model.client.Reference;

public class ReferenceImpl<T> implements Reference<T> {
    private final String uri;
    private final Class<T> type;
    private final String label;

    public ReferenceImpl(String uri, Class<T> type, String label) {
        this.uri = uri;
        this.type = type;
        this.label = label;
    }

    public String uri() {
        return uri;
    }

    public Class<T> type() {
        return type;
    }

    public String label() {
        return label;
    }
}
