package rosa.scanvas.demo.website.client.disparea;

/**
 * A DisplayElement is drawn within a bounding box on a DisplayArea.
 * Anything to be displayed on a DisplayArea must be a DisplayElement.
 */

public abstract class DisplayElement {
    private final String id;
    private final int base_x, base_y;
    private final int base_width, base_height;

    private int stack_order;
    private boolean visible;
    private DisplayAreaDrawable drawable;
    
    protected DisplayElement(String id, int x, int y, int width, int height) {
        this.id = id;
        this.base_x = x;
        this.base_y = y;
        this.base_width = width;
        this.base_height = height;
        this.stack_order = 0;
        this.visible = false;
    }
    
    public String id() {
        return id;
    }

    public int baseLeft() {
        return base_x;
    }

    public int baseTop() {
        return base_y;
    }

    public int baseWidth() {
        return base_width;
    }

    public int baseHeight() {
        return base_height;
    }

    /**
     * Returns the stacking order of this display element, or its drawing
     * priority. A lower number stacking order will be drawn after, and 
     * on top of, those elements with higher stacking order.
     */
    public int stackingOrder() {
        return stack_order;
    }

    /**
     * Sets the drawing priority of this element. Lower numbers are drawn
     * last, on top of elements with a higher order number.
     * 
     * @param order
     */
    public void setStackingOrder(int order) {
        stack_order = order;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean status) {
        visible = status;
    }

    /**
     * How a display element behaves when the mouse is clicked.
     * By default, no action is performed and FALSE is returned.
     * 
     * @param x
     * @param y
     */
    public boolean doElementAction(int x, int y) {
    	return false;
    }
    
    /**
     * Must be overridden by non-rectangular elements.
     * 
     * @param x
     * @param y
     * @return whether or not the point is contained within the element.
     */
    public boolean contains(int x, int y) {
        return x >= base_x && x <= base_x + base_width && y >= base_y
                && y <= base_y + base_height;
    }

    /**
     * Returns TRUE if any part of this element is contained inside
     * a specified rectangle.
     * 
     * @param rect_x
     * @param rect_y
     * @param rect_width
     * @param rect_height
     */
    public boolean inRectangle(int rect_x, int rect_y, int rect_width,
            int rect_height) {
        if (base_x + base_width < rect_x || base_x > rect_x + rect_width) {
            return false;
        }

        if (base_y + base_height < rect_y || base_y > rect_y + rect_height) {
            return false;
        }

        return true;
    }

    public void setDrawable(DisplayAreaDrawable drawable) {
        this.drawable = drawable;
    }
    
    public DisplayAreaDrawable drawable() {
        return drawable;
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DisplayElement)) {
            return false;
        }

        DisplayElement el = (DisplayElement) o;

        return id.equals(el.id);
    }
}
