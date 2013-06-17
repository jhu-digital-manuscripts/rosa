package rosa.scanvas.demo.website.client.view;

import java.util.ArrayList;
import java.util.HashMap;

import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.TranscriptionViewer;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;
import rosa.scanvas.model.client.Annotation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.Window;
public class CanvasView extends BasePanelView implements CanvasPanelPresenter.Display {
	
	private final Panel main;
    //private final Label title;
    private final DisplayAreaView area_view;
    
    private final Button zoomInButton;
    private final Button zoomOutButton;
    private final Button resetButton;
    
    public CanvasView() {
        this.main = new FlowPanel();
        //this.title = new Label();
        
        this.zoomInButton = new Button("Zoom In");
        this.zoomOutButton = new Button("Zoom Out");
        this.resetButton = new Button("Reset");
        
        this.area_view = new DisplayAreaView();
        
        FlowPanel canvas_toolbar = new FlowPanel();
        canvas_toolbar.setStylePrimaryName("CanvasToolbar");

 //       main.add(title);
        main.add(area_view);
        main.add(canvas_toolbar);
        
        canvas_toolbar.add(zoomInButton);
        canvas_toolbar.add(zoomOutButton);
        canvas_toolbar.add(resetButton);

        main.setStylePrimaryName("View");
        //title.addStyleName("PanelTitle");
        
        addContent(main);
    }

/*    @Override
    public Label getLabel() {
        return title;
    }*/

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
