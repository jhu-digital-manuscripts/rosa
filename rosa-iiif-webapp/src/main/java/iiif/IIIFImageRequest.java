package iiif;

public class IIIFImageRequest {
    private String image;
    private ImageFormat format;
    private Size size;
    private Region region;
    private Quality quality;
    private double rotation;

    public IIIFImageRequest() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ImageFormat getFormat() {
        return format;
    }

    public void setFormat(ImageFormat format) {
        this.format = format;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size scale) {
        this.size = scale;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
        result = prime * result + ((quality == null) ? 0 : quality.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rotation);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((size == null) ? 0 : size.hashCode());
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
        IIIFImageRequest other = (IIIFImageRequest) obj;
        if (format != other.format)
            return false;
        if (image == null) {
            if (other.image != null)
                return false;
        } else if (!image.equals(other.image))
            return false;
        if (quality != other.quality)
            return false;
        if (region == null) {
            if (other.region != null)
                return false;
        } else if (!region.equals(other.region))
            return false;
        if (Double.doubleToLongBits(rotation) != Double
                .doubleToLongBits(other.rotation))
            return false;
        if (size == null) {
            if (other.size != null)
                return false;
        } else if (!size.equals(other.size))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IIIFImageRequest [image=" + image + ", format=" + format
                + ", scale=" + size + ", region=" + region + ", quality="
                + quality + ", rotation=" + rotation + "]";
    }

}
