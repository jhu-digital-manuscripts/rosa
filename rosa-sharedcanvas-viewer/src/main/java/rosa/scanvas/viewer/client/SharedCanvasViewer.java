package rosa.scanvas.viewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SharedCanvasViewer implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.PX);
		RootLayoutPanel.get().add(mainPanel);

		HandlerManager eventBus = new HandlerManager(null);
		Controller appViewer = new MainController(eventBus);
		appViewer.go(mainPanel);
	}
}
