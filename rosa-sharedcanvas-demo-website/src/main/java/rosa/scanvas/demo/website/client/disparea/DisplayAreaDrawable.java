package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

public interface DisplayAreaDrawable {
    /**
     * Draw or schedule a draw of a DisplayElement located within the display area
     * given the context representing the viewport. Pending draws from a
     * previous call must be cancelled. The display area may change between
     * calls.
     * 
     * @param context
     * @param area
     */
    void draw(Context2d viewport_context, DisplayArea area, OnDrawnCallback cb);
    
    public interface OnDrawnCallback {
    	void onDrawn();
    }
}
