package rosa.scanvas.demo.website.client.disparea;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

public class MasterImageDrawable extends DisplayElement {
    private final DisplayView view;
    private final ImageServer server;
    private final MasterImage master;

    private final WebImage[][][] tile_cache;

    public MasterImageDrawable(String id, int x, int y, DisplayView view,
            ImageServer server, MasterImage master) {
        super(id, x, y, master.width(), master.height());

        this.view = view;
        this.server = server;
        this.master = master;
        this.tile_cache = new WebImage[view.area().numZoomLevels()][][];
    }

    @Override
    public void draw() {
        DisplayArea area = view.area();

        WebImage[][] tiles = tile_cache[area.zoomLevel()];

        double zoom = area.zoom();
        int width = (int) (baseWidth() * zoom);
        int height = (int) (baseHeight() * zoom);

        if (tiles == null) {
            tiles = server.renderToTiles(master, width, height);
            tile_cache[area.zoomLevel()] = tiles;
        }

        Context2d context = view.context();

        context.save();
        context.translate(baseLeft() * zoom, baseTop() * zoom);

        for (int row = 0, top = 0; row < tiles.length; top += tiles[row++][0]
                .height()) {
            for (int col = 0, left = 0; col < tiles[row].length; left += tiles[row][col++]
                    .width()) {
                WebImage tile = tiles[row][col];

                // TODO have to wait for image load, only do this when need to
                // Probably won't load because not attached
                tile.makeViewable();

                ImageElement img = tile.getImageElement();

                // Only draw if in viewport
                context.drawImage(img, left, top);
            }
        }

        context.restore();
    }
}
