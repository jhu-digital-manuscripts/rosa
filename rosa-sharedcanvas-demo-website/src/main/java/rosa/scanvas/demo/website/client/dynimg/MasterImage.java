package rosa.scanvas.demo.website.client.dynimg;

/**
 * Image available on an image server.
 */
public class MasterImage {
    private final String id;
    private final int width;
    private final int height;

    public MasterImage(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public String id() {
        return id;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
