package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PageTurner extends Composite implements HasClickHandlers {
    private final Grid display;
    private final ImageServer image_server;

    private int position;
    private List<Opening> openings;
    private int page_width, page_height;
    private boolean clicked_verso;

    public PageTurner(ImageServer image_server) {
        this.image_server = image_server;
        this.display = new Grid(2, 2);

        FlowPanel main = new FlowPanel();

        FlowPanel toolbar = new FlowPanel();

        final Button prev_button = new Button("Prev");
        final TextBox goto_textbox = new TextBox();
        final Button goto_button = new Button("Goto");
        final Button next_button = new Button("Next");

        next_button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (position + 1 < openings.size()) {
                    position++;
                    display(openings.get(position));
                }
            }
        });

        prev_button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (position > 0) {
                    position--;
                    display(openings.get(position));
                }
            }
        });
        
        goto_button.addClickHandler(new ClickHandler() {
        	@Override
        	public void onClick(ClickEvent event) {
        		String text = goto_textbox.getValue();
        		int new_position = findPositionOfOpening(text);
        		
        		if (text == null || new_position == -1) {
        			return;
        		}
        		
        		position = new_position;
        		display(openings.get(position));
        	}
        });
        
        goto_textbox.addKeyPressHandler(new KeyPressHandler() {
        	@Override
        	public void onKeyPress(KeyPressEvent event) {
        		if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
	        		String text = goto_textbox.getValue();
	        		int new_position = findPositionOfOpening(text);
	        		
	        		if (text == null || new_position == -1) {
	        			return;
	        		}
	        		
	        		position = new_position;
	        		display(openings.get(position));
        		}
        	}
        });

        display.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell cell = display.getCellForEvent(event);
                clicked_verso = cell.getCellIndex() == 0;
            }
        });

        toolbar.add(prev_button);
        toolbar.add(goto_textbox);
        toolbar.add(goto_button);
        toolbar.add(next_button);
        toolbar.setStylePrimaryName("CanvasToolbar");

        main.add(display);
        main.add(toolbar);

        main.setStylePrimaryName("PageTurner");

        initWidget(main);
    }

    public void setOpenings(List<Opening> openings, int page_width,
            int page_height) {
        this.position = 0;
        this.openings = openings;

        resize(page_width, page_height);
    }

    public void resize(int page_width, int page_height) {
        this.page_width = page_width;
        this.page_height = page_height - 10;

        if (openings != null) {
            display(openings.get(position));
        }
    }

    private void display(Opening opening) {
        if (opening.getVerso() == null) {
            display.setWidget(0, 0, new FlowPanel());
        } else {
            WebImage image = image_server.renderToRectangle(opening.getVerso(),
                    page_width, page_height);
            image.makeViewable();
            display.setWidget(0, 0, image);
        }

        if (opening.getVersoLabel() == null) {
            display.setWidget(1, 0, new Label());
        } else {
            display.setWidget(1, 0, new Label(opening.getVersoLabel()));
        }

        if (opening.getRecto() == null) {
            display.setWidget(0, 1, new FlowPanel());
        } else {
            WebImage image = image_server.renderToRectangle(opening.getRecto(),
                    page_width, page_height);
            image.makeViewable();
            display.setWidget(0, 1, image);
        }

        if (opening.getRectoLabel() == null) {
            display.setWidget(1, 1, new Label());
        } else {
            display.setWidget(1, 1, new Label(opening.getRectoLabel()));
        }
    }

    public boolean clickedVerso() {
        return clicked_verso;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return display.addClickHandler(handler);
    }
    
    /**
     * Find the index of the opening that contains a specified string as
     * a page label
     * 
     * @param label
     * @return the index of the opening. If no opening exists that contains
     * 			the specified string, -1 is returned
     */
    private int findPositionOfOpening(String label) {
    	for (int i = 0; i < openings.size(); i++ ) {
    		Opening op = openings.get(i);
    		if (op.getVersoLabel().equals(label) 
    				|| op.getRectoLabel().equals(label)) {
    			return i;
    		}
    	}
    	
    	return -1;
    }
}
