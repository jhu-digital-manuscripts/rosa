package rosa.scanvas.demo.website.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PanelMoveEvent extends GwtEvent<PanelMoveEventHandler> {
    public static Type<PanelMoveEventHandler> TYPE = new Type<PanelMoveEventHandler>();

    public enum PanelDirection {
        UP, DOWN, HORIZONTAL
    }

    private final PanelDirection direction;
    private final int panel_id;

    public PanelMoveEvent(PanelDirection direction, int panel_id) {
        this.direction = direction;
        this.panel_id = panel_id;
    }

    public PanelDirection getDirection() {
        return direction;
    }

    public int getPanelId() {
        return panel_id;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<PanelMoveEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PanelMoveEventHandler handler) {
        handler.onPanelMove(this);
    }
}
