package rosa.scanvas.demo.website.client.disparea;

public class TextDisplayElement extends DisplayElement {
    private final String text;
    private final int[][] coords;

    public TextDisplayElement(String id, int x, int y, int width, int height,
            String text, int[][] coords) {
        super(id, x, y, width, height);
        this.coords = coords;
        this.text = text;
    }

    public String text() {
        return text;
    }

    public int[][] coordinates() {
        return coords;
    }
}
