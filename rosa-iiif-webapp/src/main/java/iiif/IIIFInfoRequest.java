package iiif;

public class IIIFInfoRequest {
    private String image;
    private InfoFormat format;

    public IIIFInfoRequest() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public InfoFormat getFormat() {
        return format;
    }

    public void setFormat(InfoFormat fmt) {
        this.format = fmt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((format == null) ? 0 : format.hashCode());
        result = prime * result + ((image == null) ? 0 : image.hashCode());
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
        IIIFInfoRequest other = (IIIFInfoRequest) obj;
        if (format != other.format)
            return false;
        if (image == null) {
            if (other.image != null)
                return false;
        } else if (!image.equals(other.image))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IIIFInfoRequest [image=" + image + ", format=" + format + "]";
    }
}
