package rosa.scanvas.demo.website.client;

import rosa.scanvas.demo.website.client.presenter.PanelPresenter;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Panel {
    private final int id;
    private PanelPresenter presenter;
    private PanelData data;
    private PanelState state;

    public Panel(PanelPresenter presenter, int id) {
        this.id = id;
        this.presenter = presenter;
    }

    /**
     * Load the data needed by the presenter and update it.
     */
    public void display(PanelState state) {
        this.state = state;
        this.data = new PanelData();

        AsyncCallback<PanelData> cb = new AsyncCallback<PanelData>() {
            @Override
            public void onSuccess(PanelData data) {
                presenter.display(data);
            }

            @Override
            public void onFailure(Throwable err) {
                Window.alert("Error displaying view: " + err.getMessage());
            }
        };

        switch (state.getView()) {
        case CANVAS:
            // TODO Actually have to update history stuff because need
            // canvas, sequence, and manifest
            PanelData.loadManifestSequenceAndAnnotationLists(
                    state.getManifestUri(), state.getObjectUri(), data, cb);
            break;
        case HOME:
            cb.onSuccess(null);
            break;
        case MANIFEST:
            PanelData.loadManifest(state.getObjectUri(), data, cb);
            break;
        case MANIFEST_COLLECTION:
            PanelData.loadManifestCollection(state.getObjectUri(), data, cb);
            break;
        case SEQUENCE:
            PanelData.loadManifestSequenceAndAnnotationLists(
                    state.getManifestUri(), state.getObjectUri(), data, cb);
            break;
        default:
            throw new RuntimeException("Unhandled view: " + state.getView());
        }
    }

    public int getId() {
        return id;
    }

    public PanelState getState() {
        return state;
    }

    public PanelData getData() {
        return data;
    }

    public PanelPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PanelPresenter presenter) {
        this.presenter = presenter;
    }
}