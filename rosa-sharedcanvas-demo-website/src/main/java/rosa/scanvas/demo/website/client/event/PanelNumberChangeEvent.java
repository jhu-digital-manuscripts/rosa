package rosa.scanvas.demo.website.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PanelNumberChangeEvent extends GwtEvent<PanelNumberChangeEventHandler> {
	
	public enum PanelAction {ADD, REMOVE, CHANGE}
	
	public static Type<PanelNumberChangeEventHandler> TYPE = new Type<PanelNumberChangeEventHandler>();
	private final PanelAction message;
	private final int selectedPanel;
	
	public PanelNumberChangeEvent(PanelAction message) {
		this.message = message;
		selectedPanel = -1;
	}
	
	public PanelNumberChangeEvent(PanelAction message, int selectedPanel) {
		this.message = message;
		this.selectedPanel = selectedPanel;
	}
	
	public PanelAction getMessage() { return message; }
	public int getSelectedPanel() { return selectedPanel; }
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PanelNumberChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(PanelNumberChangeEventHandler handler) {
		handler.onPanelNumberChange(this);
	}
	
	
}
