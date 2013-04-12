package rosa.scanvas.demo.website.client.disparea;

/**
 * Zoom levels range from 0 to size - 1. The maximum zoom (size - 1) should be
 * 100%.
 */
public class ZoomLevels {
    private final double[] zooms;

    /**
     * Produce zoom levels such that level 0 is the size of the viewport and the
     * increments are reasonable up to 100%.
     * 
     * @param area_width
     * @param area_height
     * @param viewport_width
     * @param viewport_height
     * @return
     */
    public static ZoomLevels guess(int area_width, int area_height,
            int viewport_width, int viewport_height) {
        double zoom_increment_guess = 0.2;
        double zoom_level_0 = (double) viewport_height / area_height;

        if (zoom_level_0 * area_width > viewport_width) {
            zoom_level_0 = (double) viewport_width / area_width;
        }

        if (zoom_level_0 > 1.0) {
            zoom_level_0 = 1.0;
        }

        int num_zooms = (int) ((1.0 - zoom_level_0) / zoom_increment_guess);

        if (num_zooms == 0) {
            num_zooms = 1;
        }

        double[] zooms = new double[num_zooms];

        zooms[0] = zoom_level_0;
        zooms[zooms.length - 1] = 1.0;

        for (int i = 1; i < zooms.length - 1; i++) {
            // TODO linear scaling not user friendly?
            zooms[i] = ((1.0 - zoom_level_0) * (i + 1)) / (num_zooms);
        }

        return new ZoomLevels(zooms);
    }

    public ZoomLevels(double... zooms) {
        this.zooms = zooms;
    }

    public int size() {
        return zooms.length;
    }

    public double zoom(int level) {
        return zooms[level - 1];
    }
}
