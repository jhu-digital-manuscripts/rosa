package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.dom.client.Context2d;

/**
 * A line of text with a drawn polygon bounding box
 */
public class TextDrawable implements DisplayAreaDrawable {
    private final TextDisplayElement el;

    public TextDrawable(TextDisplayElement el) {
        this.el = el;
    }

    @Override
    public void draw(Context2d context, DisplayArea area) {
        double zoom = area.zoom();

        context.save();

        context.translate(-area.viewportLeft(), -area.viewportTop());
        context.scale(zoom, zoom);

        // outline

        int[][] coords = el.coordinates();

        context.beginPath();
        context.moveTo(coords[0][0], coords[0][1]);

        for (int i = 1; i < coords.length; i++) {
            context.lineTo(coords[i][0], coords[i][1]);
        }

        context.setLineWidth(6);
        context.stroke();
        context.closePath();

        // text

        context.setFont("bold 60px sans-serif");
        context.setTextBaseline("top");
        context.fillText(el.text(), el.baseLeft(), el.baseTop(), el.baseWidth());

        context.restore();
    }

}
