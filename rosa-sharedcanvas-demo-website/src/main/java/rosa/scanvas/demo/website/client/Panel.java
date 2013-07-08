package rosa.scanvas.demo.website.client;

import rosa.scanvas.demo.website.client.Messages;
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
        this.data = new PanelData();
    }

    /**
     * Load the data needed by the presenter and update it.
     * 
     * @param state
     * @param width
     * @param height
     */
    public void display(final int width, final int height, PanelState state) {
        this.state = state;
        data.setManifestCollection(null);
        data.setManifest(null);
        data.setSequence(null);
        data.setCanvas(null);

        AsyncCallback<PanelData> cb = new AsyncCallback<PanelData>() {
            @Override
            public void onSuccess(PanelData data) {
                presenter.display(width, height, data);
            }

            @Override
            public void onFailure(Throwable err) {
                Window.alert(Messages.INSTANCE.errorOnView() 
                		+ err.getMessage());
            }
        };

        switch (state.getView()) {
        case CANVAS:
        	PanelData.loadManifestSequenceCanvasAndAnnotationLists(
            		state.getManifestUri(), state.getObjectUri(), 
            		data, state.getCanvasIndex(), cb);
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
            throw new RuntimeException(Messages.INSTANCE.unhandledView()
            		+ state.getView());
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
    
    public void setCanvasZoomLevel(int zoom_level) {
    	data.setZoomLevel(zoom_level);
    }
    
    public void setViewportPosition(int[] position) {
    	data.setPosition(position);
    }

    public PanelPresenter getPresenter() {
        return presenter;
    }

    public void setPresenter(PanelPresenter presenter) {
        this.presenter = presenter;
    }
}