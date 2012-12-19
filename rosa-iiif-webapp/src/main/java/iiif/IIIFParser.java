package iiif;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Parse a URL path info a IIIF request. If a path_prefix is given, it is
 * stripped from paths before parsing.
 */
public class IIIFParser {
    private String path_prefix;

    public IIIFParser() {
        this(null);
    }
    
    public IIIFParser(String path_prefix) {
        this.path_prefix = path_prefix;
    }

    public IIIFRequestType determineRequestType(String path) {
        if (path.endsWith("/info.xml") || path.endsWith("/info.json")) {
            return IIIFRequestType.INFO;
        } else {
            return IIIFRequestType.IMAGE;
        }
    }

    private String[] parse(String path) {
        if (path_prefix != null && path.startsWith(path_prefix)) {
            path = path.substring(path_prefix.length());
        }

        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }

        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {
            try {
                parts[i] = URLDecoder.decode(parts[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return parts;
    }

    public IIIFInfoRequest parseImageInfoRequest(String path)
            throws IIIFException {
        String[] parts = parse(path);

        if (parts.length != 2) {
            throw new IIIFException("Malformed info request: " + path);
        }

        IIIFInfoRequest req = new IIIFInfoRequest();
        req.setImage(parts[0]);

        if (parts[1].equals("info.xml")) {
            req.setFormat(InfoFormat.XML);
        } else if (parts[1].equals("info.json")) {
            req.setFormat(InfoFormat.JSON);
        } else {
            throw new IIIFException("Format not available: " + parts[1],
                    "format");
        }

        return req;
    }

    public IIIFImageRequest parseImageRequest(String path) throws IIIFException {
        String[] parts = parse(path);

        if (parts.length != 5) {
            throw new IIIFException("Malformed image request: " + path);
        }

        IIIFImageRequest req = new IIIFImageRequest();

        req.setImage(parts[0]);
        req.setRegion(parseRegion(parts[1]));
        req.setSize(parseScale(parts[2]));

        try {
            req.setRotation(Double.parseDouble(parts[3]));
        } catch (NumberFormatException e) {
            throw new IIIFException("Malformed rotation: " + e.getMessage(),
                    "rotation");
        }

        String[] last = parts[4].split("\\.");

        if (last.length != 1 && last.length != 2) {
            throw new IIIFException("Malformed image request");
        }

        req.setQuality(parseQuality(last[0]));

        if (last.length == 2) {
            req.setFormat(parseImageFormat(last[1]));
        }

        return req;
    }

    private ImageFormat parseImageFormat(String s) throws IIIFException {
        try {
            return ImageFormat.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IIIFException("Malformed image format", "format");
        }
    }

    private Size parseScale(String s) throws IIIFException {
        Size scale = new Size();

        try {

            if (s.equals("full")) {
                scale.setType(Size.Type.FULL);
                return scale;
            }

            if (s.endsWith(",")) {
                s = s.substring(0, s.length() - 1);
                scale.setType(Size.Type.EXACT_WIDTH);

                scale.setWidth(Integer.parseInt(s));
                return scale;
            }

            if (s.startsWith(",")) {
                s = s.substring(1);
                scale.setType(Size.Type.EXACT_HEIGHT);

                scale.setHeight(Integer.parseInt(s));
                return scale;
            }

            if (s.startsWith("pct:")) {
                s = s.substring(4);

                scale.setType(Size.Type.PERCENTAGE);

                scale.setPercentage(Double.parseDouble(s));

                return scale;
            }

            if (s.startsWith("!")) {
                s = s.substring(1);
                scale.setType(Size.Type.BEST_FIT);
            } else {
                scale.setType(Size.Type.EXACT);
            }

            String[] parts = s.split(",");

            if (parts.length != 2) {
                throw new IIIFException("Malformed scale", "scale");
            }

            scale.setWidth(Integer.parseInt(parts[0]));
            scale.setHeight(Integer.parseInt(parts[1]));

            return scale;
        } catch (NumberFormatException e) {
            throw new IIIFException("Malformed number: " + e.getMessage(),
                    "scale");
        }
    }

    private Quality parseQuality(String s) throws IIIFException {
        try {
            return Quality.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IIIFException("Malformed quality", "quality");
        }
    }

    private Region parseRegion(String s) throws IIIFException {
        Region region = new Region();

        try {
            if (s.equals("full")) {
                region.setType(Region.Type.FULL);
                return region;
            }

            String[] parts = s.split(",");

            if (parts.length != 4) {
                throw new IIIFException("Malformed region", "scale");
            }

            if (s.startsWith("pct:")) {
                parts[0] = parts[0].substring(4);

                region.setType(Region.Type.PERCENTAGE);
                region.setPercentageX(Double.parseDouble(parts[0]));
                region.setPercentageY(Double.parseDouble(parts[1]));
                region.setPercentageWidth(Double.parseDouble(parts[2]));
                region.setPercentageHeight(Double.parseDouble(parts[3]));
            } else {
                region.setType(Region.Type.ABSOLUTE);
                region.setX(Integer.parseInt(parts[0]));
                region.setY(Integer.parseInt(parts[1]));
                region.setWidth(Integer.parseInt(parts[2]));
                region.setHeight(Integer.parseInt(parts[3]));
            }

            return region;
        } catch (NumberFormatException e) {
            throw new IIIFException("Malformed number: " + e.getMessage(),
                    "region");
        }
    }
}
