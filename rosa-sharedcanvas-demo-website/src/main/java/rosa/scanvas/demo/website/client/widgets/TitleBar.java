package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;

import com.google.gwt.user.client.Window;

public class TitleBar extends Composite {
	
	private final int panel_id;
	private HandlerManager event_bus;
	private PanelData data;
	
	private AnnotationListWidget annoListWidget;
	private ManifestListWidget metaListWidget;
	private boolean anno_list_ready;
	private boolean meta_list_ready;
	private boolean text_list_ready;
	
	private FlowPanel main_panel;
	private FlowPanel context;
	
	private ToggleButton text_button;
	private ToggleButton meta_button;
	private ToggleButton anno_button;
	private ToggleButton options_button;
	
	private PopupPanel text_popup;
	private PopupPanel meta_popup;
	private PopupPanel anno_popup;
	private PopupPanel options_popup;
	
	public TitleBar (int panel_id) {
		this.panel_id = panel_id;
		
		annoListWidget = new AnnotationListWidget();
		metaListWidget = new ManifestListWidget();
		
		main_panel = new FlowPanel();
		context = new FlowPanel();
		
		main_panel.setStylePrimaryName("PanelTitleBar");
		context.addStyleName("Context");
		
		context.add(new Label("Context Information..."));
		
		// TODO: 2 icons for each of the buttons, one for up one for down positions
		text_button = new ToggleButton("T");
		meta_button = new ToggleButton("I");
		anno_button = new ToggleButton("A");
		options_button = new ToggleButton("O");
		
		text_popup = new PopupPanel(false, false);
		meta_popup = new PopupPanel(false, false);
		anno_popup = new PopupPanel(false, false);
		options_popup = new PopupPanel(false, false);
		
		main_panel.add(context);
		main_panel.add(text_button);
		main_panel.add(meta_button);
		main_panel.add(anno_button);
		main_panel.add(options_button);
		
		setup_options();
		bind_dom();
		
		initWidget(main_panel);
	}
	
	public void setEventBus(HandlerManager event_bus) {
		this.event_bus = event_bus;
		bind_event_bus();
	}
	
	/**
	 * Bind event handlers for DOM events
	 */
	private void bind_dom() {
		options_button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (options_button.isDown()) {
					options_popup.show();
					options_popup.setPopupPosition((options_button.getAbsoluteLeft() 
							+ options_button.getOffsetWidth() - options_popup.getOffsetWidth()), 
							options_button.getAbsoluteTop() + options_button.getOffsetHeight());
					
					meta_button.setDown(false);
					anno_button.setDown(false);
					text_button.setDown(false);
					meta_popup.hide();
					anno_popup.hide();
					text_popup.hide();
				} else {
					options_popup.hide();
				}
			}
		});
		
		meta_button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!meta_list_ready) {
					setup_meta_list();
				}
				
				if (meta_button.isDown()) {
					meta_popup.show();
					meta_popup.setPopupPosition((meta_button.getAbsoluteLeft() + meta_button.getOffsetWidth()
							- meta_popup.getOffsetWidth()), 
							options_button.getAbsoluteTop() + options_button.getOffsetHeight());
					
					anno_button.setDown(false);
					text_button.setDown(false);
					options_button.setDown(false);
					anno_popup.hide();
					text_popup.hide();
					options_popup.hide();
				} else {
					meta_popup.hide();
				}
			}
		});
		
		anno_button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!anno_list_ready) {
					setup_annotation_list();
				}
				
				if (anno_button.isDown()) {
					anno_popup.show();
					
					anno_popup.setPopupPosition((anno_button.getAbsoluteLeft() + anno_button.getOffsetWidth()
							- anno_popup.getOffsetWidth()), 
							options_button.getAbsoluteTop() + options_button.getOffsetHeight());
					
					meta_button.setDown(false);
					text_button.setDown(false);
					options_button.setDown(false);
					meta_popup.hide();
					text_popup.hide();
					options_popup.hide();
				} else {
					anno_popup.hide();
				}
			}
		});
		
		text_button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!text_list_ready) {
					setup_text_annotations();
				}
				
				if (text_button.isDown()) {
					text_popup.show();
					text_popup.setPopupPosition((text_button.getAbsoluteLeft() + text_button.getOffsetWidth()
							- text_popup.getOffsetWidth()), 
							options_button.getAbsoluteTop() + options_button.getOffsetHeight());
					
					anno_button.setDown(false);
					meta_button.setDown(false);
					options_button.setDown(false);
					anno_popup.hide();
					meta_popup.hide();
					options_popup.hide();
				} else {
					text_popup.hide();
				}
			}
		});
	}
	
	private void bind_event_bus() {
		if (event_bus == null) {
			return;
		}
		// TODO operations in the Options menu
	}
	
	/**
	 * 
	 */
	public void display(PanelData data) {
		this.data = data;
		
		anno_list_ready = false;
		meta_list_ready = false;
		text_list_ready = false;
		
		if (data == null) {
			return;
		}
	}
	
	/**
	 * Resize the title bar
	 * 
	 * @param width
	 * @param height
	 */
	public void resize(int width, int height) {
		// set the context (panel) width to be full width minus button widths
		context.setWidth((width - text_button.getOffsetWidth() 
				- meta_button.getOffsetWidth() - anno_button.getOffsetWidth()
				- options_button.getOffsetWidth()) + "px");
		context.setHeight(options_button.getOffsetHeight() + "px");
		
		// make popup width dynamic?
		// TODO set popup heights
		
		int popup_top = options_button.getAbsoluteTop() + options_button.getOffsetHeight();
		
		// set popup position
		if (text_list_ready) {
			text_popup.setPopupPosition((text_button.getAbsoluteLeft() + text_button.getOffsetWidth()
					- text_popup.getOffsetWidth()), popup_top);
		}
		
		if (meta_list_ready) {
			meta_popup.setPopupPosition((meta_button.getAbsoluteLeft() + meta_button.getOffsetWidth()
					- meta_popup.getOffsetWidth()), popup_top);
		}
		
		if (anno_list_ready) {
			anno_popup.setPopupPosition((anno_button.getAbsoluteLeft() + anno_button.getOffsetWidth()
					- anno_popup.getOffsetWidth()), popup_top);
		}
		
		options_popup.setPopupPosition((options_button.getAbsoluteLeft() + options_button.getOffsetWidth()
				- options_popup.getOffsetWidth()), popup_top);
	}
	
	private void setup_annotation_list() {
		ScrollPanel top = new ScrollPanel();
		FlowPanel main = new FlowPanel();
		
		top.add(main);
		
		Label header = new Label("List of Annotations");
		header.addStyleName("TitleHeader");
		
		main.add(header);
		main.add(annoListWidget);
		
		anno_popup.setWidget(top);
		anno_list_ready = true;
	}
	
	private void setup_meta_list() {
		ScrollPanel top = new ScrollPanel();
		FlowPanel main = new FlowPanel();
		
		top.add(main);
		
		Label header = new Label("Metadata");
		header.addStyleName("TitleHeader");
		
		main.add(header);
		main.add(metaListWidget);
		
		meta_popup.setWidget(top);
		
		metaListWidget.setMetadata(data);
		meta_list_ready = true;
	}
	
	private void setup_text_annotations() {
		FlowPanel main = new FlowPanel();
		
		TabLayoutPanel tab_panel = new TabLayoutPanel(30, Style.Unit.PX);
		tab_panel.setSize(200+"px", 100+"px");
		
		Label header = new Label("non-targeted Text Annotations");
		header.addStyleName("TitleHeader");
		
		main.add(header);
		main.add(tab_panel);
		
		text_popup.setWidget(main);
		
		text_list_ready = true;
	}
	
	private void setup_options() {
		FlowPanel main = new FlowPanel();
		options_popup.setWidget(main);
		
		Label header = new Label("Options");
		header.addStyleName("TitleHeader");
		
		Grid options_grid = new Grid(4,1);
		Label dup = new Label("Duplicate Panel");
		Label swap_h = new Label("Swap <>");
		Label swap_v = new Label("Swap ^v");
		Label close = new Label("Close Panel");
		
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				options_popup.hide();
				
				PanelRequestEvent evt = new PanelRequestEvent(
						PanelRequestEvent.PanelAction.REMOVE, panel_id);
				event_bus.fireEvent(evt);
			}
		});
		
		options_grid.addStyleName("Options");
		options_grid.setWidget(0, 0, dup);
		options_grid.setWidget(1, 0, swap_h);
		options_grid.setWidget(2, 0, swap_v);
		options_grid.setWidget(3, 0, close);
		
		for (int i = 0; i < options_grid.getRowCount(); i++) {
			options_grid.getWidget(i, 0).addStyleName("OptionsRow");
		}
		
		main.add(header);
		main.add(options_grid);
	}
	
}
