package rosa.scanvas.demo.website.client.dynimg;

/**
 * Access web versions of a master image sitting on an image server.
 * 
 * Every time crop is mentioned it consists of the left, top, right, bottom (0,0
 * is top left) positions in a master image
 */
public interface ImageServer {

    /**
     * 
     * @param image
     * @param width
     *            of rendered image
     * @param height
     *            of rendered image
     * @param crop
     *            location in master image
     * 
     * @return resulting web image
     */

    WebImage render(MasterImage image, int width, int height, int... crop);

    /**
     * @param image
     * @param width
     *            of rendered image
     * @param height
     *            of rendered image
     * @param crop
     *            location in master image
     * 
     * @return url to resulting web image
     */
    String renderAsUrl(MasterImage image, int width, int height, int... crop);

    /**
     * Render image into the specified square, preserving the aspect ratio. The
     * actual width may be smaller than the given size, but the height should be
     * the same.
     * 
     * @param image
     * @param square_size
     * @param crop
     *            location in master image
     * @return resulting web image
     */

    WebImage renderToSquare(MasterImage image, int square_size, int... crop);

    /**
     * Render image into the specified width, preserving the aspect ratio.
     * 
     * @param image
     * @param render_width
     *            of rendered image
     * @param crop
     *            location in master image
     * @return resulting web image
     */

    WebImage renderToWidth(MasterImage image, int render_width, int... crop);

    /**
     * Scale a master image to a certain size and then divide it into a grid of
     * tiles. All tiles except perhaps the right most have the same width. All
     * tiles except perhaps the bottom most have the same height.
     * 
     * @param master
     * @param tiled_width
     *            width master image scaled to
     * @param tiled_height
     *            height master image scaled to
     * @return tiles
     */
    WebImage[][] renderToTiles(MasterImage master, int tiled_width,
            int tiled_height);

    /**
     * @return Maximum dimension a web image may be rendered.
     */
    int maxRenderSize();

    /**
     * @return Size of a tile when tiling.
     */
    int tileSize();
}
