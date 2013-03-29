package rosa.scanvas.model.client.jsonld;

import com.google.gwt.core.client.JavaScriptObject;

public class JsonLdError extends JavaScriptObject {
    protected JsonLdError() {
    }

    public final native String name() /*-{
      return this.name;
    }-*/;
    
    public final native String message() /*-{
      return this.message;
    }-*/;
   
    public final native String details() /*-{
      return this.details;
    }-*/;
}
