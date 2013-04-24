package rosa.scanvas.demo.website.client;

/**
 * Represents the a home view or a view of a shared canvas object which is
 * either a manifest collection, a manifest, a sequence, or a canvas. The object
 * uri is the identifier of either a manifest collection, a manifest or a
 * sequence. The manifest uri is set if this is a view of a manifest, a
 * sequence, or a canvas. The canvas_index, if not -1, gives the index of a
 * canvas in the sequence.
 */
public class PanelState {
    private final PanelView view;
    private final String object_uri;
    private final String manifest_uri;
    private final int canvas_index;

    /**
     * Create a Home view.
     */
    public PanelState() {
        this(PanelView.HOME, null, null, -1);
    }

    public PanelState(PanelView view, String object_uri) {
        this(view, object_uri, null, -1);
    }

    public PanelState(PanelView view, String object_uri, String manifest_uri) {
        this(view, object_uri, manifest_uri, -1);
    }

    public PanelState(PanelView view, String object_uri, String manifest_uri,
            int canvas_index) {
        this.view = view;
        this.object_uri = (object_uri == null && view == PanelView.MANIFEST) ? manifest_uri
                : object_uri;
        this.manifest_uri = (manifest_uri == null && view == PanelView.MANIFEST) ? object_uri
                : manifest_uri;
        this.canvas_index = canvas_index;
    }

    public PanelView getView() {
        return view;
    }

    public int getCanvasIndex() {
        return canvas_index;
    }

    public String getObjectUri() {
        return object_uri;
    }

    public String getManifestUri() {
        return manifest_uri;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + canvas_index;
        result = prime * result
                + ((manifest_uri == null) ? 0 : manifest_uri.hashCode());
        result = prime * result
                + ((object_uri == null) ? 0 : object_uri.hashCode());
        result = prime * result + ((view == null) ? 0 : view.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof PanelState))
            return false;
        PanelState other = (PanelState) obj;
        if (canvas_index != other.canvas_index)
            return false;
        if (manifest_uri == null) {
            if (other.manifest_uri != null)
                return false;
        } else if (!manifest_uri.equals(other.manifest_uri))
            return false;
        if (object_uri == null) {
            if (other.object_uri != null)
                return false;
        } else if (!object_uri.equals(other.object_uri))
            return false;
        if (view != other.view)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PanelHistoryState [view=" + view + ", object_uri=" + object_uri
                + ", manifest_uri=" + manifest_uri + ", canvas_index="
                + canvas_index + "]";
    }
}