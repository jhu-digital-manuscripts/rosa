package rosa.scanvas.model.client.jsonld;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsonLd {
    public interface Callback {
        void finished(JsArray<JavaScriptObject> result);
    }

    public static native boolean hasProcessor() /*-{ 
      return $wnd.jsonld != null;
    }-*/;

    /**
     * Convert a JavaScript object in JSON-LD format to an RDF JSON format.
     * 
     * @param input
     * @param cb first argument is JsonLdError, second is JavaScript object to pass to RdfDataset constructor
     */
    public static native void toRdf(JavaScriptObject input, Callback cb) /*-{  
      $wnd.jsonld.toRDF(input, null, function(err, dataset) {    
        cb.@rosa.scanvas.model.client.jsonld.JsonLd.Callback::finished(Lcom/google/gwt/core/client/JsArray;)([err, dataset]);
      });
    }-*/;
}
