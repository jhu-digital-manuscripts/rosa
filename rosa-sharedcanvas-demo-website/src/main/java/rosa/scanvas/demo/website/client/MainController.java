package rosa.scanvas.demo.website.client;

import rosa.scanvas.demo.website.client.event.DataUpdateEvent;
import rosa.scanvas.demo.website.client.event.DataUpdateEventHandler;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEvent.PanelAction;
import rosa.scanvas.demo.website.client.event.PanelNumberChangeEventHandler;
import rosa.scanvas.demo.website.client.event.SidebarViewChangeEvent;
import rosa.scanvas.demo.website.client.event.SidebarViewChangeEventHandler;
import rosa.scanvas.demo.website.client.presenter.Presenter;
import rosa.scanvas.demo.website.client.presenter.SidebarFullPresenter;
import rosa.scanvas.demo.website.client.view.SidebarFullView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Controls the sidebar, which includes ways to add/remove panels, and the 
 * metadata display and annotation list display
 * @author john
 *
 */
public class MainController implements Controller {

	private int currentIndex;
	private FlexTable mainTable;
	private HasWidgets container;
	private final HandlerManager eventBus;
	private SidebarFullPresenter sidebarPresenter;
	
	private PanelController panelController;
	
	private int historyId;
	
	public MainController(HandlerManager eventBus) {
		this.eventBus = eventBus;
		currentIndex = 0;
		historyId = 0;
		
		mainTable = new FlexTable();
		
		bind();
	}
	
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			sidebarPresenter.refreshList(token);
			sidebarPresenter.setListSelected(currentIndex);
		}
	}

	public void go(HasWidgets container) {
		this.container = container;
		
		panelController = new PanelController(eventBus);
		
		sidebarPresenter = new SidebarFullPresenter(new SidebarFullView(),eventBus);
		sidebarPresenter.go(container);
		
		ScrollPanel sPanel = new ScrollPanel();
		sPanel.add(mainTable);
		this.container.add(sPanel);
		
		mainTable.setStylePrimaryName("mainTable");
		
		panelController.go(mainTable);
	}
	
	private void bind() {
		History.addValueChangeHandler(this);
		
		eventBus.addHandler(PanelNumberChangeEvent.TYPE, 
				new PanelNumberChangeEventHandler() {
					public void onPanelNumberChange(PanelNumberChangeEvent event) {
						doPanelNumberChange(event.getMessage(), event.getSelectedPanel());
					}
		});
		
		eventBus.addHandler(DataUpdateEvent.TYPE, new DataUpdateEventHandler() {
			public void onDataUpdate(DataUpdateEvent event) {
				doDataUpdate(event.getData());
			}
		});
	}
	
	/**
	 * Occurs when a panel is added or removed, or a different panel is selected
	 * @param message PanelAction enum message from the event, indicates type of action (add, remove, change)
	 * @param selectedPanel index of the selected panel
	 */
	private void doPanelNumberChange(PanelAction message, int selectedPanel) {
		String currentToken = History.getToken();
		
		if (message.equals(PanelAction.ADD)) {
			// add a default home token to the end of the current history token
			if (currentToken.equals("")) {
				currentIndex = 0;
			} else {
				currentIndex = currentToken.split(";:").length;
			}
			
			String newToken = HistoryInfo.newToken(String.valueOf(++historyId), "home", "0");
			History.newItem(currentToken+newToken);
			
		} else if (message.equals(PanelAction.CHANGE)) {
			
			if (currentIndex != selectedPanel) {
				// unhighlight previous panel
				currentIndex = selectedPanel;
				// highlight panel;
			}
			
		} else if (message.equals(PanelAction.REMOVE)) {
			// TODO: Move logic to HistoryInfo
		    // remove history token segment from current history token, removing the panel from display
			String[] parts = currentToken.split(";:");
			String newToken = "";
			
			for (int i=0; i<parts.length; i++) {
				if (i != currentIndex) {
					newToken += parts[i] + ";:";
				}
			}
			currentIndex = 0;
			History.newItem(newToken);
		}
	}

	public void doDataUpdate(PanelData data) {
		sidebarPresenter.setData(data);
	}
	
	
	public void setData(PanelData data) {
		// TODO Auto-generated method stub
		
	}
	
}
