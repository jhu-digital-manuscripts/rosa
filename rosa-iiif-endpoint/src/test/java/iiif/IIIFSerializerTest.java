package iiif;

import junit.framework.TestCase;

public class IIIFSerializerTest extends TestCase {

    public void testImageInfoXML() throws Exception {
        ImageInfo info = new ImageInfo();

        info.setId("testid");
        info.setFormats(ImageFormat.PNG);
        info.setWidth(1000);
        info.setHeight(2000);
        info.setQualities(Quality.NATIVE);
        info.setScaleFactors(1);
        info.setTileHeight(100);
        info.setTileWidth(200);
        
        new IIIFSerializer().toXML(info, System.out);
    }
    
    public void testImageInfoJSON() throws Exception {
        ImageInfo info = new ImageInfo();

        info.setId("testid");
        info.setFormats(ImageFormat.PNG);
        info.setWidth(1000);
        info.setHeight(2000);
        info.setQualities(Quality.NATIVE);
        info.setScaleFactors(1);
        info.setTileHeight(100);
        info.setTileWidth(200);
        
        new IIIFSerializer().toJSON(info, System.out);
    }
}
