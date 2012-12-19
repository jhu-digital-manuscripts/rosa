package iiif;

public interface ImageServer {
    public String constructURL(IIIFImageRequest req) throws IIIFException;

    public ImageInfo lookupImage(String image) throws IIIFException;

    int compliance();
}
