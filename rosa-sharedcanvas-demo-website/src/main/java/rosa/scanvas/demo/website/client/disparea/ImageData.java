package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.core.client.JavaScriptObject;

public class ImageData extends JavaScriptObject {
    public interface OnLoadCallback {
        void onload();
    }
    
    public native static ImageData create() /*-{ 
      new $wnd.Image(); 
    }-*/; 
    
    protected ImageData() {
    }

    /** 
     * Not loaded until load called.
     * 
     * @param url
     */
    public native void setSource(String url) /*-{ 
      this.save_src = url; 
    }-*/; 
    
    public native void load(OnLoadCallback cb);/*-{ 
      this.src = this.save_src;
      
      this.onload = function() {        
        cb.rosa.scanvas.demo.website.client.disparea@::onLoad();
      };
      
    }-*/; 
}
