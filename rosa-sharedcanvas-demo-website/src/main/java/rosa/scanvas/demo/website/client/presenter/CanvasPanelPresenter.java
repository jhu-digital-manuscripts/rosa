package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionHandler;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
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
        Label getLabel();

        DisplayAreaView getDisplayAreaWidget();
        
        Button getZoomInButton();
        
        Button getZoomOutButton();
        
        Button getResetButton();
        
//        void showDialogBox(String label, String text, boolean tei);
        
//        void hideDialogBox(String label, String text, boolean tei);
        
//        void selected(boolean is_selected);
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
    	eventBus().addHandler(AnnotationSelectionEvent.TYPE, 
    			new AnnotationSelectionHandler() {
    		public void onSelection(AnnotationSelectionEvent event) {
    			if (event.getPanel() == panelId()) {
    				setAnnotationVisible(event.getAnnotation(), event.getStatus());
    			}
    		}
    	});
    	
    	display.getZoomInButton().addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			DisplayAreaView view = display.getDisplayAreaWidget();
    			view.area().zoomIn();
    			view.redraw();
    		}
    	});
    	
    	display.getZoomOutButton().addClickHandler(new ClickHandler() {
    		@Override
    		public void onClick(ClickEvent event) {
    			DisplayAreaView view = display.getDisplayAreaWidget();
    			view.area().zoomOut();
    			view.redraw();
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
    
    // TODO Can save display elements and operate on them for efficiency
    private void setAnnotationVisible(Annotation ann, boolean status) {

    	if (!AnnotationUtil.isSpecificResource(ann) &&
    			ann.body().isText()) {
    		// nontargeted text annotations are not displayed on the canvas
    		/*boolean tei = ann.body().format().endsWith("xml");
    		
    		if (status) {
    			display.showDialogBox(ann.label(), ann.body().textContent(), tei);
    		} else {
    			display.hideDialogBox(ann.label(), ann.body().textContent(), tei);
    		}*/
    		
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
    public void bind_annotation_checkbox(final CheckBox checkbox, final Annotation ann) {
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
    public void display(PanelData data) {
    	super.display(data);
        this.canvas = data.getCanvas();
        els.clear();
        
        display.getLabel().setText(data.getManifest().label() 
        		+ ": " + canvas.label());
        update();

        // convert annotations into display elements
        for (AnnotationList list : data.getAnnotationLists()) {
        	for (Annotation ann : list) {
        		DisplayElement el = AnnotationUtil.annotationToDisplayElement(
        				ann, this.canvas);
        		if (el != null) {
        			els.add(el);
        		}
        	}
        }
        display.getDisplayAreaWidget().area().setContent(els);
        update();
        
        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        super.resize(width, height);

        this.width = width - 22;
        this.height = height - 50;
        
        update();
    }

    private void update() {
        if (canvas == null || width < 0 || height < 0) {
            return;
        }
        
        DisplayAreaView da = display.getDisplayAreaWidget();
        DisplayArea area = new DisplayArea(canvas.width(), canvas.height(),
                width, height-70);
        // Copy the zoom level and center position of the old display area into
        // the new display area, so the view does not reset on browser resize
        DisplayArea old_area = da.area();
        if (old_area != null) {
	        if (old_area.zoomLevel() < area.numZoomLevels()) {
	        	area.setZoomLevel(old_area.zoomLevel());
	        }
	        area.setViewportBaseCenter(old_area.viewportBaseCenterX(),
	        		old_area.viewportBaseCenterY());
    	}
        
        area.setContent(els);
        da.display(area);
        da.lockDisplay(false);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	display.selected(is_selected);
    }
}
