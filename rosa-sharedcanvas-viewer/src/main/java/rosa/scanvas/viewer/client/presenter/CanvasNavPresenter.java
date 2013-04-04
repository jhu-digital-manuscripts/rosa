package rosa.scanvas.viewer.client.presenter;

import rosa.scanvas.viewer.client.HistoryInfo;
import rosa.scanvas.viewer.client.PanelProperties;
import rosa.scanvas.viewer.client.event.GetDataEvent;
import rosa.scanvas.viewer.client.widgets.PageTurnerWidget;
import rosa.scanvas.viewer.client.widgets.ThumbnailWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class CanvasNavPresenter implements Presenter {

	public interface Display {
		PageTurnerWidget getPageTurnerWidget();
		ThumbnailWidget getThumbnailWidget();
		void setSelectedTab(int index);
		HasSelectionHandlers<Integer> getTabPanelSelector();
		Widget asWidget();
	}
	
	private final HandlerManager eventBus;
	private final Display display;
	private final int row;
	private final int col;
	private final String panelId;
	
	public CanvasNavPresenter(Display display, HandlerManager eventBus, PanelProperties props) {
		this.eventBus = eventBus;
		this.display = display;
		
		panelId = props.getId();
		row = props.getRow();
		col = props.getCol();
	}
	
	public void go(HasWidgets container) {
		bind();
		
		if (container instanceof FlexTable) {
			((FlexTable) container).setWidget(row, col, display.asWidget());
		}
	}

	private void bind() {
		display.getThumbnailWidget().getThumbTable().addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						eventBus.fireEvent(new GetDataEvent("canvas"+"."+panelId));
					}
				});
		
		display.getTabPanelSelector().addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				doSelection(event.getSelectedItem());
			}
		});
	}
	
	private void doSelection(int selection) {
		History.newItem(HistoryInfo.setTab(panelId, String.valueOf(selection)));
	}
	
	public void setSelectedTab(int index) {
		display.setSelectedTab(index);
	}
}
