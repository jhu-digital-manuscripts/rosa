package rosa.scanvas.demo.website.client.disparea;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;

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
public class Html5DisplayAreaView extends Composite {
    private static final int OVERVIEW_SIZE = 128;

    private final Canvas canvas;
    private final Canvas overview;
    private final Context2d context;
    private DisplayArea area;
    
    private boolean locked;
    private boolean drag_may_start;
    private boolean dragging;
    private int canvas_drag_x, canvas_drag_y;
    private int overview_width, overview_height;
    
    public Html5DisplayAreaView() {
        this.canvas = Canvas.createIfSupported();
        this.overview = Canvas.createIfSupported();
        this.context = canvas.getContext2d();
        this.drag_may_start = false;
        this.dragging = false;
        this.locked = true;

        this.canvas.setStylePrimaryName("canvas");
        
        canvas.addClickHandler(new ClickHandler() {
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

                int click_x = event.getRelativeX(canvas.getElement());
                int click_y = event.getRelativeY(canvas.getElement());
                
                // Don't allow clicking outside of the canvas
                if (click_x < 0 || click_y < 0 || click_x > canvas.getOffsetWidth()
                        || click_y > canvas.getOffsetHeight()) {
                    return;
                }
                
                // transform click broswer coordinates into canvas coordinates
                click_x += area.viewportLeft();
                click_y += area.viewportTop();
               
                area.setViewportCenter(click_x, click_y);
                area.zoomIn();
                
                redraw();
            }
        });

        // pan when mouse down
        canvas.addMouseDownHandler(new MouseDownHandler() {
            public void onMouseDown(MouseDownEvent event) {
                if (locked) {
                    return;
                }

                event.preventDefault();
                event.stopPropagation();

                dragging = false;

                // Don't start dragging outside the canvas

                int x = event.getRelativeX(canvas.getElement());
                int y = event.getRelativeY(canvas.getElement());

                if (x < 0 || y < 0 || x > canvas.getOffsetWidth()
                        || y > canvas.getOffsetHeight()) {
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

        canvas.addMouseOutHandler(new MouseOutHandler() {
            public void onMouseOut(MouseOutEvent event) {
                drag_may_start = false;
                dragging = false;
            }
        });

        canvas.addMouseMoveHandler(new MouseMoveHandler() {
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

        canvas.addMouseWheelHandler(new MouseWheelHandler() {
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

        canvas.addTouchStartHandler(new TouchStartHandler() {
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

                int x = touch.getRelativeX(canvas.getElement());
                int y = touch.getRelativeY(canvas.getElement());

                if (x < 0 || y < 0 || x > canvas.getOffsetWidth()
                        || y > canvas.getOffsetHeight()) {
                    return;
                }

                dragging = false;
                drag_may_start = true;

                canvas_drag_x = touch.getClientX();
                canvas_drag_y = touch.getClientY();
            }
        });

        canvas.addTouchMoveHandler(new TouchMoveHandler() {
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

        canvas.addTouchEndHandler(new TouchEndHandler() {
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

                    int x = touch.getRelativeX(canvas.getElement());
                    int y = touch.getRelativeY(canvas.getElement());

                    if (x < 0 || y < 0 || x > canvas.getOffsetWidth()
                            || y > canvas.getOffsetHeight()) {
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

        canvas.addGestureStartHandler(new GestureStartHandler() {
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

        canvas.addGestureChangeHandler(new GestureChangeHandler() {
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

        canvas.addGestureEndHandler(new GestureEndHandler() {
            public void onGestureEnd(GestureEndEvent event) {
                event.preventDefault();
                event.stopPropagation();

                drag_may_start = false;
                dragging = false;
            }
        });

        canvas.addTouchCancelHandler(new TouchCancelHandler() {
            public void onTouchCancel(TouchCancelEvent event) {
                drag_may_start = false;
                dragging = false;
            }
        });

        initWidget(canvas);
    }

    public void lockDisplay(boolean status) {
        locked = status;
        drag_may_start = false;
        dragging = false;
    }

    public void display(DisplayArea area) {
        this.area = area;

        canvas.setPixelSize(area.viewportWidth(), area.viewportHeight());
        canvas.setCoordinateSpaceWidth(area.viewportWidth());
        canvas.setCoordinateSpaceHeight(area.viewportHeight());

        overview_width = OVERVIEW_SIZE;
        overview_height = (overview_width * area.baseHeight() / area
                .baseWidth());
        
        overview.setPixelSize(overview_width, overview_height);
        overview.setCoordinateSpaceWidth(overview_width);
        overview.setCoordinateSpaceHeight(overview_height);
        
        redraw();
    }

    // TODO Better to draw into buffer of whole area and then copy that to
    // screen?

    /**
     * Clear contents of viewport and redraw any visible display elements
     */
    public void redraw() {
        context.clearRect(0, 0, area.viewportWidth(), area.viewportHeight());
        
        for (DisplayElement el : area.findInViewport()) {
        	if (el.isVisible()) {
                el.draw();
            }
        }
        // TODO Grab overview when zoom level is 0...
    }
    
// this was originally protected, but changed to public for the test dialog box
    public Context2d context() {
        return context;
    }

    public DisplayArea area() {
        return area;
    }

    /**
     * Reset the display position and zoom.
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
