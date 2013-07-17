package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * Multiple lines of text with a drawn polygon bounding box
 */
public class MultiLineTextDrawable implements DisplayAreaDrawable {
    private final MultiLineTextDisplayElement el;
    private final int step;

    public MultiLineTextDrawable(MultiLineTextDisplayElement el) {
    	this.el = el;
    	step = 65;
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
    	
    	if (el.text().contains("<") && el.text().contains(">")) {
    		context.fillText(el.label() + "...", el.baseLeft(), el.baseTop(), el.baseWidth());
    		el.neverShowPopup(false);
    	} else {
    		String[] words = el.text().split(" ");
    		String line = "";
    		
    		int y = el.baseTop();
    		
    		for (int i = 0; i < words.length; i++) {
    			/*if (y + step > el.baseTop() + el.baseHeight()) {
    				context.restore();
    				el.neverShowPopup(false);
    				cb.onDrawn();
    				return;
    			}*/
    			
    			String test_line = line + words[i] + " ";
    			
    			if (context.measureText(test_line).getWidth() > el.baseWidth() && i > 0
    					|| words[i].contains("\n")) {
    				context.fillText(line, el.baseLeft(), y);
    				y += step;
    				line = words[i] + " ";
    				
    				if (y + step > el.baseTop() + el.baseHeight()) {
        				context.restore();
        				el.neverShowPopup(false);
        				cb.onDrawn();
        				return;
        			}
    			} else {
    				line = test_line;
    			}
    		}
    		context.fillText(line, el.baseLeft(), y);
    		el.neverShowPopup(true);
    	}
    	context.restore();
    	
    	cb.onDrawn();
    }
}
