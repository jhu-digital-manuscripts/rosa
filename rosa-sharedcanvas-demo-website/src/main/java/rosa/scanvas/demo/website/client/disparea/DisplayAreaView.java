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
                
                for (DisplayElement el : area.findInViewport()) {
                	int el_x = (int) (click_x / area.zoom());
                    int el_y = (int) (click_y / area.zoom());
                    
                	if (!(el instanceof MasterImageDisplayElement)
                			&& el.contains(el_x, el_y)) {
                		Window.alert("Element " + el.id() + " contains point ("
                				+ click_x + ", " + click_y + ")");
                	}
                }
                
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

        initWidget(viewport);
    }

    public void lockDisplay(boolean status) {
        locked = status;
        drag_may_start = false;
        dragging = false;
    }

    public void display(DisplayArea area) {
        this.area = area;

        viewport.setPixelSize(area.viewportWidth(), area.viewportHeight());
        viewport.setCoordinateSpaceWidth(area.viewportWidth());
        viewport.setCoordinateSpaceHeight(area.viewportHeight());

        // TODO hack, must be at zoom level 0 to grab overview

        area.setZoomLevel(0);

        // TODO problem is that viewport contains whole canvas at zoom level 0
        // somewhere
        // Could disable panning at zoom 0 and figure out where canvas is...

        int overview_width = OVERVIEW_SIZE;
        int overview_height = (overview_width * area.baseHeight())
                / area.baseWidth();

        overview.setPixelSize(overview_width, overview_height);
        overview.setCoordinateSpaceWidth(overview_width);
        overview.setCoordinateSpaceHeight(overview_height);

        // TODO 1px border
        overview_x = area.viewportWidth() - overview_width;
        overview_y = area.viewportHeight() - overview_height;
        grab_overview = true;

        redraw();
    }

    /**
     * Clear contents of viewport and redraw any visible display elements
     */
    public void redraw() {
        // Grab overview when going from zoom 0 to zoom 1
        if (grab_overview && area.zoomLevel() == 1) {
            Context2d overview_context = overview.getContext2d();

            int width = overview.getCoordinateSpaceWidth();
            int height = overview.getCoordinateSpaceHeight();

            overview_context
                    .drawImage(viewport_context.getCanvas(), 0, 0, width, height);
            overview_context.beginPath();
            overview_context.rect(0, 0, width - 1, height - 1);
            overview_context.setStrokeStyle("red");
            overview_context.stroke();
            overview_context.closePath();

            grab_overview = false;
        }

        viewport_context.clearRect(0, 0, area.viewportWidth(), area.viewportHeight());

        for (DisplayElement el : area.findInViewport()) {
            if (el.isVisible()) {
                el.drawable().draw(viewport_context, area);
            }
        }

        if (area.zoomLevel() > 0) {
            viewport_context.drawImage(overview.getCanvasElement(), overview_x,
                    overview_y);

            // Draw selection on top of overview

            int sel_left = (area.viewportLeft() * overview
                    .getCoordinateSpaceWidth()) / area.viewportWidth();
            int sel_top = (area.viewportTop() * overview
                    .getCoordinateSpaceHeight()) / area.viewportHeight();
            int sel_width = (area.viewportWidth() * overview
                    .getCoordinateSpaceWidth()) / area.width();
            int sel_height = (area.viewportHeight() * overview
                    .getCoordinateSpaceHeight()) / area.height();

            sel_left += overview_x - overview.getCoordinateSpaceWidth() / 2;
            sel_top += overview_y - overview.getCoordinateSpaceHeight() / 2;

            viewport_context.setGlobalAlpha(0.3);
            viewport_context.setFillStyle("blue");
            viewport_context.fillRect(sel_left, sel_top, sel_width, sel_height);
            viewport_context.setGlobalAlpha(1.0);
            viewport_context.setFillStyle("black");
        }
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
/*        area.setViewportBaseCenter(
                -area.viewportBaseCenterX() + area.viewportBaseWidth(),
                -area.viewportBaseCenterY() + area.viewportBaseHeight());*/
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
