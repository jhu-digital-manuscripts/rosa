package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.HistoryInfo;
import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class SidebarFullPresenter implements Presenter {

	public interface Display {
		HasClickHandlers getAddPanelButton();
		HasClickHandlers getRemovePanelButton();
		ListBox getPanelList();
		AnnotationListWidget getAnnoListWidget();
		ManifestListWidget getMetaListWidget();
		Widget asWidget();
	}
	
	private final Display display;
	private final HandlerManager eventBus;
	
	public SidebarFullPresenter(Display display, HandlerManager eventBus){
		this.display = display;
		this.eventBus = eventBus;
	}
	
	public void go(HasWidgets container) {
		bind();
		if (container instanceof DockLayoutPanel) {
			((DockLayoutPanel)container).addWest(display.asWidget(), 300);
		}
	}
	
	private void bind() {
		display.getAddPanelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doAddPanel();
			}
		});
		
		display.getRemovePanelButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doRemovePanel();
			}
		});
		
		display.getPanelList().addChangeHandler(new ChangeHandler(){
			public void onChange(ChangeEvent event) {
				
				doPanelListChange();
			}
		});
	}
	
	/**
	 * Refresh list according to current history token
	 * @param token
	 */
	public void refreshList(String token) {
		
		display.getPanelList().clear();
		for (int i=0; i<HistoryInfo.getNumItems(token); i++) {
			display.getPanelList().addItem("Panel "+ (i+1));
		}
		
	}
	
	public void setListSelectet(int index) {
		display.getPanelList().setSelectedIndex(index);
	}
	
	public boolean isListEmpty() {
		return (display.getPanelList().getItemCount() == 0);
	}
	
	/**
	 * Removes item at specified index from the list. Panels in list are renamed
	 * @param index int of item to remove. Selects the first item in list.
	 */
	public void removePanel(int index) {
		display.getPanelList().removeItem(index);
		
		for (int i=0; i<display.getPanelList().getItemCount(); i++) {
			display.getPanelList().setItemText(i, "Panel "+(i+1));
		}
		
		display.getPanelList().setSelectedIndex(0);
	}
	
	/**
	 * Adds a new item to the list and sets it to be selected.
	 * @param item
	 */
	public void addPanel(String item) {
		display.getPanelList().addItem(item);
		
		int selectedPanel = display.getPanelList().getItemCount()-1;
		display.getPanelList().setSelectedIndex(selectedPanel);
		
//		eventBus.fireEvent(new PanelNumberChangeEvent("change", selectedPanel));
	}
	
	private void doAddPanel() {		
		eventBus.fireEvent(new PanelNumberChangeEvent("add"));
	}
	
	private void doRemovePanel() {
		eventBus.fireEvent(new PanelNumberChangeEvent("remove"));
	}
	
	private void doPanelListChange() {
		int selectedPanel = display.getPanelList().getSelectedIndex();
		eventBus.fireEvent(new PanelNumberChangeEvent("change", selectedPanel));
	}

	public void setData(PanelData data) {
		// TODO Auto-generated method stub
		
	}

}
