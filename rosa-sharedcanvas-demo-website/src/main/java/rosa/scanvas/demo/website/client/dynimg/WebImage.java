package rosa.scanvas.demo.website.client.dynimg;

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
    private OnLoadCallback callback;

    public interface OnLoadCallback {
        void onLoad();
    }

    public WebImage(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.img = createImageElement();
        this.viewable = false;

        setElement(img);
        setWidth(width + "px");
        setHeight(height + "px");

        setStylePrimaryName("WebImage");
    }

    public WebImage(String url) {
        this(url, -1, -1);
    }

    // Have to use a javascript Image object to get set onload callback.
    private native ImageElement createImageElement() /*-{
      var self = this;
      var img = new Image();
                                                     
      img.onload = function() {
        self.@rosa.scanvas.demo.website.client.dynimg.WebImage::invokeCallback()();
        img.onload = null;
      }
                                              
      return img;
    }-*/;

    private void invokeCallback() {
        if (callback != null) {
            callback.onLoad();
        }
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
       makeViewable(null);
    }

    public void makeViewable(OnLoadCallback cb) {
        if (!viewable) {
            callback = cb;
            viewable = true;
            img.setSrc(url);
        }
    }

    public ImageElement getImageElement() {
        return img;
    }

    public boolean isViewable() {
        return viewable;
    }
}
