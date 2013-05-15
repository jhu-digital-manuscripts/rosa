package rosa.scanvas.demo.website.client.disparea;

public class PolygonDisplayElement extends DisplayElement {
    private final int[][] coords;

    public PolygonDisplayElement(String id, int x, int y, int width,
            int height, int[][] coords) {
        super(id, x, y, width, height);
        this.coords = coords;
    }

    public int[][] coordinates() {
        return coords;
    }
}
