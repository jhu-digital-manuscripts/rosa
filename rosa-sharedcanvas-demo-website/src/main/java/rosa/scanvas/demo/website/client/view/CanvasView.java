package rosa.scanvas.demo.website.client.view;

import java.util.ArrayList;
import java.util.HashMap;

import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.TranscriptionViewer;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;
import rosa.scanvas.model.client.Annotation;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.Window;
public class CanvasView extends Composite implements CanvasPanelPresenter.Display {
	
	private static DialogBox transcript;
	private static DialogBox top;
	
	private static DialogBox transcript_box() {
		return transcript;
	}
	private static DialogBox top_box() {
		return top;
	}
	
	private final Panel main;
    private final Label title;
    private final DisplayAreaView area_view;
    
    private final Button zoomInButton;
    private final Button zoomOutButton;
    private final Button resetButton;
    
    private TabLayoutPanel tab_panel = new TabLayoutPanel(
    		40, Style.Unit.PX);

    private HashMap<String, ScrollPanel> tabs = new HashMap<String, ScrollPanel>();
    
    public CanvasView() {
        this.main = new FlowPanel();
        this.title = new Label();
        
        this.zoomInButton = new Button("Zoom In");
        this.zoomOutButton = new Button("Zoom Out");
        this.resetButton = new Button("Reset");
        
        this.area_view = new DisplayAreaView();
        
        FlowPanel canvas_toolbar = new FlowPanel();
        canvas_toolbar.setStylePrimaryName("CanvasToolbar");

        main.add(title);
        main.add(area_view);
        main.add(canvas_toolbar);
        
        canvas_toolbar.add(zoomInButton);
        canvas_toolbar.add(zoomOutButton);
        canvas_toolbar.add(resetButton);

        main.setStylePrimaryName("PanelView");
        title.addStyleName("PanelTitle");
        
        initWidget(main);
        
        tab_panel.addStyleName("TextAnnoTabPanel");
        if (top_box() == null) {
        	top = new DialogBox(false, false);
        	top_box().setText("Text Annotations");
        	top_box().add(tab_panel);
        	top_box().addStyleName("AnnotationDialog");
        }
        
        if (transcript_box() == null) {
        	transcript = new DialogBox(false, false);
        	transcript_box().setText("Transcriptions");
        	transcript_box().addStyleName("AnnotationDialog");
        }
        
    }

    @Override
    public Label getLabel() {
        return title;
    }

    @Override
    public DisplayAreaView getDisplayAreaWidget() {
        return area_view;
    }
    
    @Override
    public Button getZoomInButton() {
    	return zoomInButton;
    }
    
    @Override
    public Button getZoomOutButton() {
    	return zoomOutButton;
    }
    
    @Override
    public Button getResetButton() {
    	return resetButton;
    }
    
    /**
     * Adds text to a dialog box and shows it if not visible
     * 
     * @param label
     * 			title of the dialog box
     * @param text
     * 			text to be displayed in body of dialog box
     */
    @Override
    public void showDialogBox(String label, String text, boolean tei) {  	
    	// if this content already exists, do not add it
    	if (tabs.containsKey(label)) {
    		return;
    	}
    	
    	TabLayoutPanel tab = null;
    	if (tei) {
    		transcript_box().clear();
    		
    		String[] cont = { text };
    		String[] name = { label };
    		
    		tab = TranscriptionViewer.createTranscriptionViewer(
    				cont, name, 200, false);
    		
    		//tab.setSize(500+"px", 400+"px");
    		tab.addStyleName("TextAnnoTabPanel");
    		
    		transcript_box().add(tab);
    		transcript_box().show();
    	} else {
    		top_box().clear();
    		ScrollPanel scroll = new ScrollPanel();
    		HTML content = new HTML(text);
    		
    		/*scroll.setWidth("95%");
    		scroll.setHeight("95%");*/
    		scroll.add(content);
    		
    		tab_panel.add(scroll, label);
    		tabs.put(label, scroll);
    		
    		top_box().add(tab_panel);
    		top_box().show();
    	}
    }
    
    @Override
    public void hideDialogBox(String label, String text, boolean tei) {
    	
    	if (tei) {
    		transcript_box().hide();
    	} else {
    		ScrollPanel content = tabs.get(label);
    		if (content == null) {
    			return;
    		}
    		
    		tab_panel.remove(content);
    		tabs.remove(label);
    		
    		if (tab_panel.getWidgetCount() == 0) {
    			top_box().hide();
    		}
    	}
    }
    
    @Override
    public void selected(boolean is_selected) {
    	if (is_selected) {
    		main.addStyleName("PanelSelected");
    	} else {
    		main.removeStyleName("PanelSelected");
    	}
    }
}
