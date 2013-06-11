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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public abstract class BasePanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
    	ToggleButton getOptionsButton();
    	
    	ToggleButton getAnnotationsButton();
    	
    	ToggleButton getMetadataButton();
    	
    	ToggleButton getTextAnnotationsButton();
    	
    	PopupPanel getOptionsPopup();
    	
    	PopupPanel getAnnotationsPopup();
    	
    	PopupPanel getMetadataPopup();
    	
    	PopupPanel getTextAnnotationsPopup();
    	
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
    
    private PanelData data;
    
    private boolean anno_list_ready;
	private boolean meta_list_ready;
	private boolean text_list_ready;
    
    public BasePanelPresenter(Display display, HandlerManager event_bus,
            int panel_id) {
        this.display = display;
        this.event_bus = event_bus;
        this.panel_id = panel_id;

        bind_dom();
    }

    private void bind_dom() {
        display.getOptionsButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (display.getOptionsButton().isDown()) {
        			display.getAnnotationsButton().setDown(false);
        			display.getTextAnnotationsButton().setDown(false);
        			display.getMetadataButton().setDown(false);
        			
        			display.getOptionsPopup().show();
        			display.getAnnotationsPopup().hide();
        			display.getMetadataPopup().hide();
        			display.getTextAnnotationsPopup().hide();
        		} else {
        			display.getOptionsPopup().hide();
        		}
        	}
        });
        
    }

    public HandlerManager eventBus() {
    	return event_bus;
    }
    
    public int panelId() {
    	return panel_id;
    }
    
    public void setData(PanelData data) {
    	this.data = data;
    	
    	anno_list_ready = false;
		meta_list_ready = false;
		text_list_ready = false;
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
