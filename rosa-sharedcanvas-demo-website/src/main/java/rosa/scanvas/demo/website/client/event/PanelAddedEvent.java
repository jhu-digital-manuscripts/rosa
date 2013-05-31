package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelState;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that a panel has been added to the viewable content area.
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
