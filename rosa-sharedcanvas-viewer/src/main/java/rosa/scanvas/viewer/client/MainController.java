package rosa.scanvas.viewer.client;

import rosa.scanvas.viewer.client.event.PanelNumberChangeEvent;
import rosa.scanvas.viewer.client.event.PanelNumberChangeEventHandler;
import rosa.scanvas.viewer.client.event.SidebarViewChangeEvent;
import rosa.scanvas.viewer.client.event.SidebarViewChangeEventHandler;
import rosa.scanvas.viewer.client.presenter.Presenter;
import rosa.scanvas.viewer.client.presenter.SidebarFullPresenter;
import rosa.scanvas.viewer.client.view.SidebarFullView;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ScrollPanel;

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
			sidebarPresenter.setListSelectet(currentIndex);
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
	}
	
	private void doPanelNumberChange(String message, int selectedPanel) {
		String currentToken = History.getToken();
		
		if (message.equals("add")) {
			
			if (currentToken.equals("")) {
				currentIndex = 0;
			} else {
				currentIndex = currentToken.split(";:").length;
			}
			
			String newToken = HistoryInfo.newToken(String.valueOf(++historyId), "home", "0");
			History.newItem(currentToken+newToken);
			
		} else if (message.equals("change")) {
			
			if (currentIndex != selectedPanel) {
				// unhighlight previous panel
				currentIndex = selectedPanel;
				// highlight panel;
			}
			
		} else if (message.equals("remove")) {
			
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

	public void setData(PanelData data) {
		// TODO Auto-generated method stub
		
	}
	
}
