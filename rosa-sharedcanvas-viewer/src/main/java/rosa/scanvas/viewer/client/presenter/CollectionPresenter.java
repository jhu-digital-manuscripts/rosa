package rosa.scanvas.viewer.client.presenter;

import java.util.List;

import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.viewer.client.HistoryInfo;
import rosa.scanvas.viewer.client.PanelData;
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
		void setData(List list);
//		void setLabel(String label);
		int getSelectedRow(ClickEvent event);
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
	private String next;
	
	private PanelProperties props;
	
	public CollectionPresenter(Display display, HandlerManager eventBus, PanelProperties props) {
		
		this.props = props;
		this.display = display;
		this.eventBus = eventBus;
	}
	
	public void go(HasWidgets container) {
		bind();
		if (container instanceof FlexTable) {
			((FlexTable) container).setWidget(props.getRow(), props.getCol(), display.asWidget());
		}

		display.getViewLabel().setText("Manifests");

	}
	
	public void setData(PanelData data) {
		display.getViewLabel().setText(data.getCollection().label() + 
				data.getCollection().manifests().size());
		display.setData(data.getCollection().manifests());
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
