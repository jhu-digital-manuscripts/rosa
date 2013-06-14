package rosa.scanvas.demo.website.client;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.event.PanelAddedEvent;
import rosa.scanvas.demo.website.client.event.PanelAddedEventHandler;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent.PanelAction;
import rosa.scanvas.demo.website.client.event.PanelRequestEventHandler;
import rosa.scanvas.demo.website.client.event.PanelSelectedEvent;
import rosa.scanvas.demo.website.client.event.PanelSelectedEventHandler;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;
import rosa.scanvas.demo.website.client.presenter.HomePanelPresenter;
import rosa.scanvas.demo.website.client.presenter.HomePresenter;
import rosa.scanvas.demo.website.client.presenter.ManifestCollectionPanelPresenter;
import rosa.scanvas.demo.website.client.presenter.ManifestPanelPresenter;
import rosa.scanvas.demo.website.client.presenter.PanelPresenter;
import rosa.scanvas.demo.website.client.presenter.SequencePanelPresenter;
import rosa.scanvas.demo.website.client.presenter.SidebarPresenter;
import rosa.scanvas.demo.website.client.view.SequenceView;
import rosa.scanvas.demo.website.client.view.CanvasView;
import rosa.scanvas.demo.website.client.view.CollectionView;
import rosa.scanvas.demo.website.client.view.HomeView;
import rosa.scanvas.demo.website.client.view.HomePanelView;
import rosa.scanvas.demo.website.client.view.ManifestView;
import rosa.scanvas.demo.website.client.view.SidebarFullView;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainController implements ValueChangeHandler<String>, IsWidget {
	private static final int SIDEBAR_WIDTH = 275;
	private static final int HEADER_HEIGHT = 60;
	
    private static int next_panel_id = 0;

    private final DockLayoutPanel main;
    private final FlowPanel main_content;
    private final FlowPanel header_space;
    //private final Button add_panel_button;
    private final Label app_header;
    
    private final Image add_image;
    
    private final HandlerManager event_bus;
    private final ArrayList<Panel> panels;

    private int panel_width;
    private int panel_height;

    public MainController(HandlerManager event_bus) {
        this.main = new DockLayoutPanel(Unit.PX);
        main.setStylePrimaryName("Main");

        this.main_content = new FlowPanel();
        this.header_space = new FlowPanel();
        
        this.event_bus = event_bus;
        this.panels = new ArrayList<Panel>();
        
        this.add_image = new Image("icons/add.png");
        //this.add_panel_button = new Button(add_image.getElement().getInnerHTML());
        
        //this.add_panel_button = new Button("Add Panel");
        
        FlowPanel header = new FlowPanel();
        header.setStylePrimaryName("Header");
        app_header = new Label("JHU Prototype Shared Canvas Viewer");
        app_header.addStyleName("HeaderTitle");
        
        header.add(app_header);
        header.add(header_space);
        //header_space.add(add_panel_button);
        header_space.add(add_image);
        
        header_space.setHeight(HEADER_HEIGHT + "px");
        header_space.setWidth(Window.getClientWidth() + "px");
        header_space.addStyleName("AddButton");
        
        main.addNorth(header, HEADER_HEIGHT);
        
        ScrollPanel sp = new ScrollPanel();
        sp.add(main_content);
        main.add(sp);
        
        main.getWidgetContainerElement(header).setClassName("AppHeader");
        main.getWidgetContainerElement(sp).setClassName("Content");

        calculate_panel_size(Window.getClientWidth(), Window.getClientHeight());
        bind();
        
        ((Label)header.getWidget(0)).addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		History.newItem("home", true);
        	}
        });
    }

    /**
     * Bind event handlers to the event bus.
     */
    private void bind() {
        History.addValueChangeHandler(this);

        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                doResize(event.getWidth(), event.getHeight());
            }
        });

        /*Window.addResizeHandler(new ResizeHandler() {
            int width = Window.getClientWidth();
            int height = Window.getClientHeight();

            public void onResize(ResizeEvent event) {
                int dx = event.getWidth() - width;
                int dy = event.getHeight() - height;

                if (Math.abs(dx) > 10 || Math.abs(dy) > 10) {
                    width = event.getWidth();
                    height = event.getHeight();

                    doResize(width, height);
                }
            }
        });*/

        event_bus.addHandler(PanelRequestEvent.TYPE,
                new PanelRequestEventHandler() {
                    public void onPanelRequest(PanelRequestEvent event) {
                        doPanelRequest(event.getAction(), event.getPanelId(),
                                event.getPanelState());
                    }
                });
        
        event_bus.addHandler(PanelSelectedEvent.TYPE,
        		new PanelSelectedEventHandler() {
        	public void onPanelSelected(PanelSelectedEvent event) {
        		doPanelSelected(event.getPanelId());
        	}
        });
        
        add_image.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		PanelRequestEvent req = new PanelRequestEvent(
        				PanelRequestEvent.PanelAction.ADD, new PanelState());
        		event_bus.fireEvent(req);
        	}
        });
    }

    /**
     * Create a new panel presenter for a panel based off
     * information about its view
     * 
     * @param view
     * @param panel_id
     */
    private PanelPresenter create_panel_presenter(PanelView view, int panel_id) {
        switch (view) {
        case CANVAS:
            return new CanvasPanelPresenter(new CanvasView(), event_bus, panel_id);
        case HOME:
            return new HomePanelPresenter(new HomeView(), event_bus, panel_id);
        	
        case MANIFEST:
            return new ManifestPanelPresenter(new ManifestView(), event_bus,
                    panel_id);
        case MANIFEST_COLLECTION:
            return new ManifestCollectionPanelPresenter(new CollectionView(),
                    event_bus, panel_id);
        case SEQUENCE:
            return new SequencePanelPresenter(new SequenceView(), event_bus,
                    panel_id);
        default:
            throw new RuntimeException("Unhandled view: " + view);
        }
    }

    /**
     * Add a new panel with a specified state.
     * 
     * @param state
     */
    private void add_panel(PanelState state) {
        int panel_id = next_panel_id++;
        Panel panel = new Panel(create_panel_presenter(state.getView(),
                panel_id), panel_id);

        panels.add(panel);
        main_content.add(panel.getPresenter());

        event_bus.fireEvent(new PanelAddedEvent(panel_id));
        
        update_panel_sizes(Window.getClientWidth(), Window.getClientHeight());
        panel.display(state);
    }

    /**
     * Change the state of a panel
     * 
     * @param state
     * 			the new panel state
     * @param panel_id
     * 			ID of the panel to change
     */
    private void change_panel_by_id(PanelState state, int panel_id) {
        int index = find_panel_index(panel_id);

        if (index == -1) {
            return;
        }

        change_panel_by_index(state, index);
    }

    /**
     * Change the state of a panel
     * 
     * @param state
     * 			the new panel state
     * @param index
     * 			index of the panel to change
     */
    private void change_panel_by_index(PanelState state, int index) {
        Panel panel = panels.get(index);

        if (panel.getState().getView() == state.getView()) {
            // Update data and redisplay
            panel.display(state);
        } else if (panel.getState().equals(state)) {
            // Nothing to do
        } else {
            // Change the panel presenter and display

            PanelPresenter presenter = create_panel_presenter(state.getView(),
                    panel.getId());
            panel.setPresenter(presenter);

            main_content.insert(presenter, index);
            main_content.remove(index + 1);

            presenter.resize(panel_width, panel_height);
            doPanelSelected(panel.getId());
            panel.display(state);
        }
    }

    /**
     * Remove a panel from the content area and data list
     * 
     * @param panel_id
     * 				ID of the panel to remove
     */
    private void remove_panel_by_id(int panel_id) {
        int index = find_panel_index(panel_id);

        if (index == -1) {
            return;
        }

        panels.remove(index);
        main_content.remove(index);

        update_panel_sizes(Window.getClientWidth(), Window.getClientHeight());
    }

    /**
     * Issue events to handle history changes.
     * 
     * @param event
     */
    public void onValueChange(ValueChangeEvent<String> event) {
        HistoryState history_state = HistoryState.parseHistoryToken(event
                .getValue());

        if (history_state == null) {
            Window.alert("Failed to parse history state");
            // TODO
            return;
        }

        List<PanelState> panel_states = history_state.panelStates();

        for (int i = 0; i < panel_states.size(); i++) {
            PanelState panel_state = panel_states.get(i);

            if (i < panels.size()) {
                Panel old_panel = panels.get(i);
                PanelRequestEvent req = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.CHANGE,
                        old_panel.getId(), panel_state);
                event_bus.fireEvent(req);
            } else {
                PanelRequestEvent req = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.ADD, panel_state);
                event_bus.fireEvent(req);
            }
        }
  
        // if any old panels still exist, remove them
        int old_size = panels.size();
        for (int i = panel_states.size(); i < old_size; i++) {
        	Panel old_panel = panels.get(panel_states.size());
        	PanelRequestEvent req = new PanelRequestEvent(
        			PanelRequestEvent.PanelAction.REMOVE, old_panel.getId());
        	event_bus.fireEvent(req);
        }
    }

    private void calculate_panel_size(int win_width, int win_height) {
        int count = panels.size();

        if (count == 0) {
            count = 1;
        }

        panel_width = (win_width/* - SIDEBAR_WIDTH*/) - 40;
        panel_height = (win_height - HEADER_HEIGHT) - 28;

        if (count > 1) {
            panel_width /= 2;
            panel_width -= 15;

            if (count > 2) {
                panel_height /= 2;
                panel_height -= 15;
            }
        }

        // panel_width -= count * 10;
        // panel_height -= count * 5;

        if (panel_width < 300) {
            panel_width = 300;
        }

        if (panel_width > 2000) {
            panel_width = 2000;
        }

        if (panel_height < 200) {
            panel_height = 200;
        }

        if (panel_height > 2000) {
            panel_height = 2000;
        }
    }

    // TODO Be smart and only call resize when size changes...
    private void update_panel_sizes(int win_width, int win_height) {
        calculate_panel_size(win_width, win_height);

        for (Panel panel : panels) {
            panel.getPresenter().resize(panel_width, panel_height);
        }
    }

    private void doResize(int win_width, int win_height) {
    	header_space.setWidth(win_width + "px");
    	
        update_panel_sizes(win_width, win_height);
    }

    /**
     * Retrieves the index of a panel from its ID
     * 
     * @param panel_id
     * @return the index of the panel. If the specified ID is not found,
     * -1 is returned.
     */
    private int find_panel_index(int panel_id) {
        for (int i = 0; i < panels.size(); i++) {
            if (panels.get(i).getId() == panel_id) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Performs appropriate action to a panel
     * 
     * @param action
     * @param panel_id
     */
    private void doPanelRequest(PanelAction action, int panel_id,
            PanelState panel_state) {
        if (action == PanelAction.ADD) {
            add_panel(panel_state);
        } else if (action == PanelAction.CHANGE) {
            change_panel_by_id(panel_state, panel_id);
        } else if (action == PanelAction.REMOVE && panels.size() > 1) {
            remove_panel_by_id(panel_id);
        }

        History.newItem(get_history_token(), false);
    }

    /**
     * Sets the style of selected or unselected panels
     * 
     * @param panel_id
     */
    private void doPanelSelected(int panel_id) {
    	// set all panels as unselected except for the specified Id
    	for (Panel p : panels) {
    		if (p.getId() == panel_id) {
    			p.getPresenter().selected(true);
    		} else {
    			p.getPresenter().selected(false);
    		}
    	}
    }
    
    private String get_history_token() {
        PanelState[] panel_states = new PanelState[panels.size()];

        for (int i = 0; i < panels.size(); i++) {
            panel_states[i] = panels.get(i).getState();
        }

        return new HistoryState(panel_states).toToken();
    }

    /**
     * Called when the application starts running
     */
    public void go() {
        if (History.getToken().isEmpty()) {
            PanelRequestEvent event = new PanelRequestEvent(
                    PanelRequestEvent.PanelAction.ADD, new PanelState());
            event_bus.fireEvent(event);
        } else {
            History.fireCurrentHistoryState();
        }
    }

    @Override
    public Widget asWidget() {
        return main;
    }
}
