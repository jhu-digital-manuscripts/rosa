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
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ManifestPanelPresenter extends BasePanelPresenter {
    private PanelData data;

    public interface Display extends BasePanelPresenter.Display {
    	void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler);

        void setManifest(List<String> sequences, String label);
        
        String getSelectedSequence();
    }

    private final Display display;
    private final HashMap<String, String> seq_map;

    public ManifestPanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
    	super(display, event_bus, panel_id);
        this.display = display;
        
        this.seq_map = new HashMap<String, String>();

        bind();
    }

    private void bind() {
    	display.addSelectionChangeEventHandler(new SelectionChangeEvent.Handler() {
    		public void onSelectionChange(SelectionChangeEvent event) {
    			String sel = display.getSelectedSequence();
    			
    			if (sel == null) {
    				return;
    			}
    			
    			String manifest = data.getManifest().uri();
    			
    			PanelState state = new PanelState(PanelView.SEQUENCE,
                        seq_map.get(sel), manifest);

                PanelRequestEvent req = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.CHANGE, panelId(),
                        state);
                eventBus().fireEvent(req);
    		}
    	});
    }

    @Override
    public void display(int width, int height, PanelData data) {
    	super.display(width, height, data);
    	this.data = data;
    	
    	Manifest manifest = this.data.getManifest();
    	String label = manifest.label();

        if (label == null) {
            label = "Unknown title";
        }
    	
        List<String> sequences = new ArrayList<String>();
        for (Reference<Sequence> ref : manifest.sequences()) {
        	sequences.add(ref.label());
        	seq_map.put(ref.label(), ref.uri());
        }

        display.setManifest(sequences, label);
        
        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }
}
