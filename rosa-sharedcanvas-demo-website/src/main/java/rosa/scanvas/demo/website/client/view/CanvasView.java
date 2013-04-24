package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaWidget;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class CanvasView extends Composite implements CanvasPanelPresenter.Display {
    private final Panel main;
    private final Label title;
    private final DisplayAreaWidget area_view;

    public CanvasView(int width, int height, int vp_width, int vp_height) {
        this.main = new FlowPanel();
        this.title = new Label();

        DisplayArea area = new DisplayArea(width, height, vp_width, vp_height);
        this.area_view = new DisplayAreaWidget(area);

        main.add(title);
        main.add(area_view);

        main.setStylePrimaryName("PanelView");
        
        initWidget(main);
    }

    @Override
    public Label getLabel() {
        return title;
    }

    @Override
    public DisplayAreaWidget getDisplayAreaWidget() {
        return area_view;
    }
}
