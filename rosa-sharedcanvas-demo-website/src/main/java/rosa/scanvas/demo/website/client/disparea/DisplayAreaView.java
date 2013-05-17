package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
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
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.Window;
/**
 * Display the viewport of a display area using a HTML 5 canvas.
 */
public class DisplayAreaView extends Composite {
	private static final int OVERVIEW_SIZE = 128;
	private AbsolutePanel top = new AbsolutePanel();
	
	private final Canvas viewport;
	private final Canvas overview;
	private final Context2d viewport_context;
	private DisplayArea area;

	private boolean locked;
	private boolean drag_may_start;
	private boolean dragging;
	private int canvas_drag_x, canvas_drag_y;
	private int overview_x, overview_y;
	private boolean grab_overview;

	public DisplayAreaView() {
		this.viewport = Canvas.createIfSupported();
		this.overview = Canvas.createIfSupported();
		this.viewport_context = viewport.getContext2d();
		this.drag_may_start = false;
		this.dragging = false;
		this.locked = true;
		this.grab_overview = false;

		top.add(viewport);
		top.add(overview, overview_x, overview_y);
		
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

				int click_x = event.getRelativeX(viewport.getElement());
				int click_y = event.getRelativeY(viewport.getElement());

				// Don't allow clicking outside of the canvas
				if (click_x < 0 || click_y < 0
						|| click_x > viewport.getOffsetWidth()
						|| click_y > viewport.getOffsetHeight()) {
					return;
				}

				// transform click broswer coordinates into canvas coordinates
				click_x += area.viewportLeft();
				click_y += area.viewportTop();
// --------- DisplayElement.contains(x, y) method testing -----------------------               
				for (DisplayElement el : area.findInViewport()) {
					int el_x = (int) (click_x / area.zoom());
					int el_y = (int) (click_y / area.zoom());

					if (!(el instanceof MasterImageDisplayElement)
							&& el.contains(el_x, el_y)) {
						Window.alert("Element " + el.id() + " contains point ("
								+ click_x + ", " + click_y + ")");
					}
				}
// -------------------------------------------------------------------------------                
				area.setViewportCenter(click_x, click_y);
				area.zoomIn();

				redraw();
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
				} else {
					drag_may_start = false;
				}
			}
		});

		viewport.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
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

				dragging = false;
				drag_may_start = true;

				canvas_drag_x = touch.getClientX();
				canvas_drag_y = touch.getClientY();
			}
		});

		viewport.addTouchMoveHandler(new TouchMoveHandler() {
			public void onTouchMove(TouchMoveEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				if (event.getTouches().length() != 1) {
					drag_may_start = false;
					dragging = false;
					return;
				}

				Touch touch = event.getTouches().get(0);

				if (drag_may_start) {
					dragging = true;
				}

				if (dragging) {
					int dx = canvas_drag_x - touch.getClientX();
					int dy = canvas_drag_y - touch.getClientY();

					canvas_drag_x = touch.getClientX();
					canvas_drag_y = touch.getClientY();

					pan(dx, dy);
				}
			}
		});

		viewport.addTouchEndHandler(new TouchEndHandler() {
			public void onTouchEnd(TouchEndEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (locked) {
					return;
				}

				if (event.getChangedTouches().length() == 1 && !dragging) {
					// click

					Touch touch = event.getChangedTouches().get(0);
					// Don't allow click outside the canvas

					int x = touch.getRelativeX(viewport.getElement());
					int y = touch.getRelativeY(viewport.getElement());

					if (x < 0 || y < 0 || x > viewport.getOffsetWidth()
							|| y > viewport.getOffsetHeight()) {
						return;
					}

					area.setViewportCenter(x, y);
					area.zoomIn();
					redraw();
				}

				drag_may_start = false;
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

		bind_overview();
		
		initWidget(top);
	}

	/**
	 * Add event handlers to the overview
	 */
	private void bind_overview() {
		overview.addClickHandler(new ClickHandler() {
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

				int click_x = event.getRelativeX(overview.getElement());
				int click_y = event.getRelativeY(overview.getElement());

				// Don't allow clicking outside of the canvas
				if (click_x < 0 || click_y < 0
						|| click_x > overview.getOffsetWidth()
						|| click_y > overview.getOffsetHeight()) {
					return;
				}

				// transform click broswer coordinates into overview coordinates
				click_x *= area.width() / overview.getCoordinateSpaceWidth();
				click_y *= area.height() / overview.getCoordinateSpaceHeight();
				
				area.setViewportCenter(click_x, click_y);
				redraw();
			}
		});
		
		overview.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				event.preventDefault();
				event.stopPropagation();

				if (drag_may_start) {
					dragging = true;
				}

				if (dragging) {
					int dx = event.getClientX() - canvas_drag_x;
					int dy = event.getClientY() - canvas_drag_y;

					canvas_drag_x = event.getClientX();
					canvas_drag_y = event.getClientY();

					dx *= area.width() / overview.getCoordinateSpaceWidth();
					dy *= area.height() / overview.getCoordinateSpaceHeight();

					pan(dx, dy);
				}
			}
		});

		overview.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				event.stopPropagation();

				dragging = false;

				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					drag_may_start = true;
					canvas_drag_x = event.getClientX();
					canvas_drag_y = event.getClientY();
					
					int click_x = event.getRelativeX(overview.getElement())
							* area.width() / overview.getCoordinateSpaceWidth();
					int click_y = event.getRelativeY(overview.getElement())
							* area.height() / overview.getCoordinateSpaceHeight();
					
					area.setViewportCenter(click_x, click_y);
					//redraw();
				} else {
					drag_may_start = false;
				}
			}
		});

		overview.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) {
				drag_may_start = false;
				dragging = false;
			}
		});

		overview.addMouseOutHandler(new MouseOutHandler() {
			public void onMouseOut(MouseOutEvent event) {
				drag_may_start = false;
				dragging = false;
			}
		});

/*		overview.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
			}
		});*/

		overview.addTouchStartHandler(new TouchStartHandler() {
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();


				if (event.getTouches().length() != 1) {
					drag_may_start = false;
					dragging = false;
					return;
				}

				Touch touch = event.getTouches().get(0);

				dragging = false;
				drag_may_start = true;
				canvas_drag_x = touch.getClientX();
				canvas_drag_y = touch.getClientY();
			}
		});

		overview.addTouchMoveHandler(new TouchMoveHandler() {
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

				Touch touch = event.getTouches().get(0);

				if (dragging) {
					int dx = touch.getClientX() - canvas_drag_x;
					int dy = touch.getClientY() - canvas_drag_y;

					canvas_drag_x = touch.getClientX();
					canvas_drag_y = touch.getClientY();

					dx *= area.width() / overview.getCoordinateSpaceWidth();
					dy *= area.height() / overview.getCoordinateSpaceHeight();

					pan(dx, dy);
				}
			}
		});

		overview.addTouchEndHandler(new TouchEndHandler() {
			public void onTouchEnd(TouchEndEvent event) {
				event.preventDefault();
				event.stopPropagation();

				drag_may_start = false;
				dragging = false;
			}
		});
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

		// TODO 1px border
		overview_x = area.viewportWidth() - overview_width;
		overview_y = area.viewportHeight() - overview_height;
		grab_overview = false;

		top.setWidgetPosition(overview, overview_x, overview_y);
		redraw();
	}

	/**
	 * Create the overview
	 */
	private void draw_overview() {
		// reset view, so it is centered in viewport at zoom 0
		Context2d overview_context = overview.getContext2d();
		int current_zoom = area.zoomLevel();
		int current_base_center_x = area.viewportBaseCenterX();
		int current_base_center_y = area.viewportBaseCenterY();

		int width = overview.getCoordinateSpaceWidth();
		int height = overview.getCoordinateSpaceHeight();

		double width_scale = (double) width / area.width();
		double zoom = area.zoom() * width_scale;

		// do not redraw overview on this step
		grab_overview = false;
		area.setZoomLevel(0);
		resetDisplay();

		overview_context.clearRect(0, 0, width, height);
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

		// reset view so it is at the correct center and zoom
		area.setZoomLevel(current_zoom);
		area.setViewportBaseCenter(current_base_center_x,
				current_base_center_y);

		overview_context.setGlobalAlpha(0.3);
		overview_context.setFillStyle("blue");
		overview_context.fillRect(area.viewportBaseLeft() * zoom,
				area.viewportBaseTop() * zoom,
				area.viewportBaseWidth() * zoom,
				area.viewportBaseHeight() * zoom);

		overview_context.setGlobalAlpha(1.0);
		overview_context.setFillStyle("black");
	}

	/**
	 * Clear contents of viewport and redraw any visible display elements
	 */
	public void redraw() {
		// Grab overview on redraw
		if (grab_overview && area.zoomLevel() > 0) {
			draw_overview();
		}

		// Draw all visible display elements
		viewport_context.clearRect(0, 0, area.viewportWidth(), area.viewportHeight());
		for (DisplayElement el : area.findInViewport()) {
			if (el.isVisible()) {
				el.drawable().draw(viewport_context, area);
			}
		}

		// Draw overview if needed
		if (area.zoomLevel() == 0) {
			overview.setVisible(false);
		} else if (area.zoomLevel() > 0) {
			overview.setVisible(true);
		}

		grab_overview = true;
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
