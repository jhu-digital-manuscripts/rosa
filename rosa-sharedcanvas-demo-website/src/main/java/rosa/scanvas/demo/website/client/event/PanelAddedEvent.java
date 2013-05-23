package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelState;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a request to add a panel, remove a panel, or change the view of a
 * panel.
 */
public class PanelAddedEvent extends GwtEvent<PanelAddedEventHandler> {
    public static Type<PanelAddedEventHandler> TYPE = new Type<PanelAddedEventHandler>();

    private final int panel_id;

    public PanelAddedEvent(int panel_id) {
        this.panel_id = panel_id;
    }

    public int getPanelId() {
        return panel_id;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PanelAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelAddedEventHandler handler) {
        handler.onPanelAdded(this);
    }
}
