package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Canvas;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public class CanvasPanelPresenter extends BasePanelPresenter {
    public interface Display extends BasePanelPresenter.Display {
        DisplayAreaView getDisplayAreaWidget();
        
        Button getZoomInButton();
        
        Button getZoomOutButton();
        
        Button getResetButton();
    }

    private final Display display;
    private Canvas canvas;
    private int width, height;
    
    private List<DisplayElement> els = new ArrayList<DisplayElement>();

    public CanvasPanelPresenter(Display display, HandlerManager eventBus, int panel_id) {
    	super(display, eventBus, panel_id);
        this.display = display;
        this.width = -1;
        this.height = -1;
        
        bind();
    }

    /**
     * Bind event handlers to the event bus and the DOM
     */
    private void bind() {
    	display.getZoomInButton().addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			int old_width = display.getDisplayAreaWidget().area().viewportBaseWidth();
    			display.getDisplayAreaWidget().area().zoomIn();
    			display.getDisplayAreaWidget().animatedRedraw(old_width);
    		}
    	});
    	
    	display.getZoomOutButton().addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			DisplayAreaView view = display.getDisplayAreaWidget();
    			int old_width = view.area().viewportBaseWidth();
    			view.area().zoomOut();
    			display.getDisplayAreaWidget().animatedRedraw(old_width);
    		}
    	});
    	
    	display.getResetButton().addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			DisplayAreaView view = display.getDisplayAreaWidget();
    			view.resetDisplay();
    		}
    	});
    }
    
    @Override
    protected void doDuplicatePanel() {
    	DisplayArea area = display.getDisplayAreaWidget().area();
    	
    	PanelRequestEvent req = new PanelRequestEvent(
				PanelRequestEvent.PanelAction.ADD, panelId(), null, area.zoomLevel(),
				area.viewportBaseCenterX(), area.viewportBaseCenterY());
		eventBus().fireEvent(req);
    }
    
    private void setAnnotationVisible(Annotation ann, boolean status) {

    	if (!AnnotationUtil.isSpecificResource(ann) &&
    			ann.body().isText()) {
    		return;
    	}

        DisplayAreaView da = display.getDisplayAreaWidget();
        DisplayElement el = da.area().get(ann.uri());

        if (el != null) {
            el.setVisible(status);
        }

        da.redraw();
    }
    
    @Override
    protected void bind_annotation_checkbox(final CheckBox checkbox, final Annotation ann) {
    	checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
    		public void onValueChange(ValueChangeEvent<Boolean> event) {
    			setAnnotationVisible(ann, event.getValue());
    			data().setAnnotationStatus(ann, event.getValue());
    		}
    	});
    	
    }
    
    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(int width, int height, PanelData data) {
    	super.display(width, height, data);
        this.canvas = data.getCanvas();
        els.clear();
      
        if (!isResized()) {
        	this.width = width;
        	this.height = height;
        }

        DisplayAreaView view = display.getDisplayAreaWidget();
        
        DisplayArea area = view.area();
        area.setBaseSize(canvas.width(), canvas.height());
        area.resizeViewport(this.width - 22, 
        		this.height - 70 - display.getContextHeight());
        
        // Set position and zoom level of viewport, if possible
        if (data.getZoomLevel() != -1) {
        	area.setZoomLevel(area.numZoomLevels() 
        			>= data.getZoomLevel() ? data.getZoomLevel() : 0);
        }
        
        if (data.getPosition().length == 2 && data.getPosition()[0] != -1
        		&& data.getPosition()[1] != -1) {
        	area.setViewportBaseCenter(data.getPosition()[0],
        			data.getPosition()[1]);
        }
        
        // convert annotations into display elements
        for (AnnotationList list : data.getAnnotationLists()) {
        	for (Annotation ann : list) {
        		DisplayElement el = AnnotationUtil.annotationToDisplayElement(
        				ann, this.canvas);
        		if (el != null) {
                    el.setVisible(data.getAnnotationStatus(ann));
        			els.add(el);        			
        		}
        	}
        }
        area.setContent(els);
        
        view.lockDisplay(false);
        view.display();
      
        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        super.resize(width, height);

        this.width = width;
        this.height = height;

        display.getDisplayAreaWidget().resize(width - 22, 
        		height - 70 - display.getContextHeight());
    }
}
