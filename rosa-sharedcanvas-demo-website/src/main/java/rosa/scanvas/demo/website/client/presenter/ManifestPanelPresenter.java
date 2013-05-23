package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.Manifest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ManifestPanelPresenter implements PanelPresenter {
    private PanelData data;

    public interface Display extends IsWidget {
        HasClickHandlers getSequenceList();

        void setManifest(Manifest manifest);

        int getSelectedSequence();

        void resize(int width, int height);
        
        void selected(boolean is_selected);
    }

    private final Display display;
    private final HandlerManager event_bus;
    private final int panel_id;

    public ManifestPanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
        this.display = display;
        this.event_bus = event_bus;
        this.panel_id = panel_id;

        bind();
    }

    private void bind() {
        display.getSequenceList().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int sel = display.getSelectedSequence();

                if (sel >= 0) {
                    String manifest = data.getManifest().uri();
                    String sequence = data.getManifest().sequences().get(sel)
                            .uri();

                    PanelState state = new PanelState(PanelView.SEQUENCE,
                            sequence, manifest);

                    PanelRequestEvent req = new PanelRequestEvent(
                            PanelRequestEvent.PanelAction.CHANGE, panel_id,
                            state);
                    event_bus.fireEvent(req);
                }
            }
        });
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(PanelData data) {
        this.data = data;

        display.setManifest(data.getManifest());

        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        event_bus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	display.selected(is_selected);
    }
}
