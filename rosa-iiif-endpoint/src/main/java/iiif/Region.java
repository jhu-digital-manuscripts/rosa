package iiif;


public class Region {
    public enum Type {
        FULL, PERCENTAGE, ABSOLUTE;
    }

    private int x, y, width, height;
    private double px, py, pwidth, pheight;
    private Type type;

    public Region(Type type) {
        this.type = type;
    }

    public Region() {
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getPercentageX() {
        return px;
    }

    public void setPercentageX(double px) {
        this.px = px;
    }

    public double getPercentageY() {
        return py;
    }

    public void setPercentageY(double py) {
        this.py = py;
    }

    public double getPercentageWidth() {
        return pwidth;
    }

    public void setPercentageWidth(double pwidth) {
        this.pwidth = pwidth;
    }

    public double getPercentageHeight() {
        return pheight;
    }

    public void setPercentageHeight(double pheight) {
        this.pheight = pheight;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + height;
        long temp;
        temp = Double.doubleToLongBits(pheight);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pwidth);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(px);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(py);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + width;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Region other = (Region) obj;
        if (height != other.height)
            return false;
        if (Double.doubleToLongBits(pheight) != Double
                .doubleToLongBits(other.pheight))
            return false;
        if (Double.doubleToLongBits(pwidth) != Double
                .doubleToLongBits(other.pwidth))
            return false;
        if (Double.doubleToLongBits(px) != Double.doubleToLongBits(other.px))
            return false;
        if (Double.doubleToLongBits(py) != Double.doubleToLongBits(other.py))
            return false;
        if (type != other.type)
            return false;
        if (width != other.width)
            return false;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Region [x=" + x + ", y=" + y + ", width=" + width + ", height="
                + height + ", px=" + px + ", py=" + py + ", pwidth=" + pwidth
                + ", pheight=" + pheight + ", type=" + type + "]";
    }
}
