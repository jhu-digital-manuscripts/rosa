package rosa.scanvas.model.client;

import rosa.scanvas.model.client.impl.ManifestImpl;
import rosa.scanvas.model.client.rdf.impl.RdfDatasetJson;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SharedCanvas {
    public static <T> void load(String url, final Class<T> type,
            final AsyncCallback<T> cb) {
        JsonpRequestBuilder rb = new JsonpRequestBuilder();

        rb.requestObject(url, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable err) {
                cb.onFailure(err);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(JavaScriptObject js) {
                JSONObject json = new JSONObject(js);

                if (type == Manifest.class) {
                    cb.onSuccess((T) new ManifestImpl(new RdfDatasetJson(json)));
                }
            }
        });
    }
}
