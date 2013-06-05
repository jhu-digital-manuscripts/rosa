package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

/**
 * Widget holding a thumbnail and an associated label
 */
public class Thumbnail extends Composite implements HasClickHandlers {
    private final Grid grid;
    private final WebImage image;
    private final int canvas_index;

    public Thumbnail(WebImage image, String label, int canvas_index) {
        this.image = image;
        this.canvas_index = canvas_index;
        this.grid = new Grid(2, 1);
        grid.setStylePrimaryName("Thumbnail");        
        
        grid.setWidget(0, 0, image);
        grid.setWidget(1, 0, new Label(label));

        initWidget(grid);
    }

    public void makeViewable() {
        image.makeViewable();
    }

    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return grid.addClickHandler(handler);
    }
    
    public int canvasIndex() {
    	return canvas_index;
    }
}