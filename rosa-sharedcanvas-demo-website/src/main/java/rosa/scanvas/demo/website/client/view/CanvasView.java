package rosa.scanvas.demo.website.client.view;

import java.util.HashMap;

import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.presenter.CanvasPanelPresenter;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class CanvasView extends Composite implements CanvasPanelPresenter.Display {
    private final Panel main;
    private final Label title;
    private final DisplayAreaView area_view;
    
    private DialogBox top;
    private TabLayoutPanel tab_panel;

    private HashMap<String, HTML> tabs = new HashMap<String, HTML>();
    
    public CanvasView() {
        this.main = new FlowPanel();
        this.title = new Label();

        this.area_view = new DisplayAreaView();

        main.add(title);
        main.add(area_view);

        main.setStylePrimaryName("PanelView");
        
        initWidget(main);
        
        this.top = new DialogBox(false, false);
        this.tab_panel = new TabLayoutPanel(40, Style.Unit.PX);
        top.setPopupPosition(0, 0);
        top.add(tab_panel);
        top.setText("Text Annotations");
        tab_panel.setSize(300 + "px", 150 + "px");
    }

    @Override
    public Label getLabel() {
        return title;
    }

    @Override
    public DisplayAreaView getDisplayAreaWidget() {
        return area_view;
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
    public void showDialogBox(String label, String text) {
    	HTML content = new HTML(text);
    	
    	tab_panel.add(content, label);
    	tabs.put(label, content);
    	
    	if (tab_panel.getWidgetCount() == 1) {
    		top.show();
    	}
    }
    
    @Override
    public void hideDialogBox(String label) {
    	HTML content = tabs.get(label);
    	
    	if (top == null || content == null) {
    		return;
    	}
    	
    	tab_panel.remove(content);
    	tabs.remove(label);
    	
    	if (tab_panel.getWidgetCount() == 0) {
    		top.hide();
    	}
    }
}
