package rosa.scanvas.demo.website.client.disparea;

/**
 * A DisplayElement is drawn within a bounding box on a DisplayArea.
 */

public abstract class DisplayElement {
    private final String id;
    private final int base_x, base_y;
    private final int base_width, base_height;

    private int stack_order;
    private boolean visible;

    public DisplayElement(String id, int x, int y, int width, int height) {
        this.id = id;
        this.base_x = x;
        this.base_y = y;
        this.base_width = width;
        this.base_height = height;
        this.stack_order = 0;
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

    public int stackingOrder() {
        return stack_order;
    }

    public void setStackingOrder(int order) {
        stack_order = order;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean status) {
        visible = status;
    }

    public abstract void draw();

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
