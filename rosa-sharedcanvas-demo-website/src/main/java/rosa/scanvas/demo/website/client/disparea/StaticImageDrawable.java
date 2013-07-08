package rosa.scanvas.demo.website.client.disparea;

import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

public class StaticImageDrawable implements DisplayAreaDrawable {
    private final StaticImageDisplayElement el;
    
    private WebImage cached;
    private DisplayArea last_area;

    public StaticImageDrawable(StaticImageDisplayElement el) {
        this.el = el;
    }
    
    @Override
    public void draw(final Context2d context, final DisplayArea area, final OnDrawnCallback cb) {
        final double zoom = area.zoom();
        int width = (int) (el.baseWidth() * zoom);
        int height = (int) (el.baseHeight() * zoom);
        
        if (cached == null) {
    		cached = new WebImage(el.uri(), width, height);
    	}

        final int vp_left = area.viewportLeft();
        final int vp_top = area.viewportTop();
        final int vp_right = vp_left + area.viewportWidth();
        final int vp_bottom = vp_top + area.viewportHeight();

        final int img_left = (int) (el.baseLeft() * zoom);
        final int img_top = (int) (el.baseTop() * zoom);
        final int img_right = (int) (img_left + el.baseWidth() * zoom);
        final int img_bottom = (int) (img_top + el.baseHeight() * zoom);

        if (img_right < vp_left || img_left > vp_right || img_bottom < vp_top
        		|| img_top > vp_bottom) {
        	// Do not draw anything if image is out of viewport
        	return;
        }
        
        // On load draw the image if it has not been cancelled.
        WebImage.OnLoadCallback onload_cb = new WebImage.OnLoadCallback() {
            @Override
            public void onLoad(WebImage image) {
                ImageElement img = image.getImageElement();

                context.save();
                context.translate(-area.viewportLeft(), -area.viewportTop());
                context.drawImage(img, img_left, img_top,
                		image.width(), image.height());
                context.restore();
                    
            	cb.onDrawn();
            }
        };
        
        if (cached.isViewable()) {
        	// If the image is already set, draw it on the canvas
        	ImageElement img = cached.getImageElement();
        	
        	context.save();
        	context.translate(-area.viewportLeft(), -area.viewportTop());
        	context.drawImage(img, img_left, img_top, width, height);
        	context.restore();
        	
        	cb.onDrawn();
        	return;
        }
        
        cached.makeViewable(onload_cb);
    }
}
