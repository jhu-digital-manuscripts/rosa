package rosa.scanvas.demo.website.client.disparea;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;

public class StaticImageDisplayElement extends DisplayElement {
	private final String uri;

    public StaticImageDisplayElement(String id, String uri, int x, int y, int width, int height) {
        super(id, x, y, width, height);
        this.uri = uri;
    }
    
    public String uri() {
    	return uri;
    }
}
