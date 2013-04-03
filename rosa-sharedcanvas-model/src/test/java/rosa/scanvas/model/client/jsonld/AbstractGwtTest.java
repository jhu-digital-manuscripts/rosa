package rosa.scanvas.model.client.jsonld;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.SharedCanvas;
import rosa.scanvas.model.client.SharedCanvasConstants;
import rosa.scanvas.model.client.jsonld.JsonLd.Callback;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractGwtTest extends GWTTestCase implements
        SharedCanvasConstants {
    public String getModuleName() {
        return "rosa.scanvas.model.SharedCanvasModel";
    }

    /**
     * Invoke a callback on the result of converting json to RDF. The callback
     * is setup to be run outside of the javascript callback context so
     * assertions can be used.
     * 
     * @param json
     * @param cb
     */

    public void checkRdf(String json, final JsonLd.Callback cb) {
        final List<JsArray<JavaScriptObject>> container = new ArrayList<JsArray<JavaScriptObject>>();

        JSONValue json_val = JSONParser.parseLenient(json);

        JsonLd.toRdf(json_val.isObject().getJavaScriptObject(), new Callback() {
            @Override
            public void finished(JsArray<JavaScriptObject> result) {
                container.add(result);
            }
        });

        delayTestFinish(5 * 60 * 1000);

        Timer timer = new Timer() {
            public void run() {
                if (container.size() != 1) {
                    cancel();
                    throw new IllegalStateException(
                            "Failed to parse json in time");
                }

                if (!container.isEmpty()) {
                    cb.finished(container.get(0));
                    cancel();
                    finishTest();
                }
            }
        };

        timer.scheduleRepeating(1000);
    }

    /**
     * Load a remote shared canvas object and invoke the given callback outside
     * of the context of a JavaScript callback so asserts can be used.
     * 
     * @param url
     * @param type
     * @param cb
     */
    public <T> void checkRemoteSharedCanvas(final String url, Class<T> type,
            final AsyncCallback<T> cb) {
        final List<T> success_container = new ArrayList<T>();
        final List<Throwable> failure_container = new ArrayList<Throwable>();

        SharedCanvas.load(url, type, new AsyncCallback<T>() {
            public void onFailure(Throwable error) {
                System.out.println("Failed to load: " + url + "; " + error);
                failure_container.add(error);
            }

            public void onSuccess(T result) {
                System.out.println("Loaded: " + url);
                success_container.add(result);
            }
        });

        delayTestFinish(5 * 60 * 1000);

        final Timer timer = new Timer() {
            public void run() {
                if (!success_container.isEmpty()) {
                    cb.onSuccess(success_container.get(0));
                    cancel();
                    finishTest();
                } else if (!failure_container.isEmpty()) {
                    cb.onFailure(failure_container.get(0));
                    cancel();
                    finishTest();
                }
            }
        };

        timer.scheduleRepeating(1000);
    }
}
