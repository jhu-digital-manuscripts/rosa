package rosa.scanvas.viewer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GetDataEventHandler extends EventHandler {
	void retrieveData(String url);
}
