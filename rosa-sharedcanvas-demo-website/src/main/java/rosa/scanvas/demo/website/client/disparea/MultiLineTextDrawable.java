package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Multiple lines of text with a drawn polygon bounding box
 */
public class MultiLineTextDrawable implements DisplayAreaDrawable {
    private final MultiLineTextDisplayElement el;

    public MultiLineTextDrawable(MultiLineTextDisplayElement el) {
    	this.el = el;
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
    	
    	cb.onDrawn();
    }
    
    

}
