package rosa.scanvas.demo.website.client.disparea;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.GestureChangeEvent;
import com.google.gwt.event.dom.client.GestureChangeHandler;
import com.google.gwt.event.dom.client.GestureEndEvent;
import com.google.gwt.event.dom.client.GestureEndHandler;
import com.google.gwt.event.dom.client.GestureStartEvent;
import com.google.gwt.event.dom.client.GestureStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.Window;
/**
 * Display the viewport of a display area using a HTML 5 canvas.
 */
public class DisplayAreaView extends Composite {
	private static final int OVERVIEW_SIZE = 128;
	
	private final Canvas viewport;
	private final Canvas overview;
	private final Context2d viewport_context;
	private final Context2d overview_context;
	private DisplayArea area;
	
	private int current_zoom_level;
	private int current_base_center_x;
	private int current_base_center_y;

	private boolean locked;
	private boolean drag_may_start;
	private boolean dragging;
	private int canvas_drag_x, canvas_drag_y;
	private int overview_x, overview_y;
	private boolean grab_overview;
	private boolean in_overview;
	private boolean drag_from_overview;
	
	private int draw_index = 0;
	List<DisplayElement> draw_queue = new ArrayList<DisplayElement> ();

	public DisplayAreaView() {
		this.viewport = Canvas.createIfSupported();
		this.overview = Canvas.createIfSupported();
		this.viewport_context = viewport.getContext2d();
		this.overview_context = overview.getContext2d();
		this.drag_may_start = false;
		this.dragging = false;
		this.locked = true;
		this.grab_overview = false;
		this.in_overview = false;
		this.drag_from_overview = false;
		
		viewport.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				drag_may_start = false;

				if (dragging) {
					dragging = false;
					return;
				}
				
				if (drag_from_overview) {
					drag_from_overview = false;
					return;
				}

				int click_x = event.getRelativeX(viewport.getElement());
				int click_y = event.getRelativeY(viewport.getElement());

				// Don't allow clicking outside of the canvas
				if (click_x < 0 || click_y < 0
						|| click_x > viewport.getOffsetWidth()
						|| click_y > viewport.getOffsetHeight()) {
					return;
				}
				
				if (click_x > overview_x && click_y > overview_y) {
					// If click is in overview, transform to overview coordinates
					click_x = (click_x - overview_x) * area.width()
							/ overview.getCoordinateSpaceWidth();
					click_y = (click_y - overview_y) * area.height()
							/ overview.getCoordinateSpaceHeight();
					
					area.setViewportCenter(click_x, click_y);
					redraw();
				} else {
					// transform click broswer coordinates into canvas coordinates
					click_x += area.viewportLeft();
					click_y += area.viewportTop();
					
					area.setViewportCenter(click_x, click_y);
					area.zoomIn();
					redraw();
				} 

// --------- DisplayElement.contains(x, y) method testing -----------------------               
/*				for (DisplayElement el : area.findInViewport()) {
					int el_x = (int) (click_x / area.zoom());
					int el_y = (int) (click_y / area.zoom());

					if (!(el instanceof MasterImageDisplayElement)
							&& el.contains(el_x, el_y)) {
						Window.alert("Element " + el.id() + " contains point ("
								+ click_x + ", " + click_y + ")");
					}
				}*/
// -------------------------------------------------------------------------------
			}
		});

		// pan when mouse down
		viewport.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				if (locked) {
					return;
				}

				event.preventDefault();
				event.stopPropagation();

				dragging = false;

				// Don't start dragging outside the canvas

				int x = event.getRelativeX(viewport.getElement());
				int y = event.getRelativeY(viewport.getElement());

				if (x < 0 || y < 0 || x > viewport.getOffsetWidth()
						|| y > viewport.getOffsetHeight()) {
					return;
				}
				
				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					drag_may_start = true;
					canvas_drag_x = event.getClientX();
					canvas_drag_y = event.getClientY();
					
					if (x > overview_x && y > overview_y) {
						in_overview = true;
						
						x = (x - overview_x) * area.width()
								/ overview.getCoordinateSpaceWidth();
						y = (y - overview_y) * area.height()
								/ overview.getCoordinateSpaceHeight();
						
						area.setViewportCenter(x, y);
					}
				} else {
					drag_may_start = false;
				}
			}
		});

		viewport.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				drag_from_overview = false;
				drag_may_start = false;
				dragging = false;
			}
		});

		viewport.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				event.preventDefault();
				event.stopPropagation();
				
				if (locked) {
					return;
				}

				if (drag_may_start) {
					dragging = true;
				}
				
				if (dragging) {
					int dx = canvas_drag_x - event.getClientX();
					int dy = canvas_drag_y - event.getClientY();
					
					int click_x = event.getRelativeX(viewport.getElement());
					int click_y = event.getRelativeY(viewport.getElement());
					
					if ((click_x < overview_x || click_y < overview_y)
							&& in_overview) {
						drag_may_start = false;
						in_overview = false;
						dragging = false;
					}
					
					if (in_overview) {
						drag_from_overview = true;
						
						int width_scale = area.width()
								/ overview.getCoordinateSpaceWidth();
						int height_scale = area.height()
								/ overview.getCoordinateSpaceHeight();
						
						dx *= -width_scale;
						dy *= -height_scale;
					}
					
					canvas_drag_x = event.getClientX();
					canvas_drag_y = event.getClientY();

					pan(dx, dy);
				}
			}
		});

		viewport.addMouseWheelHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				drag_may_start = false;
				dragging = false;

				int v = event.getNativeEvent().getMouseWheelVelocityY();

				if (v < 0) {
					if (area.zoomIn()) {
						redraw();
					}
				} else {
					if (area.zoomOut()) {
						redraw();
					}
				}
			}
		});

		// TODO double tap to zoom out, use timeouts
		viewport.addTouchStartHandler(new TouchStartHandler() {
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				if (event.getTouches().length() != 1) {
					return;
				}

				Touch touch = event.getTouches().get(0);

				// Don't start dragging outside the canvas
				int x = touch.getRelativeX(viewport.getElement());
				int y = touch.getRelativeY(viewport.getElement());

				if (x < 0 || y < 0 || x > viewport.getOffsetWidth()
						|| y > viewport.getOffsetHeight()) {
					return;
				}
				
				//area.setViewportCenter(x, y);
				
				dragging = false;
				in_overview = false;
				drag_may_start = true;
				drag_from_overview = false;
				
				canvas_drag_x = touch.getClientX();
				canvas_drag_y = touch.getClientY();
				
				if (x > overview_x && y > overview_y) {
					in_overview = true;
					drag_may_start = false;
					
					x = (x - overview_x) * area.width()
							/ overview.getCoordinateSpaceWidth();
					y = (y - overview_y) * area.height()
							/ overview.getCoordinateSpaceHeight();
					
					area.setViewportCenter(x, y);
				}
			}
		});

		viewport.addTouchMoveHandler(new TouchMoveHandler() {
			public void onTouchMove(TouchMoveEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}
				
				Touch touch = event.getTouches().get(0);
				
				int dx = canvas_drag_x - touch.getClientX();
				int dy = canvas_drag_y - touch.getClientY();
				
				int touch_x = touch.getRelativeX(viewport.getElement());
				int touch_y = touch.getRelativeY(viewport.getElement());
				
				if ((touch_x < overview_x || touch_y < overview_y)
						&& in_overview) {
					in_overview = false;
					dragging = false;
				}
				
				if (drag_may_start && !in_overview) {
					dragging = true;
				}
				
				if (in_overview) {
					drag_from_overview = true;
					
					int width_scale = area.width()
							/ overview.getCoordinateSpaceWidth();
					int height_scale = area.height()
							/ overview.getCoordinateSpaceHeight();
					
					dx *= -width_scale;
					dy *= -height_scale;
				}
				
				canvas_drag_x = touch.getClientX();
				canvas_drag_y = touch.getClientY();

				pan(dx, dy);
			}
		});

		viewport.addTouchEndHandler(new TouchEndHandler() {
			public void onTouchEnd(TouchEndEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked || event.getChangedTouches().length() != 1) {
					drag_from_overview = false;
					drag_may_start = false;
					in_overview = false;
					dragging = false;
					return;
				}
				
				Touch touch = event.getChangedTouches().get(0);
				int touch_x = touch.getRelativeX(viewport.getElement());
				int touch_y = touch.getRelativeY(viewport.getElement());
				
				if (touch_x < 0 || touch_y < 0 || touch_x > viewport.getOffsetWidth()
						|| touch_y > viewport.getOffsetHeight()) {
					return;
				}
				
				if (!dragging && !drag_from_overview && !in_overview) {
					touch_x += area.viewportLeft();
					touch_y += area.viewportTop();
					
					area.setViewportCenter(touch_x, touch_y);
					area.zoomIn();
					redraw();
				} else if (!dragging && !drag_from_overview && in_overview) {
					touch_x = (touch_x - overview_x) * area.width()
							/ overview.getCoordinateSpaceWidth();
					touch_y = (touch_y - overview_y) * area.height()
							/ overview.getCoordinateSpaceHeight();
					
					area.setViewportCenter(touch_x, touch_y);
					redraw();
				}
				
				drag_from_overview = false;
				drag_may_start = false;
				in_overview = false;
				dragging = false;
			}
		});

		viewport.addGestureStartHandler(new GestureStartHandler() {
			public void onGestureStart(GestureStartEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				drag_may_start = false;
				dragging = false;
			}
		});

		viewport.addGestureChangeHandler(new GestureChangeHandler() {
			public void onGestureChange(GestureChangeEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				drag_may_start = false;
				dragging = false;

				double scale = event.getScale();

				if (scale > 1.0) {
					if (area.zoomIn()) {
						redraw();
					}
				} else {
					if (area.zoomOut()) {
						redraw();
					}
				}
			}
		});

		viewport.addGestureEndHandler(new GestureEndHandler() {
			public void onGestureEnd(GestureEndEvent event) {
				event.preventDefault();
				event.stopPropagation();

				drag_may_start = false;
				dragging = false;
			}
		});

		viewport.addTouchCancelHandler(new TouchCancelHandler() {
			public void onTouchCancel(TouchCancelEvent event) {
				drag_may_start = false;
				dragging = false;
			}
		});
		
		initWidget(viewport);
	}

	/**
	 * Lock or unlock the display area pan/zoom controls
	 * 
	 * @param status
	 */
	public void lockDisplay(boolean status) {
		locked = status;
		drag_may_start = false;
		dragging = false;
	}

	/**
	 * Sets the display area, viewport size, and overview size
	 * 
	 * @param area
	 */
	public void display(DisplayArea area) {
		this.area = area;

		viewport.setPixelSize(area.viewportWidth(), area.viewportHeight());
		viewport.setCoordinateSpaceWidth(area.viewportWidth());
		viewport.setCoordinateSpaceHeight(area.viewportHeight());

		int overview_width = OVERVIEW_SIZE;
		int overview_height = (overview_width * area.baseHeight())
				/ area.baseWidth();

		overview.setPixelSize(overview_width, overview_height);
		overview.setCoordinateSpaceWidth(overview_width);
		overview.setCoordinateSpaceHeight(overview_height);

		overview_x = area.viewportWidth() - overview_width;
		overview_y = area.viewportHeight() - overview_height;
		grab_overview = true;
		
		// save zoom level and center position of new display area
		current_zoom_level = area.zoomLevel();
		current_base_center_x = area.viewportBaseCenterX();
		current_base_center_y = area.viewportBaseCenterY();
		// set zoom level to 0 and recenter display area in order
		// to grab overview
		resetDisplay();
	}
	
	/**
	 * Callback that is called by any drawable when it completes its draw method
	 */
	DisplayAreaDrawable.OnDrawnCallback ondrawn_cb = 
			new DisplayAreaDrawable.OnDrawnCallback() {
		@Override
		public void onDrawn() {
			if (draw_queue == null || draw_queue.size() == 0) {
				return;
			} else if ( draw_index >= draw_queue.size()) {
				if (grab_overview && area.zoomLevel() == 0) {
					get_overview_image();
				}
				
				if (area.zoomLevel() > 0) {
					draw_overview();
				}
				return;
			}
		
			DisplayElement el = draw_queue.get(draw_index++);
			el.drawable().draw(viewport_context, area, ondrawn_cb);
		}
	};
	
	/**
	 * Draw the overview
	 */
	private void draw_overview() {
		int width = overview.getCoordinateSpaceWidth();
		int height = overview.getCoordinateSpaceHeight();
		double zoom = (double) width / area.baseWidth();
	
		viewport_context.drawImage(overview.getCanvasElement(), 0, 0, 
				width, height, overview_x, overview_y, width, height);
		
		viewport_context.setGlobalAlpha(0.3);
		viewport_context.setFillStyle("blue");
		viewport_context.fillRect(area.viewportBaseLeft() * zoom + overview_x,
				area.viewportBaseTop() * zoom + overview_y,
				area.viewportBaseWidth() * zoom,
				area.viewportBaseHeight() * zoom);
		viewport_context.setGlobalAlpha(1.0);
		viewport_context.setFillStyle("black");
	}
	
	/**
	 * Get the base overview image
	 */
	private void get_overview_image() {
		grab_overview = false;
		
		int width = overview.getCoordinateSpaceWidth();
		int height = overview.getCoordinateSpaceHeight();

		overview_context.clearRect(0, 0, width, height);
		
		// TODO the source values may need tweeking
		overview_context
		.drawImage(viewport_context.getCanvas(),
				area.viewportWidth() / 2 - area.width() / 2,
				0,
				area.width(),
				area.height(),
				0, 0, width, height);
		
		overview_context.beginPath();
		overview_context.rect(0, 0, width - 1, height - 1);
		overview_context.setStrokeStyle("red");
		overview_context.stroke();
		overview_context.closePath();
		
		// change zoom level and center position back to correct values
		area.setZoomLevel(current_zoom_level);
		area.setViewportBaseCenter(current_base_center_x,
				current_base_center_y);
		redraw();
	}
	
	/**
	 * Clear contents of viewport and redraw any visible display elements
	 */
	public void redraw() {
		viewport_context.clearRect(0, 0, area.viewportWidth(), 
				area.viewportHeight());
		
		draw_index = 0;
		draw_queue.clear();
		
		// remove all the elements that are not visible
		for (DisplayElement el : area.findInViewport()) {
			if (el != null && el.isVisible()) {
				draw_queue.add(el);
			}
		}
	
		// draw the first element in the list
		if (draw_queue != null && draw_queue.size() > 0) {
			DisplayElement el = draw_queue.get(draw_index++);
			if (el.isVisible()) {
				el.drawable().draw(viewport_context, area, ondrawn_cb);
			}
		}
	}

	public DisplayArea area() {
		return area;
	}

	/**
	 * Reset the display position to the display area center and zoom level 0.
	 */
	public void resetDisplay() {
		area.setZoomLevel(0);
		area.setViewportBaseCenter(area.baseWidth() / 2, area.baseHeight() / 2);
		redraw();
	}

	public void pan(int canvas_dx, int canvas_dy) {
		area.panViewport(canvas_dx, canvas_dy);
		redraw();
	}

	public void center(int base_x, int base_y) {
		area.setViewportBaseCenter(base_x, base_y);
		redraw();
	}
}
