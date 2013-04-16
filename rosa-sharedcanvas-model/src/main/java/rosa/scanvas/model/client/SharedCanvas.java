package rosa.scanvas.model.client;

import java.util.HashMap;
import java.util.Map;

import rosa.scanvas.model.client.impl.AnnotationListImpl;
import rosa.scanvas.model.client.impl.ManifestImpl;
import rosa.scanvas.model.client.impl.ManifestCollectionImpl;
import rosa.scanvas.model.client.impl.SequenceImpl;
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
    private static int MAX_CACHE_SIZE = 100;

    private static final Map<String, Object> cache = new HashMap<String, Object>();

    /**
     * Loading a Shared Canvas entity from a JSON-LD endpoint.
     * 
     * @param url
     * @param type
     * @param cb
     */
    public static <T> void load(Reference<T> ref, final AsyncCallback<T> cb) {
        load(ref.uri(), ref.type(), cb);
    }

    /**
     * Loading a Shared Canvas entity from a JSON-LD endpoint. Load results are
     * cached in memory up until a certain number.
     * 
     * @param url
     * @param type
     * @param cb
     */

    // GWT doesn't support type.cast()
    @SuppressWarnings("unchecked")
    public static <T> void load(final String url, final Class<T> type,
            final AsyncCallback<T> cb) {

        if (cache.containsKey(url)) {
            cb.onSuccess((T) cache.get(url));
            return;
        }

        JsonpRequestBuilder rb = new JsonpRequestBuilder();

        rb.requestObject(url, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable err) {
                cb.onFailure(err);
            }

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

                        Object obj = null;

                        if (type == ManifestCollection.class) {
                            obj = new ManifestCollectionImpl(graph);
                        } else if (type == Manifest.class) {
                            obj = new ManifestImpl(graph);
                        } else if (type == AnnotationList.class) {
                            obj = new AnnotationListImpl(graph);
                        } else if (type == Sequence.class) {
                            obj = new SequenceImpl(graph);
                        }

                        if (obj == null) {
                            cb.onFailure(new IllegalArgumentException(
                                    "Unsupported type: " + type.getName()));
                        } else {
                            cb.onSuccess((T) obj);

                            if (cache.size() > MAX_CACHE_SIZE) {
                                cache.clear();
                            }

                            cache.put(url, obj);
                        }
                    }
                });
            }
        });
    }
}
