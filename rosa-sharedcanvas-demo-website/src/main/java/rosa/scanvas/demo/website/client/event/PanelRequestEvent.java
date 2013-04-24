package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelState;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents a request to add a panel, remove a panel, or change the view of a
 * panel.
 */
public class PanelRequestEvent extends GwtEvent<PanelRequestEventHandler> {
    public static Type<PanelRequestEventHandler> TYPE = new Type<PanelRequestEventHandler>();

    public enum PanelAction {
        ADD, REMOVE, CHANGE
    }

    private final PanelAction action;
    private final int panel_id;
    private final PanelState state;

    public PanelRequestEvent(PanelAction action, int panel_id) {
        this(action, panel_id, null);
    }

    public PanelRequestEvent(PanelAction action, PanelState state) {
        this(action, -1, state);
    }

    public PanelRequestEvent(PanelAction action, int panel_id, PanelState state) {
        this.action = action;
        this.panel_id = panel_id;
        this.state = state;
    }

    public PanelAction getAction() {
        return action;
    }

    public int getPanelId() {
        return panel_id;
    }

    public PanelState getPanelState() {
        return state;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PanelRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelRequestEventHandler handler) {
        handler.onPanelRequest(this);
    }
}
