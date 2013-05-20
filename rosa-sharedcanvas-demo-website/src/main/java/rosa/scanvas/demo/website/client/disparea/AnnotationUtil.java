package rosa.scanvas.demo.website.client.disparea;

import rosa.scanvas.demo.website.client.dynimg.IIIFImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.AnnotationSelector;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.Window;

/**
 * Provides methods useful when dealing with Shared Canvas Annotations
 */
public class AnnotationUtil {

    /**
     * Converts an annotation to a DisplayElement to be displayed on the canvas
     * 
     * @param ann
     *            a Shared Canvas annotation
     * @return a DisplayElement that can be drawn on an HTML canvas
     */
    public static DisplayElement annotationToDisplayElement(Annotation ann,
            Canvas canvas) {
        AnnotationBody body = ann.body();

        if (body.isImage()) {
            // only applicable if the image fills entire canvas
            IIIFImageServer iiif_server = IIIFImageServer.instance();
            String id = IIIFImageServer.parseIdentifier(body.uri());
            MasterImage master = new MasterImage(id, canvas.width(),
                    canvas.height());

            // TODO the case of images with selector

            MasterImageDisplayElement el = new MasterImageDisplayElement(ann.uri(), 0, 0, iiif_server,
                    master);
            el.setStackingOrder(5);
            el.setDrawable(new MasterImageDrawable(el));
            
            return el;
        } else if (body.isText() && isSpecificResource(ann)) {

            AnnotationSelector selector = getSelector(ann);

            if (selector.isSvgSelector() && selector.hasTextContent()) {
                String content = selector.textContent();
                int[][] coords = findCoordinates(content);
                int[] bounds = findBounds(coords);


                TextDisplayElement el = new TextDisplayElement(ann.uri(), bounds[0], bounds[1],
                        bounds[2], bounds[3], body.textContent(), coords);
                el.setStackingOrder(1);
                el.setDrawable(new TextDrawable(el));
                
                return el;
            }
        }

        return null;
    }

    /**
     * Is this annotation a specific resource, indicating that it has a
     * selector?
     * 
     * @param annotation
     */
    public static boolean isSpecificResource(Annotation annotation) {
        for (AnnotationTarget target : annotation.targets()) {
            if (target.isSpecificResource()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Parses an SVG selector to find the points that define a polygon
     * 
     * @param svgContent
     *            the text content of the SVG selector
     * @return 2D array of (x, y) coordinates
     */
    private static int[][] findCoordinates(String svgContent) {
        int points_start = svgContent.indexOf("'");
        int points_end = svgContent.lastIndexOf("'");

        String[] points = svgContent.substring(points_start + 1, points_end)
                .split(" ");

        int[][] coords = new int[points.length + 1][2];
        for (int i = 0; i < points.length; i++) {
            String[] point = points[i].split(",");

            try {
                coords[i][0] = Integer.parseInt(point[0]);
                coords[i][1] = Integer.parseInt(point[1]);
            } catch (IndexOutOfBoundsException e) {
                Window.alert("SVG selector has incorrectly formatted "
                        + "coordinates:\n" + svgContent);
            } catch (NumberFormatException e) {
                Window.alert("SVG selector has incorrectly formatted "
                        + "coordinates:\n" + svgContent);
            }
        }
        // copy the first point to close the path
        coords[points.length][0] = coords[0][0];
        coords[points.length][1] = coords[0][1];

        return coords;
    }

    /**
     * Find the top left coordinate (x,y) and width and height of a bounding box
     * that completely encloses an annotation. Used only in the case of a
     * selector.
     * 
     * @param coords
     * @return array: { left, top, width, height }
     */
    private static int[] findBounds(int[][] coords) {
        // set initial values. these do not have to be the correct points
        int left = coords[0][0];
        int top = coords[0][1];
        int right = coords[0][0];
        int bottom = coords[0][1];

        // iterate through all points to find correct points
        for (int i = 0; i < coords.length; i++) {
            if (coords[i][0] < left) {
                left = coords[i][0];
            }

            if (coords[i][0] > right) {
                right = coords[i][0];
            }

            if (coords[i][1] < top) {
                top = coords[i][1];
            }

            if (coords[i][1] > bottom) {
                bottom = coords[i][1];
            }
        }

        int width = right - left;
        int height = bottom - top;

        int[] bounds = { left, top, width, height };
        return bounds;
    }

    /**
     * Returns the first AnnotationSelector encountered in the targets list. If
     * no selector is found in the targets list, NULL is returned
     * 
     * @param annotation
     */
    private static AnnotationSelector getSelector(Annotation annotation) {
        for (AnnotationTarget target : annotation.targets()) {
            return target.hasSelector();
        }
        return null;
    }

    public static HTML nonTargetedText(Annotation ann) {
    	HTML text = new HTML();
    	
    	return text;
    }
    
}
