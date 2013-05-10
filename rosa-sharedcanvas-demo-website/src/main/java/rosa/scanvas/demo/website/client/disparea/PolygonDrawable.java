package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

public class PolygonDrawable extends DisplayElement {
    private final Html5DisplayAreaView view;
    private final int[][] coords;

    public PolygonDrawable(String id, int x, int y, int width, int height,
            Html5DisplayAreaView view, int[][] coords) {
        super(id, x, y, width, height);

        this.view = view;
        this.coords = coords;
    }

    // TODO Override contains. Implement by drawing on another canvas and
    // testing pixel color

    @Override
    public void draw() {
        Context2d context = view.context();
        DisplayArea area = view.area();
        
        double zoom = area.zoom();

        context.save();
        context.translate(-area.viewportLeft(), -area.viewportTop());
        context.scale(zoom, zoom);

        context.beginPath();
        context.moveTo(coords[0][0], coords[0][1]);

        for (int i = 1; i < coords.length; i++) {
            context.lineTo(coords[i][0], coords[i][1]);
        }

        context.stroke();
        context.closePath();

        context.restore();
    }

}
