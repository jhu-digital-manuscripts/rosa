package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.Window;

public class MultiLineTextDisplayElement extends DisplayElement {
    private final String text;
    private final int[][] coords;
    private final ImageData image_data;
    
    private PopupPanel popup;
    
    // Green
    private final CssColor color_fill = CssColor.make(0, 255, 0);

    public MultiLineTextDisplayElement(String id, int x, int y, int width, int height,
            String text, int[][] coords) {
        super(id, x, y, width, height);
        this.coords = coords;
        this.text = text;
        
        popup = new PopupPanel(true, false);
        HTML content = new HTML(text);
    	
    	popup.setWidget(content);
        
     // Create a canvas containing the filled polygon with no border
        Canvas sub_canvas = Canvas.createIfSupported();
        sub_canvas.setCoordinateSpaceWidth(width);
        sub_canvas.setCoordinateSpaceHeight(height);
        
        Context2d context = sub_canvas.getContext2d();
        context.beginPath();
        context.moveTo(coords[0][0] - baseLeft(), coords[0][1] - baseTop());

        for (int i = 1; i < coords.length; i++) {
        	context.lineTo(coords[i][0] - baseLeft(), coords[i][1] - baseTop());
        }
    	
        context.setFillStyle(color_fill);
        context.fill();
        
        context.closePath();
        
        this.image_data = context.getImageData(0, 0, width, height);
    }

    public String text() {
        return text;
    }

    /**
     * Coordinates that define the bounding box of this display element.
     */
    public int[][] coordinates() {
        return coords;
    }
    
    @Override
    public void doMouseClick(final int x, final int y) {
    	popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
    		int left = x;
    		int top = y;

    		public void setPosition(int width, int height) {
    			if (left + width > Window.getClientWidth()) {
    				left = Window.getClientWidth() - width;
    			}
    			
    			if (top + height > Window.getClientHeight()) {
    				top = Window.getClientHeight() - height;
    			}
    			
    			popup.setPopupPosition(left, top);
    		}
    	});
    }
    
    /**
     * Returns a short string to be displayed on the canvas.
     */
    public String firstLine() {
    	// TODO
    	return "Click Here";
    }
    
    /**
     * Whether or not the point (x, y) lies within the display element
     * 
     * @param x
     * 			must be in the coordinates of the display element
     * @param y
     * 			must be in the coordinates of the display element
     */
    @Override
    public boolean contains(int x, int y) {
    	if (!super.contains(x, y)) {
    		return false;
    	}
    	
    	x -= baseLeft();
    	y -= baseTop();
        
    	return image_data.getGreenAt(x, y) == 255;
    }
}
