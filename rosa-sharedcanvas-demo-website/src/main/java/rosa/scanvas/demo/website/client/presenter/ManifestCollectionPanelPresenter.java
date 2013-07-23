package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.google.gwt.view.client.SelectionChangeEvent;

public class ManifestCollectionPanelPresenter extends BasePanelPresenter {

    public interface Display extends BasePanelPresenter.Display {
        void setCollection(List<Reference<Manifest>> col, String title);

        void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler);

        Reference<Manifest> getSelectedManifest();
    }

    private final Display display;

    public ManifestCollectionPanelPresenter(Display display,
            HandlerManager eventBus, int panel_id) {
        super(display, eventBus, panel_id);
        this.display = display;

        bind();
    }

    private void bind() {
        display.addSelectionChangeEventHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                Reference<Manifest> sel = display.getSelectedManifest();

                if (sel == null) {
                    return;
                }

                PanelState state = new PanelState(PanelView.MANIFEST, sel.uri());
                PanelRequestEvent req = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.CHANGE, panelId(), state);
                eventBus().fireEvent(req);
            }
        });
    }

    @Override
    public void display(int width, int height, PanelData data) {
        super.display(width, height, data);

        ManifestCollection col = data.getManifestCollection();
        String title = col.label() == null ? "Unknown" : col.label();

        List<Reference<Manifest>> manifests = col.manifests();
        
        Collections.sort(manifests, new Comparator<Reference<Manifest>>() {
            @Override
            public int compare(Reference<Manifest> r1, Reference<Manifest> r2) {
                return r1.label().compareTo(r2.label());
            }
        });

        display.setCollection(manifests, title);

        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }
}
