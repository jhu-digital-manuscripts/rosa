package rosa.scanvas.demo.website.client.dynimg;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Some tests depend on IIIF endpoint.
 */
public class GwtTestIIIFImageServer extends GWTTestCase {

    public String getModuleName() {
        return "rosa.scanvas.demo.website.SharedCanvasDemoWebsiteJUnit";
    }

    public void testLoadMasterImage() {
        final String id = "http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif";

        delayTestFinish(10000);

        IIIFImageServer.loadMasterImage(id, new AsyncCallback<MasterImage>() {
            @Override
            public void onSuccess(MasterImage mi) {
                assertEquals(id, mi.id());
                assertEquals(5742, mi.height());
                assertEquals(3732, mi.width());
                finishTest();
            }

            @Override
            public void onFailure(Throwable err) {
                fail(err.getMessage());
                finishTest();
            }
        });
    }

    public void testParseIdentifier() {
        String id;

        id = IIIFImageServer.parseIdentifier("");
        assertNull(id);

        id = IIIFImageServer.parseIdentifier("asd;lk19xcz//asdasdfjlk");
        assertNull(id);

        id = IIIFImageServer.parseIdentifier("http://example.com/blah");
        assertNull(id);

        id = IIIFImageServer
                .parseIdentifier("http://example.com/iiif/moo/info.json");
        assertEquals("http://example.com/iiif/moo", id);

        id = IIIFImageServer
                .parseIdentifier("http://www.example.org/image-service/abcd1234/full/full/0/native.jpg");
        assertEquals("http://www.example.org/image-service/abcd1234", id);

        id = IIIFImageServer
                .parseIdentifier("http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif/full/300,/0/native.jpg");
        assertEquals(
                "http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif",
                id);
    }
}
