package rosa.scanvas.demo.website.client.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.disparea.AnnotationUtil;
import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.dom.client.Touch;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public class PageTurner extends Composite implements HasClickHandlers, 
		HasTouchEndHandlers {
	
	public interface NewOpeningCallback {
		void onNewOpening(PanelData data);
	}
	
	private static final int MIN_SWIPE_X = 30;
	private static final int MAX_SWIPE_Y = 20;
	
    private final Grid display;
    private FlowPanel place_holder = new FlowPanel();
    private final FocusPanel focus;

    private final Panel verso_panel;
    private final Panel recto_panel;
    
    private final Button prev_button;
    private final TextBox goto_textbox;
    private final Button goto_button;
    private final Button next_button;
    
    private final Button canvas_button;

    private int position;
    private List<Opening> openings;
    private int page_width, page_height;
    
    private Opening current_opening;
    
    private Sequence sequence;
    
    private int clicked_index;
    
    private boolean dragging;
    private boolean drag_may_start;
    private int drag_x, drag_y;
    
    private ArrayList<DisplayElement> verso_els;
    private ArrayList<DisplayElement> recto_els;
    private DisplayAreaView verso_view;
    private DisplayAreaView recto_view;
    private NewOpeningCallback cb;
    
    private PanelData opening_data;
    private HashSet<Integer> to_draw;

    public PageTurner() {
        this.display = new Grid(2, 2);
        this.clicked_index = 0;
        
        this.canvas_button = new Button();
        canvas_button.setVisible(false);
        
        this.verso_els = new ArrayList<DisplayElement>();
        this.recto_els = new ArrayList<DisplayElement>();
        this.verso_view = new DisplayAreaView();
        this.recto_view = new DisplayAreaView();
        
        this.verso_panel = new SimplePanel(verso_view);
        this.recto_panel = new SimplePanel(recto_view);
        
        this.prev_button = new Button(Messages.INSTANCE.prev());
        this.goto_textbox = new TextBox();
        this.goto_button = new Button(Messages.INSTANCE.gotoButton());
        this.next_button = new Button(Messages.INSTANCE.next());
        
        this.to_draw = new HashSet<Integer>();

        FlowPanel main = new FlowPanel();
        FlowPanel toolbar = new FlowPanel();
        this.focus = new FocusPanel();

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
        		
        		goto_textbox.setText("");
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
	        		
	        		goto_textbox.setText("");
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
                
                clicked_index = (cell.getCellIndex() == 0) ? 
                		openings.get(position).getVersoIndex() :
                		openings.get(position).getRectoIndex();
                		
                canvas_button.click();
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
                        	next_button.click();
                        } else {
                        	prev_button.click();
                        }
                    }
                }
            }
        });

        focus.addTouchCancelHandler(new TouchCancelHandler() {
            public void onTouchCancel(TouchCancelEvent event) {
                drag_may_start = false;
                dragging = false;
            }
        });
        
        bind_canvas(verso_view, true);
        bind_canvas(recto_view, false);
        
        toolbar.add(prev_button);
        toolbar.add(next_button);
        toolbar.add(goto_textbox);
        toolbar.add(goto_button);
        toolbar.add(canvas_button);
        toolbar.setStylePrimaryName("CanvasToolbar");

        focus.add(display);
        main.add(focus);
        main.add(toolbar);

        main.setStylePrimaryName("PageTurner");
        
        initWidget(main);
    }
    
    /**
     * Adds DOM event handlers to the DisplayAreaView of a page of an opening.
     * These handlers are used to redirect the user to the detailed canvas view.
     * 
     * @param view
     * @param is_verso
     */
    private void bind_canvas(final DisplayAreaView view, final boolean is_verso) {
    	view.addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			if (!view.isLocked()) {
	    			return;
	    		}
	    		
	    		clicked_index = is_verso ? current_opening.getVersoIndex() 
	    				: current_opening.getRectoIndex();
	    		
	    		canvas_button.click();
    		}
    	});
    	
    	view.addTouchStartHandler(new TouchStartHandler() {
            public void onTouchStart(TouchStartEvent event) {
                event.preventDefault();
                event.stopPropagation();
                
                if (event.getTouches().length() != 1
                		|| !view.isLocked()) {
                    return;
                }

                Touch touch = event.getTouches().get(0);

                dragging = false;
                drag_may_start = true;

                drag_x = touch.getClientX();
                drag_y = touch.getClientY();
            }
        });

    	view.addTouchMoveHandler(new TouchMoveHandler() {
            public void onTouchMove(TouchMoveEvent event) {
                event.preventDefault();
                event.stopPropagation();

                if (!view.isLocked()) {
                	return;
                }
                
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

    	view.addTouchEndHandler(new TouchEndHandler() {
            public void onTouchEnd(TouchEndEvent event) {
                event.preventDefault();

                if (!view.isLocked()) {
                	return;
                }
                
                if (dragging && event.getChangedTouches().length() == 1) {
                    Touch touch = event.getChangedTouches().get(0);

                    int dx = drag_x - touch.getClientX();
                    int dy = drag_y - touch.getClientY();

                    if (Math.abs(dy) < MAX_SWIPE_Y
                            && Math.abs(dx) > MIN_SWIPE_X) {
                        if (dx > 0) {
                        	next_button.click();
                        } else {
                        	prev_button.click();
                        }
                    }
                } else if (!dragging && event.getChangedTouches().length() == 1) {
                	clicked_index = is_verso ? current_opening.getVersoIndex()
            				: current_opening.getRectoIndex();
            		
            		canvas_button.click();
                }
            }
        });

    	view.addTouchCancelHandler(new TouchCancelHandler() {
            public void onTouchCancel(TouchCancelEvent event) {
                drag_may_start = false;
                dragging = false;
            }
        });
    }

    /**
     * Sets the openings. Each opening consists of a verso and/or a recto page.
     * 
     * @param sequence
     * @param openings
     * @param page_width
     * @param page_height
     * @param cb
     */
    public void setOpenings(Sequence sequence, List<Opening> openings,
    		final int page_width, final int page_height, NewOpeningCallback cb) {
        this.position = 0;
        this.openings = openings;
        this.sequence = sequence;
        this.cb = cb;

        resize(page_width, page_height);
    }

    public void resize(int page_width, int page_height) {
        this.page_width = page_width;
        this.page_height = page_height - 15;

        place_holder.setHeight( (page_height - 20) + "px");
        
        if (openings != null) {
            display(openings.get(position));
        }
    }
    
    /**
     * Displays the contents of a page on a DisplayAreaView
     */
    private void asDisplayArea(final int canvas_index, 
    		final DisplayAreaView view, final ArrayList<DisplayElement> els) {
    	PanelData canvas_data = new PanelData();
    	canvas_data.setCanvas(sequence.canvas(canvas_index));
    	
    	final double aspect = (double)canvas_data.getCanvas().width() 
    			/ canvas_data.getCanvas().height();
    	
    	final int img_width = page_width > page_height * aspect ? 
    			(int) (page_height * aspect) : page_width;
    	final int img_height = page_width > page_height * aspect ? 
    			page_height : (int) (page_width / aspect);
    	
    	PanelData.loadAnnotationLists(canvas_data.getCanvas().hasAnnotations(),
    			canvas_data, new AsyncCallback<PanelData>() {
    		@Override
    		public void onFailure(Throwable err) {
    			Window.alert(Messages.INSTANCE.errorGettingList() + err.getMessage());
    		}
    		
    		@Override
    		public void onSuccess(PanelData result) {
    			DisplayArea area = view.area();

    			area.setBaseSize(result.getCanvas().width(), result.getCanvas().height());
    			area.resizeViewport(img_width, img_height);
    			
    			// Convert all annotations to DisplayElements
    			for (AnnotationList al : result.getAnnotationLists()) {
    				for (Annotation ann : al) {
    					DisplayElement el = AnnotationUtil.annotationToDisplayElement(
    							ann, result.getCanvas());
    					if (el != null) {
    					    if (!AnnotationUtil.isSpecificResource(ann)) {
    					        result.setAnnotationStatus(ann, true);
    					        el.setVisible(true);
    					    }
    					    
    						els.add(el);
    					}
    				}
    			}
    			area.setContent(els);
    			
    			view.lockDisplay(true);
    			view.display();
    			
    			opening_data.getAnnotationLists().addAll(result.getAnnotationLists());
    			opening_data.setCanvas(result.getCanvas());

    			to_draw.remove(canvas_index);
    			if (to_draw.isEmpty()) {
    				cb.onNewOpening(opening_data);
    			}
    		}
    	});
    }
    
    private void display(final Opening opening) {
    	this.current_opening = opening;

    	opening_data = new PanelData();
    	verso_els.clear();
    	recto_els.clear();
    	
    	// Ensure that if both pages exist, add them to the to_draw queue
    	if (opening.getVerso() != null) {
    		to_draw.add(opening.getVersoIndex());
    	}
    	if (opening.getRecto() != null) {
    		to_draw.add(opening.getRectoIndex());
    	}
    	
    	if (opening.getVerso() != null) {
    		asDisplayArea(opening.getVersoIndex(), verso_view, verso_els);
    		display.setWidget(0, 0, verso_panel);
    	} else {
    		display.setWidget(0, 0, place_holder);
    	}
    	
    	if (opening.getVersoLabel() != null) {
    		display.setWidget(1, 0, new Label(opening.getVersoLabel()));
    	} else {
    		display.setWidget(1, 0, new Label());
    	}
    	
    	if (opening.getRecto() != null) {
    		asDisplayArea(opening.getRectoIndex(), recto_view, recto_els);
    		display.setWidget(0, 1, recto_panel);
    	} else {
    		display.setWidget(0, 1, place_holder);
    	}
    	
    	if (opening.getRectoLabel() != null) {
    		display.setWidget(1, 1, new Label(opening.getRectoLabel()));
    	} else {
    		display.setWidget(1, 1, new Label());
    	}
    }
    
    public void bindAnnotationCheckbox(final CheckBox checkbox, final Annotation ann) {
    	checkbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
    		public void onValueChange(ValueChangeEvent<Boolean> event) {
    			set_annotation_visible(ann, event.getValue());
    			opening_data.setAnnotationStatus(ann, event.getValue());
    		}
    	});
    }
    
    /**
     * Change the boolean status of an annotation. Both pages of an opening are checked.
     * 
     * @param ann
     * @param status
     */
    private void set_annotation_visible(Annotation ann, boolean status) {
    	if (!AnnotationUtil.isSpecificResource(ann) && ann.body().isText()) {
    		return;
    	}
    	
    	DisplayElement verso_el = verso_view.area().get(ann.uri());
    	DisplayElement recto_el = recto_view.area().get(ann.uri());
    	
    	if (verso_el != null) {
    		verso_el.setVisible(status);
    		verso_view.redraw();
    	}
    	
    	if (recto_el!= null) {
    		recto_el.setVisible(status);
    		recto_view.redraw();
    	}
    }

    public boolean isDragging() {
    	return dragging;
    }
    
    public int getClickedIndex() {
    	return clicked_index;
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
    	return canvas_button.addClickHandler(handler);
    }
    
    @Override
    public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
    	return canvas_button.addTouchEndHandler(handler);
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
    	
    	if (label.matches("\\d+")) {
    		label += "r";
    	} else if (label.matches("[a-zA-Z]\\d+")) {
    		label = label.toUpperCase() + "r";
    	}
    	
    	if (label.startsWith("00")) {
    		label = label.substring(2);
    	} else if (label.startsWith("0")) {
    		label = label.substring(1);
    	}
    	
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
