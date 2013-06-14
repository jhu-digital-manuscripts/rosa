package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.ManifestCollection;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ManifestCollectionPanelPresenter extends BasePanelPresenter {
    private PanelData data;

    public interface Display extends BasePanelPresenter.Display {
        HasClickHandlers getManifestList();

        void setCollection(ManifestCollection col);

        int getSelectedManifest();

        /*void resize(int width, int height);
        
        void selected(boolean is_selected);*/
    }

    private final Display display;

    public ManifestCollectionPanelPresenter(Display display,
            HandlerManager eventBus, int panel_id) {
    	super(display, eventBus, panel_id);
        this.display = display;

        bind();
    }

    private void bind() {
        display.getManifestList().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                int sel = display.getSelectedManifest();

                if (sel >= 0) {
                    String manifest = data.getManifestCollection().manifests()
                            .get(sel).uri();

                    PanelState state = new PanelState(PanelView.MANIFEST,
                            manifest);
                    PanelRequestEvent req = new PanelRequestEvent(
                            PanelRequestEvent.PanelAction.CHANGE, panelId(),
                            state);
                    eventBus().fireEvent(req);
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
    	super.display(data);
        this.data = data;

        ManifestCollection col = data.getManifestCollection();
        display.setCollection(col);

        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }

/*    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	display.selected(is_selected);
    }*/
}
