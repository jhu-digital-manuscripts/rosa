package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ManifestCollectionPanelPresenter extends BasePanelPresenter {
    private PanelData data;

    public interface Display extends BasePanelPresenter.Display {
    	void setCollection(List<String> col, String title);
        
        void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler);
        
        String getSelectedManifest();
    }

    private final Display display;
    private final HashMap<String, String> manifests;

    public ManifestCollectionPanelPresenter(Display display,
            HandlerManager eventBus, int panel_id) {
    	super(display, eventBus, panel_id);
        this.display = display;
        
        this.manifests = new HashMap<String, String>();

        bind();
    }

    private void bind() {
    	display.addSelectionChangeEventHandler(new SelectionChangeEvent.Handler() {
    		public void onSelectionChange(SelectionChangeEvent event) {
    			String selected = display.getSelectedManifest();
    			
    			if (selected == null) {
    				return;
    			}
    			
    			PanelState state = new PanelState(PanelView.MANIFEST,
    					manifests.get(selected));
    			PanelRequestEvent req = new PanelRequestEvent(
    					PanelRequestEvent.PanelAction.CHANGE, panelId(),
    					state);
    			eventBus().fireEvent(req);
    		}
    	});
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(int width, int height, PanelData data) {
    	super.display(width, height, data);
        this.data = data;

        ManifestCollection col = data.getManifestCollection();
        String title = col.label() == null ? "Unknown collection" : col.label();
        
        List<String> labels = new ArrayList<String>();
        for (Reference<Manifest> ref : col.manifests()) {
        	labels.add(ref.label());
        	manifests.put(ref.label(), ref.uri());
        }
        
        display.setCollection(labels, title);

        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }

/*    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }*/
}
