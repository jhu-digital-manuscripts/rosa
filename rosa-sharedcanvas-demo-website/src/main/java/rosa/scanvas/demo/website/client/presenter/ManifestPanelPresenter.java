package rosa.scanvas.demo.website.client.presenter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.view.client.SelectionChangeEvent;

public class ManifestPanelPresenter extends BasePanelPresenter {
    private PanelData data;

    public interface Display extends BasePanelPresenter.Display {
        void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler);

        void setManifest(List<Reference<Sequence>> sequences, String label);

        Reference<Sequence> getSelectedSequence();
    }

    private final Display display;

    public ManifestPanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
        super(display, event_bus, panel_id);
        this.display = display;

        bind();
    }

    private void bind() {
        display.addSelectionChangeEventHandler(new SelectionChangeEvent.Handler() {
            public void onSelectionChange(SelectionChangeEvent event) {
                Reference<Sequence> sel = display.getSelectedSequence();

                if (sel == null) {
                    return;
                }

                String manifest = data.getManifest().uri();

                PanelState state = new PanelState(PanelView.SEQUENCE,
                        sel.uri(), manifest);

                PanelRequestEvent req = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.CHANGE, panelId(), state);
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

        List<Reference<Sequence>> sequences = manifest.sequences();

        Collections.sort(sequences, new Comparator<Reference<Sequence>>() {
            @Override
            public int compare(Reference<Sequence> r1, Reference<Sequence> r2) {
                return r1.label().compareTo(r2.label());
            }
        });

        display.setManifest(sequences, label);

        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }
}
