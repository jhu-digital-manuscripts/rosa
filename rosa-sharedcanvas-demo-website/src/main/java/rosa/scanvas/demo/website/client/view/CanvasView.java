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
	
	private final Panel main;
    private final Label title;
    private final DisplayAreaView area_view;
    
    private final Button zoomInButton;
    private final Button zoomOutButton;
    private final Button resetButton;
    
    private DialogBox transcript = new DialogBox(false, false);
    private DialogBox top = new DialogBox(false, false);
    private TabLayoutPanel tab_panel = new TabLayoutPanel(
    		40, Style.Unit.PX);

    private HashMap<String, ScrollPanel> tabs = new HashMap<String, ScrollPanel>();
/*    private HashMap<String, DialogBox> dialog_boxes = 
    		new HashMap<String, DialogBox>();
    private HashMap<String, Annotation> annotations = 
    		new HashMap<String, Annotation>();*/
    
    public CanvasView() {
        this.main = new FlowPanel();
        this.title = new Label();
        
        this.zoomInButton = new Button("Zoom In");
        this.zoomOutButton = new Button("Zoom Out");
        this.resetButton = new Button("Reset");
        
        this.area_view = new DisplayAreaView();

        main.add(title);
        main.add(area_view);
        main.add(zoomInButton);
        main.add(zoomOutButton);
        main.add(resetButton);

        main.setStylePrimaryName("PanelView");
        
        initWidget(main);
        
        top.setPopupPosition(0, 0);
        top.setText("Text Annotations");
        top.add(tab_panel);
        tab_panel.setSize(500+"px", 300+"px");
        
        transcript.setPopupPosition(0, 0);
        transcript.setText("Transcriptions");
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
    		transcript.clear();
    		
    		String[] cont = { text };
    		String[] name = { label };
    		
    		tab = TranscriptionViewer.createTranscriptionViewer(
    				cont, name, 200, false);
    		
    		tab.setSize(500+"px", 400+"px");
    		
    		transcript.add(tab);
    		transcript.show();
    	} else {
    		ScrollPanel scroll = new ScrollPanel();
    		HTML content = new HTML(text);
    		
    		scroll.setWidth("95%");
    		scroll.setHeight("95%");
    		scroll.add(content);
    		
    		tab_panel.add(scroll, label);
    		tabs.put(label, scroll);
    		
    		top.show();
    	}
    }
    
    @Override
    public void hideDialogBox(String label, String text, boolean tei) {
    	
    	if (tei) {
    		transcript.hide();
    	} else {
    		ScrollPanel content = tabs.get(label);
    		if (content == null) {
    			return;
    		}
    		
    		tab_panel.remove(content);
    		tabs.remove(label);
    		
    		if (tab_panel.getWidgetCount() == 0) {
    			top.hide();
    		}
    	}
    	
    }
}
