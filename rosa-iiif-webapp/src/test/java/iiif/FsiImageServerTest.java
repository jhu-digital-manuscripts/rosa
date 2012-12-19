package iiif;

import junit.framework.TestCase;

public class FsiImageServerTest extends TestCase {
    private FSIServer server = new FSIServer(
            "http://fsiserver.library.jhu.edu/server");

    public void testLookupImage() throws Exception {
        ImageInfo info = server.lookupImage("rose/Douce195/Douce195.001r.tif");

        assertNotNull(info);
        assertTrue(info.getWidth() > 0);
        assertTrue(info.getHeight() > 0);

        System.out.println(info.getWidth() + " " + info.getHeight());

        info = server.lookupImage("moo/moo.tif");

        assertNull(info);
    }

    public void testConstructURL() throws Exception {
        IIIFImageRequest req = new IIIFImageRequest();

        Region region = new Region();
        Size scale = new Size();

        req.setSize(scale);
        req.setRegion(region);
        req.setImage("rose/Douce195/Douce195.001r.tif");
        req.setFormat(ImageFormat.PNG);
        req.setQuality(Quality.NATIVE);
        scale.setType(Size.Type.FULL);
        region.setType(Region.Type.FULL);

        String url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

        req.setFormat(ImageFormat.JPG);
        req.setQuality(Quality.COLOR);

        scale.setType(Size.Type.EXACT);
        scale.setWidth(300);
        scale.setHeight(300);

        region.setType(Region.Type.ABSOLUTE);
        region.setX(100);
        region.setY(100);
        region.setWidth(600);
        region.setHeight(600);

        url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

        scale.setType(Size.Type.PERCENTAGE);
        scale.setPercentage(0.5);

        region.setType(Region.Type.ABSOLUTE);
        region.setX(100);
        region.setY(100);
        region.setWidth(600);
        region.setHeight(600);

        url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

        scale.setType(Size.Type.EXACT_WIDTH);
        scale.setWidth(200);

        region.setType(Region.Type.FULL);

        url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

        scale.setType(Size.Type.EXACT_HEIGHT);
        scale.setHeight(300);

        region.setType(Region.Type.FULL);

        url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

        scale.setType(Size.Type.BEST_FIT);
        scale.setWidth(200);
        scale.setHeight(200);

        region.setType(Region.Type.ABSOLUTE);

        url = server.constructURL(req);
        // new URL(url).openStream().close();
        System.out.println(url);

    }
}
