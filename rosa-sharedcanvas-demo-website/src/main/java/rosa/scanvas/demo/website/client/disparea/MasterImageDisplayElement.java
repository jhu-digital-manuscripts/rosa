package rosa.scanvas.demo.website.client.disparea;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;

public class MasterImageDisplayElement extends DisplayElement {
    private final ImageServer server;
    private final MasterImage master;

    public MasterImageDisplayElement(String id, int x, int y, ImageServer server, MasterImage master) {
        super(id, x, y, master.width(), master.height());
        this.server = server;
        this.master = master;
    }

    public MasterImage masterImage() {
        return master;
    }

    public ImageServer imageServer() {
        return server;
    }
}
