package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.BasePanelPresenter;
import rosa.scanvas.demo.website.client.widgets.AnnotationListWidget;
import rosa.scanvas.demo.website.client.widgets.ManifestListWidget;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class BasePanelView extends Composite implements BasePanelPresenter.Display {
	private final FlowPanel main;
	private final FlowPanel title_bar;
	
	private AnnotationListWidget annoListWidget;
	private ManifestListWidget metaListWidget;
	
	private final ToggleButton options_button;
	private final ToggleButton anno_button;
	private final ToggleButton meta_button;
	private final ToggleButton text_button;
	
	private final PopupPanel text_popup;
	private final PopupPanel meta_popup;
	private final PopupPanel anno_popup;
	private final PopupPanel options_popup;
	
	private final Label close;
	private final Label swap_v;
	private final Label swap_h;
	private final Label dupl;
	private final Label context_label;

    public BasePanelView() {
        main = new FlowPanel();
        main.setStylePrimaryName("PanelView");
        title_bar = new FlowPanel();
        title_bar.setStylePrimaryName("PanelTitleBar");
        
        options_button = new ToggleButton("Op");
        anno_button = new ToggleButton("A");
        meta_button = new ToggleButton("I");
        text_button = new ToggleButton("T");
        
        text_popup = new PopupPanel(false, false);
		meta_popup = new PopupPanel(false, false);
		anno_popup = new PopupPanel(false, false);
		options_popup = new PopupPanel(false, false);
        
        close = new Label("Close Panel");
        swap_v = new Label("Swap ^v");
        swap_h = new Label("Swap <>");
        dupl = new Label("Duplicate Panel");
        
        context_label = new Label("Context Information...");
        context_label.addStyleName("Context");
        
        main.add(title_bar);
        title_bar.add(context_label);
        title_bar.add(text_button);
        title_bar.add(anno_button);
        title_bar.add(meta_button);
        title_bar.add(options_button);
        
        initWidget(main);
        
        setup_options();
/*        setup_annotations_list();
        setup_meta_list();
        setup_text_annotations();*/
    }
    
/*    private void setup_annotations_list() {
		ScrollPanel top = new ScrollPanel();
		FlowPanel main = new FlowPanel();
		
		top.add(main);
		
		Label header = new Label("List of Annotations");
		header.addStyleName("TitleHeader");
		
		main.add(header);
		main.add(annoListWidget);
		
		anno_popup.setWidget(top);
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
		
//		metaListWidget.setMetadata(data);
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
	}*/
    
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
		
		options_popup.addAttachHandler(new AttachEvent.Handler() {
			public void onAttachOrDetach(AttachEvent event) {
				if (event.isAttached()) {
					options_popup.setPopupPosition(options_button.getAbsoluteLeft()
						+ options_button.getOffsetWidth()
						- options_popup.getOffsetWidth(),
						options_button.getAbsoluteTop() 
						+ options_button.getOffsetHeight());
				}
			}
		});
    }

    /**
	 * Adds a new widget to the content area
	 * 
	 * @param content
	 */
	public void addContent(Widget content) {
		main.add(content);
	}
    
    @Override
    public ToggleButton getOptionsButton() {
    	return options_button;
    }
	
    @Override
    public ToggleButton getAnnotationsButton() {
    	return anno_button;
    }
	
    @Override
    public ToggleButton getMetadataButton() {
    	return meta_button;
    }
	
    @Override
    public ToggleButton getTextAnnotationsButton() {
    	return text_button;
    }
    
    @Override
    public PopupPanel getOptionsPopup() {
    	return options_popup;
    }
	
    @Override
    public PopupPanel getAnnotationsPopup() {
    	return anno_popup;
    }
	
    @Override
    public PopupPanel getMetadataPopup() {
    	return meta_popup;
    }
	
    @Override
    public PopupPanel getTextAnnotationsPopup() {
    	return text_popup;
    }
	
    @Override
    public HasClickHandlers getCloseButton() {
    	return close;
    }
	
    @Override
    public HasClickHandlers getDuplicateButton() {
    	return dupl;
    }
	
    @Override
    public HasClickHandlers getSwapHorizontalButton() {
    	return swap_h;
    }
	
    @Override
    public HasClickHandlers getSwapVerticalButton() {
    	return swap_v;
    }
	
    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);
        
        context_label.setWidth(width - text_button.getOffsetWidth()
        		- meta_button.getOffsetWidth() - anno_button.getOffsetWidth()
        		- options_button.getOffsetWidth() + "px");
        context_label.setHeight(options_button.getOffsetHeight() + "px");
        
        int popup_top = options_button.getAbsoluteTop() + options_button.getOffsetHeight();
		
		// set popup position
/*		text_popup.setPopupPosition((text_button.getAbsoluteLeft() + text_button.getOffsetWidth()
				- text_popup.getOffsetWidth()), popup_top);
	
		meta_popup.setPopupPosition((meta_button.getAbsoluteLeft() + meta_button.getOffsetWidth()
				- meta_popup.getOffsetWidth()), popup_top);

		
		anno_popup.setPopupPosition((anno_button.getAbsoluteLeft() + anno_button.getOffsetWidth()
				- anno_popup.getOffsetWidth()), popup_top);*/
		
		options_popup.setPopupPosition((options_button.getAbsoluteLeft() + options_button.getOffsetWidth()
				- options_popup.getOffsetWidth()), popup_top);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	/*if (is_selected) {
    		top.addStyleName("PanelSelected");
    	} else {
    		top.removeStyleName("PanelSelected");
    	}*/
    }
}
