package rosa.scanvas.viewer.client.presenter;

import java.util.List;
import java.util.jar.Manifest;

import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.viewer.client.HistoryInfo;
import rosa.scanvas.viewer.client.PanelProperties;
import rosa.scanvas.viewer.client.event.GetDataEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class CollectionPresenter implements Presenter {
	
	public interface Display {
		HasClickHandlers getList();
		HasText getViewLabel();
		void setData(List list, Class type);
		void setLabel(String label);
		int getSelectedRow(ClickEvent event);
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
	private final String type;
	private final int row;
	private final int col;
	private String next;
	
	private PanelProperties props;
	
	public CollectionPresenter(Display display, HandlerManager eventBus, PanelProperties props, String type) {
		row = props.getRow();
		col = props.getCol();
		
		this.type = type;
		this.props = props;
		this.display = display;
		this.eventBus = eventBus;
	}
	
	public void go(HasWidgets container) {
		bind();
//		container.clear();
		if (container instanceof FlexTable) {
			((FlexTable) container).setWidget(row, col, display.asWidget());
		}

		if (type.equals("manifest")) {
			next = "canvasNav";
			display.getViewLabel().setText("Sequences");
			display.getViewLabel().setText(props.getCollection().label() + String.valueOf(props.getDataList().size()));
//			display.setData(props.getDataList(), Manifest.class);
		} else if (type.equals("collection")) {
			next = "manifest";
			display.getViewLabel().setText("Manifests");
			display.getViewLabel().setText(props.getCollection().label() + String.valueOf(props.getDataList().size()));
			display.setData(props.getDataList(), ManifestCollection.class);
		}
	}
	
	private void bind() {
		display.getList().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int selectedRow = display.getSelectedRow(event);
				
				if (selectedRow >= 0) {
					eventBus.fireEvent(new GetDataEvent(HistoryInfo.newToken(
							props.getId(), next, "0")));
				}
			}
		});
	}

}
