package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelState;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that a panel has been selected, either by an action being performed
 * on it, or it being selected in the sidebar.
 */
public class PanelSelectedEvent extends GwtEvent<PanelSelectedEventHandler> {
    public static Type<PanelSelectedEventHandler> TYPE = new Type<PanelSelectedEventHandler>();

    private final int panel_id;

    public PanelSelectedEvent(int panel_id) {
        this.panel_id = panel_id;
    }

    public int getPanelId() {
        return panel_id;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PanelSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelSelectedEventHandler handler) {
        handler.onPanelSelected(this);
    }
}
