package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Composite;

public class DisplayAreaWidget extends Composite {
    private final Canvas canvas;
    private final Context2d context;
    private DisplayArea area;

    public DisplayAreaWidget(DisplayArea area) {
        this.canvas = Canvas.createIfSupported();
        this.context = canvas.getContext2d();

        initWidget(canvas);
    }

    public void display(DisplayArea area) {
        this.area = area;

        canvas.setPixelSize(area.viewportWidth(), area.viewportHeight());
        canvas.setCoordinateSpaceWidth(area.baseWidth());
        canvas.setCoordinateSpaceHeight(area.baseHeight());

        redraw();
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

    public DisplayArea area() {
        return area;
    }
}
