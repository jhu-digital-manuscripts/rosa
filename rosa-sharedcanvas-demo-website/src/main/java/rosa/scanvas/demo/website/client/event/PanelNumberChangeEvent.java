package rosa.scanvas.demo.website.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class PanelNumberChangeEvent extends GwtEvent<PanelNumberChangeEventHandler> {
	public static Type<PanelNumberChangeEventHandler> TYPE = new Type<PanelNumberChangeEventHandler>();
	private final String message;
	private final int selectedPanel;
	
	public PanelNumberChangeEvent(String message) {
		this.message = message;
		selectedPanel = -1;
	}
	
	public PanelNumberChangeEvent(String message, int selectedPanel) {
		this.message = message;
		this.selectedPanel = selectedPanel;
	}
	
	public String getMessage() { return message; }
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
