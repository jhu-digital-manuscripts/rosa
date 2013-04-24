package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaWidget;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.Canvas;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CanvasPanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
        Label getLabel();

        DisplayAreaWidget getDisplayAreaWidget();
    }

    private final Display display;
    private final HandlerManager event_bus;
    private Canvas canvas;
    private int width, height;

    public CanvasPanelPresenter(Display display, HandlerManager eventBus) {
        this.display = display;
        this.event_bus = eventBus;
        this.width = -1;
        this.height = -1;
    }

    // TODO Can save display elements and operate on them for efficiency
    private void setAnnotationVisible(Annotation ann, boolean status) {
        DisplayAreaWidget da = display.getDisplayAreaWidget();
        DisplayElement el = da.area().get(ann.uri());

        if (el != null) {
            el.setVisible(status);
        }

        da.redraw();
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(PanelData data) {
        this.canvas = data.getCanvas();

        update();
    }

    @Override
    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }

        this.width = width;
        this.height = height;

        update();
    }

    private void update() {
        if (canvas == null || width < 0 || height < 0) {
            return;
        }

        DisplayAreaWidget da = display.getDisplayAreaWidget();
        DisplayArea area = new DisplayArea(canvas.width(), canvas.height(),
                width, height);
        da.display(area);
    }
}
