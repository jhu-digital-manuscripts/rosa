package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A line of text with a drawn polygon bounding box
 */
public class TextDrawable extends PolygonDrawable {
    private final Html5DisplayAreaView view;
    private final int[][] coords;
    private final String text;

    public TextDrawable(String id, int x, int y, int width, int height,
            Html5DisplayAreaView view, int[][] coords, String text) {
        super(id, x, y, width, height, view, coords);

        this.view = view;
        this.text = text;
        this.coords = coords;
    }

    // TODO Override contains. Implement by drawing on another canvas and
    // testing pixel color

    @Override
    public void draw() {
    	super.draw();
    	
        Context2d context = view.context();
        DisplayArea area = view.area();
        
        double zoom = area.zoom();
        
        context.save();
        context.translate(-area.viewportLeft(), -area.viewportTop());
        context.scale(zoom, zoom);
        
        context.setFont("bold 60px sans-serif");
        context.setTextBaseline("top");
        context.fillText(text, baseLeft(), baseTop(), baseWidth());

        context.restore();
    }

}
