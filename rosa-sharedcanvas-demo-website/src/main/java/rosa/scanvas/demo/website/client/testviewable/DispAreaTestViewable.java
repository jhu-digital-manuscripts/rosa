package rosa.scanvas.demo.website.client.testviewable;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Separate GWT module useful for testing the DisplayArea with
 * predefined data.
 */
public class DispAreaTestViewable implements EntryPoint {
	
	public final static int width = 3816;
	public final static int height = 5429;
	
	private DialogBox top = new DialogBox(false, false);
	
	private MainPresenter presenter;
    //private MainView view = new MainView(width, height);
	private MainView view = new MainView(width, height, 1000, 600);
	
    public void onModuleLoad() {
        FlowPanel panel = new FlowPanel();
        top.add(panel);
        
        top.setText("Test Canvas");
        top.setPopupPosition(0, 0);
        top.show();
        
    	presenter = new MainPresenter(view, panel);
    	bind();
    }
    
    private void bind() {
    	presenter.getCloseButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			top.hide();
    		}
    	});
    }
    
}
