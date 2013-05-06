package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.Html5DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionEvent;
import rosa.scanvas.demo.website.client.event.AnnotationSelectionHandler;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.Canvas;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CanvasPanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
        Label getLabel();

        Html5DisplayAreaView getDisplayAreaWidget();
    }

    private final Display display;
    private final int panel_id;
    private final HandlerManager event_bus;
    private Canvas canvas;
    private int width, height;

    public CanvasPanelPresenter(Display display, HandlerManager eventBus, int panel_id) {
        this.display = display;
        this.event_bus = eventBus;
        this.panel_id = panel_id;
        this.width = -1;
        this.height = -1;
        
        bind();
    }

    /**
     * Bind event handlers to the event bus
     */
    private void bind() {
    	event_bus.addHandler(AnnotationSelectionEvent.TYPE, 
    			new AnnotationSelectionHandler() {
    		public void onSelection(AnnotationSelectionEvent event) {
    			if (event.getPanel() == panel_id) {
    				setAnnotationVisible(event.getAnnotation(), event.getStatus());
    			}
    		}
    	});
    }
    
    // TODO Can save display elements and operate on them for efficiency
    private void setAnnotationVisible(Annotation ann, boolean status) {
    	if (!AnnotationUtil.isSpecificResource(ann) &&
    			ann.body().isText()) {
    	// nontargeted text annotations are not displayed on the canvas
    		return;
    	}
    	
        Html5DisplayAreaView da = display.getDisplayAreaWidget();
        DisplayElement el = da.area().get(ann.uri());

        if (el != null) {
            el.setVisible(status);
        }

        da.redraw();
    }
    
    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(PanelData data) {
        this.canvas = data.getCanvas();

        // TODO display.getDisplayAreaWidget().area().setContent(els);
        
        update();

        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        event_bus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }

        this.width = width;
        this.height = height;

        update();
    }

    private void update() {
        if (canvas == null || width < 0 || height < 0) {
            return;
        }

        Html5DisplayAreaView da = display.getDisplayAreaWidget();
        DisplayArea area = new DisplayArea(canvas.width(), canvas.height(),
                width, height);
        da.display(area);
    }
}
