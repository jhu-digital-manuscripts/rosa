package rosa.scanvas.demo.website.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class SidebarViewChangeEvent extends GwtEvent<SidebarViewChangeEventHandler>{
	public static Type<SidebarViewChangeEventHandler> TYPE = new Type<SidebarViewChangeEventHandler>();
	
	private final String view;
	
	public SidebarViewChangeEvent(String view) {
		this.view = view;
	}
	
	public String getView() { return view; }
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<SidebarViewChangeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SidebarViewChangeEventHandler handler) {
		handler.onListViewChange(this);
	}
	
	
	
}
