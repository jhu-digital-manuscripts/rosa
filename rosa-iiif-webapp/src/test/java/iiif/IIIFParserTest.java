package iiif;

import junit.framework.TestCase;

public class IIIFParserTest extends TestCase {
    private IIIFParser parser = new IIIFParser("/image-service");

    public void testDetermineType() {
        assertEquals(
                IIIFRequestType.INFO,
                parser.determineRequestType("/image-service/abcd1234/info.json"));
        assertEquals(IIIFRequestType.INFO,
                parser.determineRequestType("/image-service/abcd1234/info.xml"));

        assertEquals(
                IIIFRequestType.IMAGE,
                parser.determineRequestType("/image-service/abcd1234/full/full/0/native.jpg"));
    }

    public void testParseInfoRequest() throws Exception {
        IIIFInfoRequest info, test;

        info = new IIIFInfoRequest();
        info.setImage("abcd1234");
        info.setFormat(InfoFormat.XML);

        test = parser.parseImageInfoRequest("/image-service/abcd1234/info.xml");
        assertEquals(info, test);

        info.setImage("abc/123");
        info.setFormat(InfoFormat.JSON);

        test = parser
                .parseImageInfoRequest("/image-service/abc%2F123/info.json");
        assertEquals(info, test);
    }

    public void testParseImageRequest() throws Exception {
        IIIFImageRequest img, test;

        img = new IIIFImageRequest();
        img.setImage("moo");
        img.setFormat(ImageFormat.PNG);
        img.setQuality(Quality.NATIVE);
        img.setRegion(new Region(Region.Type.FULL));
        img.setSize(new Size(Size.Type.FULL));

        test = parser
                .parseImageRequest("/image-service/moo/full/full/0/native.png");
        assertEquals(img, test);

        img = new IIIFImageRequest();
        img.setImage("moo");
        img.setFormat(null);
        img.setQuality(Quality.COLOR);
        img.setRotation(90.0);
        img.setRegion(new Region(Region.Type.FULL));
        img.setSize(new Size(Size.Type.FULL));

        test = parser
                .parseImageRequest("/image-service/moo/full/full/90.0/color");
        assertEquals(img, test);

    }

    public void testParseScale() throws IIIFException {
        IIIFImageRequest img, test;
        Size scale;

        scale = new Size(Size.Type.PERCENTAGE);
        img = new IIIFImageRequest();
        img.setImage("moo");
        img.setFormat(null);
        img.setQuality(Quality.COLOR);
        img.setRotation(90.0);
        img.setRegion(new Region(Region.Type.FULL));

        scale.setPercentage(50.0);
        img.setSize(scale);

        test = parser
                .parseImageRequest("/image-service/moo/full/pct:50.0/90.0/color");
        assertEquals(img, test);

        scale = new Size(Size.Type.EXACT_WIDTH);
        scale.setWidth(100);
        img.setSize(scale);

        test = parser
                .parseImageRequest("/image-service/moo/full/100,/90.0/color");
        assertEquals(img, test);

        scale = new Size(Size.Type.EXACT_HEIGHT);
        scale.setHeight(100);
        img.setSize(scale);

        test = parser
                .parseImageRequest("/image-service/moo/full/,100/90.0/color");
        assertEquals(img, test);

        scale = new Size(Size.Type.EXACT);
        scale.setWidth(100);
        scale.setHeight(200);
        img.setSize(scale);

        test = parser
                .parseImageRequest("/image-service/moo/full/100,200/90.0/color");
        assertEquals(img, test);

        scale = new Size(Size.Type.BEST_FIT);
        scale.setWidth(100);
        scale.setHeight(200);
        img.setSize(scale);

        test = parser
                .parseImageRequest("/image-service/moo/full/!100,200/90.0/color");
        assertEquals(img, test);
    }

    public void testParseRegion() throws IIIFException {
        IIIFImageRequest img, test;
        Region region;

        img = new IIIFImageRequest();
        img.setImage("moo");
        img.setFormat(null);
        img.setQuality(Quality.GREY);
        img.setRotation(0);
        img.setSize(new Size(Size.Type.FULL));

        region = new Region(Region.Type.FULL);
        img.setRegion(region);

        test = parser.parseImageRequest("/image-service/moo/full/full/0/grey");
        assertEquals(img, test);

        region = new Region(Region.Type.ABSOLUTE);
        region.setX(10);
        region.setY(20);
        region.setWidth(100);
        region.setHeight(200);
        img.setRegion(region);

        test = parser
                .parseImageRequest("/image-service/moo/10,20,100,200/full/0/grey");
        assertEquals(img, test);

        region = new Region(Region.Type.PERCENTAGE);
        region.setPercentageX(10.0);
        region.setPercentageY(20.0);
        region.setPercentageWidth(50.0);
        region.setPercentageHeight(60.0);
        img.setRegion(region);

        test = parser
                .parseImageRequest("/image-service/moo/pct:10.0,20.0,50.0,60.0/full/0/grey");
        assertEquals(img, test);
    }
}
