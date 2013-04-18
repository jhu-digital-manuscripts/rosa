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

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Controls the sidebar, which includes ways to add/remove panels, and the 
 * metadata display and annotation list display
 *
 */
public class MainController implements Controller {

	private final double SIDEBAR_WIDTH_PERCENT = 0.25;
	private final double SIDEBAR_HEIGHT_PERCENT= 1;
	
	private int currentIndex;
	private FlexTable mainTable;
	private HasWidgets container;
	private final HandlerManager eventBus;
	private SidebarFullPresenter sidebarPresenter;
	
	private PanelController panelController;
	
	private int width;
	private int height;
	
	public MainController(HandlerManager eventBus) {
		this.eventBus = eventBus;
		currentIndex = 0;
		
		mainTable = new FlexTable();
		
		bind();
	}
	
	/**
	 * Add handler to listen to changes in History
	 */
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();

		if (token != null) {
			sidebarPresenter.refreshList(token);
			sidebarPresenter.setListSelected(currentIndex);
		}
	}

	public void go(HasWidgets container) {
		this.container = container;
		width = (int) (Window.getClientWidth() * SIDEBAR_WIDTH_PERCENT);
		height= (int) (Window.getClientHeight()* SIDEBAR_HEIGHT_PERCENT);
		
		FlowPanel header = new FlowPanel();
		header.setStylePrimaryName("Header");
		
		header.add(new Label("JHU Prototype Shared Canvas Viewier"));
		
		if (container instanceof DockLayoutPanel) {
		    ((DockLayoutPanel)container).addNorth(header, 100);
		}    
		
		panelController = new PanelController(eventBus);
		
		sidebarPresenter = new SidebarFullPresenter(new SidebarFullView(),eventBus);
		sidebarPresenter.go(container);
		
		ScrollPanel sPanel = new ScrollPanel();
		sPanel.add(mainTable);
		this.container.add(sPanel);
		
		mainTable.setStylePrimaryName("mainTable");
		
		panelController.go(mainTable);
	}
	
	/**
	 * Add handlers to listen for application events
	 */
	private void bind() {
		History.addValueChangeHandler(this);
		
		Window.addResizeHandler(new ResizeHandler() {
			public void onResize(ResizeEvent event) {
				width = (int) (event.getWidth() * SIDEBAR_WIDTH_PERCENT);
				height = (int) (event.getHeight()*SIDEBAR_HEIGHT_PERCENT);
				
				doResize();
			}
		});
		
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
	
	private void doResize() {
/*		sidebarPresenter.setSize(String.valueOf(width)+"px", 
								String.valueOf(height)+"px");*/
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
			
		} else if (message.equals(PanelAction.CHANGE)) {
			
			if (currentIndex != selectedPanel) {
				// unhighlight previous panel
				currentIndex = selectedPanel;
				// highlight panel;
			}
			
		} else if (message.equals(PanelAction.REMOVE)) {
			// TODO: Move logic to HistoryInfo
		    // remove history token segment from current history token, removing the panel from display
			
			
			currentIndex = 0;
		}
	}

	public void doDataUpdate(PanelData data) {
		sidebarPresenter.setData(data);
	}
}
