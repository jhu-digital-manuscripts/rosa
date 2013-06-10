package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;


public abstract class BasePanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
    	ToggleButton getOptionsButton();
    	
    	ToggleButton getAnnotationButton();
    	
    	ToggleButton getMetadataButton();
    	
    	ToggleButton getTextAnnotationsButton();
    	
    	HasClickHandlers getCloseButton();
    	
    	HasClickHandlers getDuplicateButton();
    	
    	HasClickHandlers getSwapHorizontalButton();
    	
    	HasClickHandlers getSwapVerticalButton();

        void resize(int width, int height);
        
        void selected(boolean is_selected);
    }

    private final Display display;
    private final HandlerManager event_bus;
    private final int panel_id;
    
    public BasePanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
        this.display = display;
        this.event_bus = event_bus;
        this.panel_id = panel_id;

        bind();
    }

    private void bind() {
        
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	display.selected(is_selected);
    }
}
