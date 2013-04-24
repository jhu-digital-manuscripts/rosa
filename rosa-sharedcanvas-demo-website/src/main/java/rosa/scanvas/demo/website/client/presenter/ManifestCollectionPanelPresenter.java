package rosa.scanvas.demo.website.client.presenter;

import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ManifestCollectionPanelPresenter implements PanelPresenter {
    private PanelData data;

    public interface Display extends IsWidget {
        HasClickHandlers getList();

        HasText getViewLabel();

        void setData(List<Reference<Manifest>> list);

        int getSelectedRow(ClickEvent event);

        void resize(int width, int height);
    }

    private final Display display;
    private final HandlerManager eventBus;
    private final int panel_id;

    public ManifestCollectionPanelPresenter(Display display,
            HandlerManager eventBus, int panel_id) {
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
                    String manifest = data.getManifestCollection().manifests()
                            .get(sel).uri();

                    PanelState state = new PanelState(PanelView.MANIFEST,
                            manifest);
                    PanelRequestEvent req = new PanelRequestEvent(
                            PanelRequestEvent.PanelAction.CHANGE, panel_id,
                            state);
                    eventBus.fireEvent(req);
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

        ManifestCollection col = data.getManifestCollection();

        String label = col.label() == null ? "Unknown collection" : col.label();

        display.getViewLabel().setText(label);
        display.setData(data.getManifestCollection().manifests());

        display.getViewLabel().setText(data.getManifestCollection().label());
        display.setData(data.getManifestCollection().manifests());
        
        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        eventBus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
}
