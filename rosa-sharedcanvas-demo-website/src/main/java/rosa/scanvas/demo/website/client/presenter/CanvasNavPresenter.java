package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.HistoryInfo;
import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelProperties;
import rosa.scanvas.demo.website.client.event.GetDataEvent;
import rosa.scanvas.demo.website.client.widgets.PageTurnerWidget;
import rosa.scanvas.demo.website.client.widgets.ThumbnailWidget;

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
		display.getTabPanelSelector().addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> event) {
				doSelection(event.getSelectedItem());
			}
		});
	}
	
	private void bindThumbnailTable(final PanelData data) {
		display.getThumbnailWidget().getThumbTable().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = display.getThumbnailWidget().getSelectedRow(event);
				int col = display.getThumbnailWidget().getSelectedColumn(event);
				
				if (row >= 0 && col >= 0) {
					eventBus.fireEvent(new GetDataEvent(HistoryInfo.newToken(
							panelId, "canvas", "0",
							data.getCollection().uri(), 
							data.getManifest().uri(),
							data.getSequence().uri(),
							String.valueOf(row*4 + col)
							/*data.getSequence().canvas(row*4 + col).uri()*/)));
				}
				
			}
		});
	}
	
	private void doSelection(int selection) {
	// TODO change to fire an event on the eventBus, instead of directly accessing the History
		History.newItem(HistoryInfo.setAttribute(panelId, 2, String.valueOf(selection)));
	}
	
	public void setSelectedTab(int index) {
		display.setSelectedTab(index);
	}

	public void setData(PanelData data) {
		bindThumbnailTable(data);
		
		display.getThumbnailWidget().setData(data.getSequence());
	}
}
