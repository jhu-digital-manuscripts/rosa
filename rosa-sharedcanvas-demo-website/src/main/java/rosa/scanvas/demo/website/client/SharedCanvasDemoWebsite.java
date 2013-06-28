package rosa.scanvas.demo.website.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point of application.
 */
public class SharedCanvasDemoWebsite implements EntryPoint {
    public void onModuleLoad() {
    	MainController main = new MainController(new HandlerManager(null));
        RootLayoutPanel.get().add(main.asWidget());

        main.go();
    }
}
