package rosa.scanvas.model.client;

import rosa.scanvas.model.client.impl.ManifestImpl;
import rosa.scanvas.model.client.impl.ManifestCollectionImpl;
import rosa.scanvas.model.client.jsonld.JsonLd;
import rosa.scanvas.model.client.jsonld.JsonLdError;
import rosa.scanvas.model.client.rdf.RdfDataset;
import rosa.scanvas.model.client.rdf.RdfGraph;
import rosa.scanvas.model.client.rdf.impl.RdfDatasetJson;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SharedCanvas {
    /**
     * Loading a Shared Canvas entity from a JSON-LD endpoint.
     * 
     * @param url
     * @param type
     * @param cb
     */
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
            public void onSuccess(JavaScriptObject json) {
                JsonLd.toRdf(json, new JsonLd.Callback() {
                    public void finished(JsArray<JavaScriptObject> result) {
                        if (result.length() != 2) {
                            cb.onFailure(new IllegalStateException(
                                    "Expected result size to be 2"));
                            return;
                        }

                        JsonLdError error = result.get(0).cast();
                        JavaScriptObject rdf_json = result.get(1);

                        if (error != null) {
                            cb.onFailure(new IllegalStateException(
                                    "Error converting to rdf: "
                                            + error.message()));
                            return;
                        }

                        RdfDataset ds = new RdfDatasetJson(new JSONObject(
                                rdf_json));

                        // Assume everything in default graph
                        RdfGraph graph = ds.defaultGraph();

                        if (type == ManifestCollection.class) {
                            cb.onSuccess((T) new ManifestCollectionImpl(graph));
                        } else if (type == Manifest.class) {
                            cb.onSuccess((T) new ManifestImpl(graph));
                        } else {
                            cb.onFailure(new IllegalArgumentException(
                                    "Unsupported type: " + type.getName()));
                        }
                    }
                });
            }
        });
    }
}
