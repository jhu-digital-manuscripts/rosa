package rosa.scanvas.demo.website.client.presenter;

import java.util.List;

import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.demo.website.client.HistoryInfo;
import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelProperties;
import rosa.scanvas.demo.website.client.event.GetDataEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

public class ManifestPresenter implements Presenter {

	public interface Display {
		HasClickHandlers getList();
		HasText getViewLabel();
		void setData(List<Reference<Sequence>> seq);
		int getSelectedRow(ClickEvent event);
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
//	private final String type;
//	private String next;
	
	private PanelProperties props;
	
	public ManifestPresenter(Display display, HandlerManager eventBus, PanelProperties props) {
		this.props = props;
		this.display = display;
		this.eventBus = eventBus;
	}
	
	public void go(HasWidgets container) {
		if (container instanceof FlexTable) {
			((FlexTable) container).setWidget(props.getRow(), props.getCol(), display.asWidget());
		}
		
	}
	
	private void bind(final PanelData data) {
		display.getList().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int selectedRow = display.getSelectedRow(event);
				
				if (selectedRow >= 0) {
					eventBus.fireEvent(new GetDataEvent(HistoryInfo.newToken(
							props.getId(), "canvasNav", "0",
							data.getCollection().uri(), 
							data.getManifest().uri(),
							data.getManifest().sequences().get(selectedRow).uri())));
				}
			}
		});
	}

	public void setData(PanelData data) {
		bind(data);
		
		display.getViewLabel().setText(data.getManifest().label());
		display.setData(data.getManifest().sequences());
	}
	
}
