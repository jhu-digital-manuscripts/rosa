package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.presenter.CanvasNavPresenter;
import rosa.scanvas.demo.website.client.widgets.PageTurnerWidget;
import rosa.scanvas.demo.website.client.widgets.ThumbnailWidget;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class CanvasNavView extends Composite implements CanvasNavPresenter.Display {

	private ThumbnailWidget thumbnailWidget = new ThumbnailWidget();
	private PageTurnerWidget pageTurnerWidget = new PageTurnerWidget();
	
	//private AbsolutePanel mainPanel = new AbsolutePanel();
	private FlowPanel mainPanel = new FlowPanel();
	private TabLayoutPanel tabPanel = new TabLayoutPanel(50, Unit.PX);
	
	public CanvasNavView() {
		initWidget(mainPanel);
		mainPanel.add(tabPanel);
		
		/*mainPanel.setHeight("30em");
		mainPanel.setWidth("30em");*/
		tabPanel.setHeight("100%");
		tabPanel.setWidth("100%");
		
		tabPanel.add(thumbnailWidget, "Thumbnail Browser");
		tabPanel.add(pageTurnerWidget, "Page Turner" );
		
		tabPanel.selectTab(0);
	}
	
	public void setSize(String width, String height) {
		mainPanel.setWidth(width);
		mainPanel.setHeight(height);
	}
	
	public void setData(/*Sequence data, List<Annotation> images*/PanelData data) {
		thumbnailWidget.setData(data/*, images*/);
	}
	
	public PageTurnerWidget getPageTurnerWidget() { return pageTurnerWidget; }
	public ThumbnailWidget getThumbnailWidget() { return thumbnailWidget; }

	public void setSelectedTab(int index) {
		tabPanel.selectTab(index);
	}

	public HasSelectionHandlers<Integer> getTabPanelSelector() {
		return tabPanel;
	}

}
