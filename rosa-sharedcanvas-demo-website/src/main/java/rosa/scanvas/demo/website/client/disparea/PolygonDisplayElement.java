package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.user.client.Window;
public class PolygonDisplayElement extends DisplayElement {
    private final int[][] coords;
    private final ImageData image_data;

    // Green
    private final CssColor fill_color = CssColor.make(0, 255, 0);
    
    public PolygonDisplayElement(String id, int x, int y, int width,
            int height, int[][] coords) {
        super(id, x, y, width, height);
        this.coords = coords;
        
        // Create a canvas containing the filled polygon with no border
        Canvas sub_canvas = Canvas.createIfSupported();
        sub_canvas.setCoordinateSpaceWidth(baseWidth());
        sub_canvas.setCoordinateSpaceHeight(baseHeight());
        
        Context2d context = sub_canvas.getContext2d();
        context.beginPath();
        context.moveTo(coords[0][0] - baseLeft(), coords[0][1] - baseTop());

        for (int i = 1; i < coords.length; i++) {
        	context.lineTo(coords[i][0] - baseLeft(), coords[i][1] - baseTop());
        }
    	context.closePath();
        context.setFillStyle(fill_color);
        context.fill();
       
        this.image_data = context.getImageData(0, 0, baseWidth(), baseHeight());
    }

    public int[][] coordinates() {
        return coords;
    }
    
    /**
     * Whether or not the point (x, y) lies within the display element
     * 
     * @param x
     * @param y
     */
    @Override
    public boolean contains(int x, int y) {
    	// the zoom cannot be known at this point, so the clicked (x, y) point must
    	// be transformed before this method call
    	x -= baseLeft();
    	y -= baseTop();
        
    	return image_data.getGreenAt(x, y) == 255;
    }
}
