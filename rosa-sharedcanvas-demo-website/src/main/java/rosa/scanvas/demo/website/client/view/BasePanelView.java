package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.BasePanelPresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class BasePanelView extends Composite implements BasePanelPresenter.Display {
	
	private final FlowPanel main;
	private final FlowPanel context;
//	private final ScrollPanel top;
	
	private final ToggleButton options_button;
	private final ToggleButton anno_button;
	private final ToggleButton meta_button;
	private final ToggleButton text_button;
	
	private final Label close;
	private final Label swap_v;
	private final Label swap_h;
	private final Label dupl;
	private final Label context_label;

    public BasePanelView() {
        main = new FlowPanel();
        context = new FlowPanel();
        main.setStylePrimaryName("PanelView");
        
//        top = new ScrollPanel();
        
        options_button = new ToggleButton("Op");
        anno_button = new ToggleButton("A");
        meta_button = new ToggleButton("I");
        text_button = new ToggleButton("T");
        
        close = new Label("Close Panel");
        swap_v = new Label("Swap ^v");
        swap_h = new Label("Swap <>");
        dupl = new Label("Duplicate Panel");
        context_label = new Label("Context Information...");
        
        main.add(context);
        main.add(text_button);
        main.add(meta_button);
        main.add(anno_button);
        main.add(options_button);

        initWidget(main);
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
    public ToggleButton getAnnotationButton() {
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
