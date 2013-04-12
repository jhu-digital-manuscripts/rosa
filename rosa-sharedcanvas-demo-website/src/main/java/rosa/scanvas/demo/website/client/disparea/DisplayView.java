package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Composite;

public class DisplayView extends Composite {
    private final Canvas canvas;
    private final Context2d context;
    private final DisplayArea area;

    public DisplayView(DisplayArea area) {
        this.canvas = Canvas.createIfSupported();
        this.context = canvas.getContext2d();
        this.area = area;

        canvas.setPixelSize(area.viewportWidth(), area.viewportHeight());
        canvas.setCoordinateSpaceWidth(area.baseWidth());
        canvas.setCoordinateSpaceHeight(area.baseHeight());

        initWidget(canvas);
    }

    public void redraw() {
        context.clearRect(0, 0, area.viewportWidth(), area.viewportBaseWidth());

        for (DisplayElement el : area.findInViewport()) {
            if (el.isVisible()) {
                el.draw();
            }
        }
    }

    protected Context2d context() {
        return context;
    }

    protected DisplayArea area() {
        return area;
    }
}
