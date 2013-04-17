package rosa.scanvas.demo.website.client.dynimg;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * A widget for displaying an image of given dimensions. The image isn't
 * viewable until makeViewable is called at which point it is loaded from the
 * given url.
 */
public class WebImage extends FocusWidget {
    private final String url;
    private final int width;
    private final int height;
    private final ImageElement img;
    private boolean viewable;

    public interface OnLoadCallback {
        void onLoad();
    }

    public WebImage(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.img = Document.get().createImageElement();
        this.viewable = false;

        setElement(img);
        setWidth(width + "px");
        setHeight(height + "px");

        setStylePrimaryName("WebImage");
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public String url() {
        return url;
    }

    public void makeViewable() {
        if (!viewable) {
            img.setSrc(url);
            viewable = true;
        }
    }
    
    public void makeViewable(OnLoadCallback cb) {
        if (!viewable) {
            setOnloadCallback(cb);
            
            img.setSrc(url);
            viewable = true;                        
        }
    }

    public ImageElement getImageElement() {
        return img;
    }

    /**
     * Callback when the image data is loaded.
     * 
     * @param cb
     */
    private native void setOnloadCallback(OnLoadCallback cb) /*-{
      this.img.onload = function() {        
        cb.@rosa.scanvas.demo.website.client.dynimg.WebImage.OnLoadCallback::onLoad();
      };
    }-*/;

    public boolean isViewable() {
        return viewable;
    }
}
