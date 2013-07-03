package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Multiple lines of text with a drawn polygon bounding box
 */
public class MultiLineTextDrawable implements DisplayAreaDrawable {
    private final TextDisplayElement el;
    
    private final PopupPanel popup;
    
    private boolean popup_ready;

    public MultiLineTextDrawable(MultiLineTextDisplayElement el) {
    	this.el = el;
    	
    	popup = new PopupPanel(true, false);
    	
    	popup_ready = false;
    }

    @Override
    public void draw(Context2d context, DisplayArea area, OnDrawnCallback cb) {
    	double zoom = area.zoom();
    	context.save();
    	
    	context.translate(-area.viewportLeft(), -area.viewportTop());
    	context.scale(zoom, zoom);
    	
    	// Draw outline
    	int[][] coords = el.coordinates();
    	
    	context.beginPath();
    	context.moveTo(coords[0][0], coords[0][1]);
    	
    	for (int i = 1; i < coords.length; i++) {
    		context.lineTo(coords[i][0], coords[i][1]);
    	}
    	
    	context.setLineWidth(6);
    	context.stroke();
    	
    	context.setGlobalAlpha(0.3);
    	context.setFillStyle("white");
    	context.fill();
    	context.setGlobalAlpha(1.0);
    	
    	context.closePath();
    	
    	// Write text
    	context.setFillStyle("black");
    	context.setFont("bold 60px sans-serif");
    	context.setTextBaseline("top");
    	context.fillText(el.firstLine(), el.baseLeft(), el.baseTop(), el.baseWidth());
    	
    	context.restore();
    	
    	popup_ready = true;
    	cb.onDrawn();
    }
    
    

}
