package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ThumbnailBrowser extends Composite {
    private final ScrollPanel container;
    private final FlowPanel content;
    private final FlowPanel top;
    
    private final Button zoom_in = new Button(Messages.INSTANCE.plus());
    private final Button zoom_out = new Button(Messages.INSTANCE.minus());
    
    public ThumbnailBrowser() {
    	this.top = new FlowPanel();
        this.content = new FlowPanel();
        this.container = new ScrollPanel(content);

        // Update thumbs when scrolling

        final HandlerRegistration reg1 = container
                .addScrollHandler(new ScrollHandler() {
                    public void onScroll(ScrollEvent event) {
                        displayVisibleThumbs();
                    }
                });

        // Remove handlers when widget no longer attached

        content.addAttachHandler(new AttachEvent.Handler() {
            public void onAttachOrDetach(AttachEvent event) {
                if (!event.isAttached()) {
                    reg1.removeHandler();
                }
            }
        });

        initWidget(top);
        
        top.add(zoom_out);
        top.add(zoom_in);
        top.add(container);
    }
    
    /**
     * Set the thumbnails to display.
     * 
     * @param thumbs
     */
    public void setThumbnails(List<Thumbnail> thumbs) {
        content.clear();

        for (int i = 0; i < thumbs.size(); ) {
        	Thumbnail thumb1 = thumbs.get(i++);
        	Thumbnail thumb2 = i + 1 < thumbs.size() ? thumbs.get(i++) : null;
        	
        	FlowPanel opening_panel = new FlowPanel();
        	opening_panel.addStyleName("Opening");
        	
        	if (thumb1 != null && thumb1.getStyleName().contains("Verso")) {
        		opening_panel.add(thumb1);
        	}
        	
        	if (thumb2 != null && thumb2.getStyleName().contains("Recto")) {
    			opening_panel.add(thumb2);
    		} else if ((thumb2 != null 
    				&& !thumb2.getStyleName().contains("Recto"))){
    			i--;
    		}
        	
        	if (opening_panel.getWidgetCount() > 0) {
        		content.add(opening_panel);
        	}
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                displayVisibleThumbs();
            }
        });
    }

    private void displayVisibleThumbs() {
        int left = container.getAbsoluteLeft();
        int right = left + container.getOffsetWidth();
        int top = container.getAbsoluteTop();
        int bottom = top + container.getOffsetHeight();

        // This widget may be occupying more space than is
        // viewable because an ancestor might be a ScrollPanel.

        // TODO Use scroll panel vertical scroll position
        for (int i = 0, n = content.getWidgetCount(); i < n; i++) {
        	FlowPanel opening = (FlowPanel) content.getWidget(i);
        	
        	int opening_left = opening.getAbsoluteLeft();
        	int opening_top = opening.getAbsoluteTop();
  	
        	if (opening_left >= left && opening_top >= top 
        			&& opening_left < right && opening_top < bottom) {
        		int widgets = opening.getWidgetCount();
        		
        		Thumbnail thumb_v = widgets > 0 ? 
        				(Thumbnail) opening.getWidget(0) : null;
        		Thumbnail thumb_r = widgets > 1 
        				? (Thumbnail) opening.getWidget(1) : null;
        		
        		if (thumb_v != null) {
        			thumb_v.makeViewable();
        		}
        		
        		if (thumb_r != null) {
        			thumb_r.makeViewable();
        		}
        	}
        }
    }
    
    public void resize(int width, int height) {
    	container.setWidth((width - 35) + "px");
    	container.setHeight((height - 105) + "px");
    }
    
    public HasClickHandlers getZoomInButton() {
    	return zoom_in;
    }
    
    public HasClickHandlers getZoomOutButton() {
    	return zoom_out;
    }
}
