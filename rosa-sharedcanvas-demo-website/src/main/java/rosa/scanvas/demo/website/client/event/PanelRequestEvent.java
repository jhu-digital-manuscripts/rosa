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
    private final int zoom_level;
    private final int[] position = new int[2];

    public PanelRequestEvent(PanelAction action, int panel_id) {
        this(action, panel_id, null, -1, -112, -112);
    }

    public PanelRequestEvent(PanelAction action, PanelState state) {
        this(action, -1, state, -1);
    }

    public PanelRequestEvent(PanelAction action, int panel_id, PanelState state) {
        this(action, panel_id, state, -1);
    }
    
    public PanelRequestEvent(PanelAction action, int panel_id, PanelState state, 
    		int zoom_level, int... position) {
    	this.action = action;
        this.panel_id = panel_id;
        this.state = state;
        this.zoom_level = zoom_level;
        
        this.position[0] = position.length > 0 ? position[0] : -111;
        this.position[1] = position.length > 1 ? position[1] : -111;
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
    
    public int getZoomLevel() {
    	return zoom_level;
    }
    
    public int[] getPosition() {
    	return position;
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
