package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.demo.website.client.PanelData;

import com.google.gwt.event.shared.GwtEvent;

public class DataUpdateEvent extends GwtEvent<DataUpdateEventHandler> {
	public static Type<DataUpdateEventHandler> TYPE = new Type<DataUpdateEventHandler>();
	private final PanelData data;
	
	public DataUpdateEvent(PanelData data) {
		this.data = data;
	}
	
	public PanelData getData() {
		return data;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<DataUpdateEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DataUpdateEventHandler handler) {
		handler.onDataUpdate(this);
	}
	
	
}
