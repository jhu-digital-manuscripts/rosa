package rosa.scanvas.demo.website.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class GetDataEvent extends GwtEvent<GetDataEventHandler>{
	public static Type<GetDataEventHandler> TYPE = new Type<GetDataEventHandler>();
	private final String url;

	public GetDataEvent(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GetDataEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GetDataEventHandler handler) {
		handler.retrieveData(this);
	}
}
