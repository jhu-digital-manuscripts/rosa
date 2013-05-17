package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

public class PolygonDrawable implements DisplayAreaDrawable {
    private final int[][] coords;

    public PolygonDrawable(PolygonDisplayElement polygon) {
        this.coords = polygon.coordinates();
    }

    @Override
    public void draw(Context2d context, DisplayArea area) {
        // DisplayArea area = view.area();

        double zoom = area.zoom();

        context.save();

        context.translate(-area.viewportLeft(), -area.viewportTop());
        context.scale(zoom, zoom);

        context.beginPath();
        context.moveTo(coords[0][0], coords[0][1]);

        for (int i = 1; i < coords.length; i++) {
            context.lineTo(coords[i][0], coords[i][1]);
        }

        context.setLineWidth(6);
        context.stroke();
        
        context.setGlobalAlpha(0.3);
        context.setFillStyle("white");
        context.fill();
        context.setGlobalAlpha(1.0);
        
        context.closePath();

        context.restore();
    }
}
