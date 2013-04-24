package rosa.scanvas.demo.website.client.presenter;

import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ManifestPanelPresenter implements PanelPresenter {
    private PanelData data;

    public interface Display extends IsWidget {
        HasClickHandlers getList();

        HasText getViewLabel();

        void setData(List<Reference<Sequence>> seq);

        int getSelectedRow(ClickEvent event);

        void resize(int width, int height);
    }

    private final Display display;
    private final HandlerManager eventBus;
    private final int panel_id;
    
    public ManifestPanelPresenter(Display display, HandlerManager eventBus, int panel_id) {
        this.display = display;
        this.eventBus = eventBus;
        this.panel_id = panel_id;
        
        bind();
    }

    private void bind() {
        display.getList().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int sel = display.getSelectedRow(event);

                if (sel >= 0) {
                    String collection = data.getManifestCollection()
                            .manifests().get(sel).uri();
                    String manifest = data.getManifest().uri();
                    String sequence = data.getManifest().sequences().get(sel)
                            .uri();

                    // TODO fire event
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

        display.getViewLabel().setText(data.getManifest().label());
        display.setData(data.getManifest().sequences());
        
        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        eventBus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
}
