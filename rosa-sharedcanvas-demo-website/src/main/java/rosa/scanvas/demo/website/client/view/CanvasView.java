package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

public class CanvasView extends BasePanelView implements CanvasPanelPresenter.Display {
	
	private final Panel main;
    private final DisplayAreaView area_view;
    
    private final Button zoomInButton;
    private final Button zoomOutButton;
    private final Button resetButton;
    
    public CanvasView() {
        this.main = new FlowPanel();
        
        this.zoomInButton = new Button(Messages.INSTANCE.zoomIn());
        this.zoomOutButton = new Button(Messages.INSTANCE.zoomOut());
        this.resetButton = new Button(Messages.INSTANCE.reset());
        
        this.area_view = new DisplayAreaView();
        
        FlowPanel canvas_toolbar = new FlowPanel();
        canvas_toolbar.setStylePrimaryName("CanvasToolbar");

        main.add(area_view);
        main.add(canvas_toolbar);
        
        canvas_toolbar.add(zoomInButton);
        canvas_toolbar.add(zoomOutButton);
        canvas_toolbar.add(resetButton);

        main.setStylePrimaryName("View");
        
        addContent(main);
    }

    @Override
    public DisplayAreaView getDisplayAreaWidget() {
        return area_view;
    }
    
    @Override
    public Button getZoomInButton() {
    	return zoomInButton;
    }
    
    @Override
    public Button getZoomOutButton() {
    	return zoomOutButton;
    }
    
    @Override
    public Button getResetButton() {
    	return resetButton;
    }
}
