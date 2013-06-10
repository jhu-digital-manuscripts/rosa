package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PageTurner extends Composite implements HasClickHandlers, 
		HasTouchEndHandlers {
	private static final int MIN_SWIPE_X = 30;
	private static final int MAX_SWIPE_Y = 20;
	
    private final Grid display;
    private final ImageServer image_server;
    private FlowPanel place_holder = new FlowPanel();
    private final FocusPanel focus;

    private int position;
    private List<Opening> openings;
    private int page_width, page_height;
//    private boolean clicked_verso;
    
    private int clicked_index;
    
    private boolean dragging;
    private boolean drag_may_start;
    private int drag_x, drag_y;

    public PageTurner(ImageServer image_server) {
        this.image_server = image_server;
        this.display = new Grid(2, 2);
        this.clicked_index = 0;

        FlowPanel main = new FlowPanel();

        FlowPanel toolbar = new FlowPanel();
        
        this.focus = new FocusPanel();

        final Button prev_button = new Button("Prev");
        final TextBox goto_textbox = new TextBox();
        final Button goto_button = new Button("Goto");
        final Button next_button = new Button("Next");
        
        prev_button.setEnabled(false);

        next_button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (position + 1 < openings.size()) {
                    position++;
                    display(openings.get(position));
                }
                
                if (position >= openings.size()) {
                	next_button.setEnabled(false);
                }
                
                if (position > 0 && !prev_button.isEnabled()) {
                	prev_button.setEnabled(true);
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
                
                if (position == 0) {
                	prev_button.setEnabled(false);
                }
                
                if (position < openings.size() && !next_button.isEnabled()) {
                	next_button.setEnabled(true);
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
        		
        		if (position > 0 && !prev_button.isEnabled()) {
        			prev_button.setEnabled(true);
        		}
        		
        		if (position < openings.size() && !next_button.isEnabled()) {
        			next_button.setEnabled(true);
        		}
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
	        		
	        		if (position > 0 && !prev_button.isEnabled()) {
	        			prev_button.setEnabled(true);
	        		}
	        		
	        		if (position < openings.size() && !next_button.isEnabled()) {
	        			next_button.setEnabled(true);
	        		}
        		}
        	}
        });

        display.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Cell cell = display.getCellForEvent(event);
//                clicked_verso = cell.getCellIndex() == 0;
                
                clicked_index = (cell.getCellIndex() == 0) ? 
                		openings.get(position).getVersoIndex() :
                	openings.get(position).getRectoIndex();
            }
        });
        
        focus.addTouchStartHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                event.preventDefault();
                event.stopPropagation();

                if (event.getTouches().length() != 1) {
                    return;
                }

                Touch touch = event.getTouches().get(0);

                dragging = false;
                drag_may_start = true;

                drag_x = touch.getClientX();
                drag_y = touch.getClientY();
            }
        });

        focus.addTouchMoveHandler(new TouchMoveHandler() {
            public void onTouchMove(TouchMoveEvent event) {
                event.preventDefault();
                event.stopPropagation();

                if (event.getTouches().length() != 1) {
                    drag_may_start = false;
                    dragging = false;
                    return;
                }

                if (drag_may_start) {
                    dragging = true;
                }
            }
        });

        focus.addTouchEndHandler(new TouchEndHandler() {
            public void onTouchEnd(TouchEndEvent event) {
                event.preventDefault();

                if (dragging && event.getChangedTouches().length() == 1) {
                    Touch touch = event.getChangedTouches().get(0);

                    int dx = drag_x - touch.getClientX();
                    int dy = drag_y - touch.getClientY();

                    if (Math.abs(dy) < MAX_SWIPE_Y
                            && Math.abs(dx) > MIN_SWIPE_X) {
                        if (dx > 0) {
                            //ctrl.gotoNextOpening();
                        	next_button.click();
                        } else {
                            //ctrl.gotoPreviousOpening();
                        	prev_button.click();
                        }
                    }
                }

//                drag_may_start = false;
//                dragging = false;
            }
        });

        focus.addTouchCancelHandler(new TouchCancelHandler() {
            public void onTouchCancel(TouchCancelEvent event) {
                drag_may_start = false;
                dragging = false;
            }
        });

        toolbar.add(prev_button);
        toolbar.add(next_button);
        toolbar.add(goto_textbox);
        toolbar.add(goto_button);
        toolbar.setStylePrimaryName("CanvasToolbar");

        focus.add(display);
        main.add(focus);
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

        place_holder.setHeight(page_height+"px");
        
        if (openings != null) {
            display(openings.get(position));
        }
    }
    
    private void display(final Opening opening) {
    	FocusPanel verso_panel = new FocusPanel();
    	FocusPanel recto_panel = new FocusPanel();
    	
        if (opening.getVerso() == null) {
            display.setWidget(0, 0, place_holder);
        } else {
            WebImage image = image_server.renderToRectangle(opening.getVerso(),
                    page_width, page_height);
            image.makeViewable();
            verso_panel.setWidget(image);
            
            verso_panel.addTouchEndHandler(new TouchEndHandler() {
            	public void onTouchEnd(TouchEndEvent event) {
            		if (!dragging && event.getChangedTouches().length() == 1) {
            			clicked_index = opening.getVersoIndex();
            		}
            	}
            });
            
            display.setWidget(0, 0, verso_panel);
        }

        if (opening.getVersoLabel() == null) {
            display.setWidget(1, 0, new Label());
        } else {
            display.setWidget(1, 0, new Label(opening.getVersoLabel()));
        }

        if (opening.getRecto() == null) {
            display.setWidget(0, 1, place_holder);
        } else {
            WebImage image = image_server.renderToRectangle(opening.getRecto(),
                    page_width, page_height);
            image.makeViewable();
            recto_panel.setWidget(image);
            
            recto_panel.addTouchEndHandler(new TouchEndHandler() {
            	public void onTouchEnd(TouchEndEvent event) {
            		if (!dragging && event.getChangedTouches().length() == 1) {
            			clicked_index = opening.getRectoIndex();
            		}
            	}
            });
            
            display.setWidget(0, 1, recto_panel);
        }

        if (opening.getRectoLabel() == null) {
            display.setWidget(1, 1, new Label());
        } else {
            display.setWidget(1, 1, new Label(opening.getRectoLabel()));
        }
    }

    public boolean isDragging() {
    	return dragging;
    }
/*    public boolean clickedVerso() {
        return clicked_verso;
    }*/

/*    public int getPosition() {
        return position;
    }*/
    
    public int getClickedIndex() {
    	return clicked_index;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return display.addClickHandler(handler);
    }
    
    @Override
    public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    	return focus.addTouchEndHandler(handler);
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
