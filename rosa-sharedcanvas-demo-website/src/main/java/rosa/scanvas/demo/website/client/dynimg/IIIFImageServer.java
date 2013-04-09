package rosa.scanvas.demo.website.client.dynimg;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * For IIIF master images the id is the endpoint plus the IIIF image id.
 * 
 * TODO Probably should redo framework so each MasterImage has a reference to
 * image server.
 */
public class IIIFImageServer extends AbstractImageServer {

    public static void loadMasterImage(final String id,
            final AsyncCallback<MasterImage> cb) {
        final String url = id + "/info.json";

        JsonpRequestBuilder rb = new JsonpRequestBuilder();

        rb.requestObject(url, new AsyncCallback<JavaScriptObject>() {
            @Override
            public void onFailure(Throwable err) {
                cb.onFailure(err);
            }

            @Override
            public void onSuccess(JavaScriptObject json) {
                JSONObject o = new JSONObject(json);

                int width = get(o, "width");
                int height = get(o, "height");

                if (width == -1 || height == -1) {
                    cb.onFailure(new Throwable(
                            "Failed to parse info request result: " + url));
                } else {
                    cb.onSuccess(new MasterImage(id, width, height));
                }
            }

            private int get(JSONObject o, String key) {
                JSONValue val = o.get(key);

                if (val == null) {
                    return -1;
                }

                JSONNumber num = val.isNumber();

                if (num == null) {
                    return -1;
                }

                return (int) num.doubleValue();
            }
        });
    }

    /**
     * Return the identifier from a valid IIIF request.
     * 
     * @param iiif_url
     *            null on failure
     */
    public static String parseIdentifier(String iiif_url) {
        String[] parts = iiif_url.split("/");

        if (parts.length < 5) {
            // Cannot be an info request
            return null;
        }

        String last = parts[parts.length - 1];

        int prefix;

        if (last.equals("info.xml") || last.equals("info.json")) {
            // info request
            prefix = parts.length - 1;
        } else if (parts.length < 8) {
            // Cannot be an image request
            return null;
        } else {
            // Must be an image request
            prefix = parts.length - 4;
        }

        String result = parts[0] + "/";

        for (int i = 2; i < prefix; i++) {
            result += "/" + parts[i];
        }

        return result;
    }

    public IIIFImageServer() {

    }

    @Override
    public String renderAsUrl(MasterImage image, int width, int height,
            int... crop) {
        return renderAsUrl(image.id(), width, height, crop);
    }

    public String renderAsUrl(String iiif_prefix, int width, int height,
            int... crop) {
        String region;

        if (crop.length == 0) {
            region = "full";
        } else {
            region = crop[0] + "," + crop[1] + "," + (crop[2] - crop[0]) + ","
                    + (crop[3] - crop[1]);
        }

        String size = width + "," + height;
        String rotation = "0";
        String quality = "native";
        String format = "jpg";

        return iiif_prefix + "/" + region + "/" + size + "/" + rotation + "/"
                + quality + "." + format;
    }

    @Override
    public int maxRenderSize() {
        return 1000;
    }

    @Override
    public int tileSize() {
        return 500;
    }

}
