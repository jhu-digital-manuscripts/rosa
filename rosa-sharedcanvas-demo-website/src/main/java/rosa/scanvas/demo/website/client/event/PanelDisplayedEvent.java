package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelData;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates a panel has displayed the given data.
 */
public class PanelDisplayedEvent extends GwtEvent<PanelDisplayedEventHandler> {
    public static Type<PanelDisplayedEventHandler> TYPE = new Type<PanelDisplayedEventHandler>();

    private final int panel_id;
    private final PanelData data;

    public PanelDisplayedEvent(int panel_id, PanelData data) {
        this.panel_id = panel_id;
        this.data = data;
    }

    public PanelData getPanelData() {
        return data;
    }

    public int getPanelId() {
        return panel_id;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PanelDisplayedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelDisplayedEventHandler handler) {
        handler.onPanelDisplayed(this);
    }

}
