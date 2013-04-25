package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.presenter.SequencePanelPresenter;
import rosa.scanvas.demo.website.client.widgets.PageTurnerWidget;
import rosa.scanvas.demo.website.client.widgets.ThumbnailBrowser;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class SequenceView extends Composite implements
        SequencePanelPresenter.Display {

    private final TabLayoutPanel tab_panel;
    private final ThumbnailBrowser thumb_browser;
    private final PageTurnerWidget page_turner;

    public SequenceView() {
        FlowPanel main = new FlowPanel();
        main.setStylePrimaryName("PanelView");

        this.thumb_browser = new ThumbnailBrowser();
        this.page_turner = new PageTurnerWidget();
        this.tab_panel = new TabLayoutPanel(50, Unit.PX);

        tab_panel.setHeight("100%");
        tab_panel.setWidth("100%");

        tab_panel.add(thumb_browser, "Thumbnail Browser");
        tab_panel.add(page_turner, "Page Turner");

        tab_panel.selectTab(0);

        main.add(tab_panel);

        initWidget(main);
    }

    public PageTurnerWidget getPageTurnerWidget() {
        return page_turner;
    }

    public ThumbnailBrowser getThumbnailWidget() {
        return thumb_browser;
    }

    public void setSelectedTab(int index) {
        tab_panel.selectTab(index);
    }

    public HasSelectionHandlers<Integer> getTabPanelSelector() {
        return tab_panel;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);
    }

    @Override
    public ThumbnailBrowser getThumbnailBrowser() {
        return thumb_browser;
    }
}
