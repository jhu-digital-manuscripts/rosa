package rosa.scanvas.demo.website.client.disparea;

import java.util.HashMap;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Window;
public class MasterImageDrawable extends DisplayElement {
    private final DisplayArea area;
    private final Context2d context;
    private final ImageServer server;
    private final MasterImage master;

    private final HashMap<String, WebImage> drawQueue;
    private final WebImage[][][] tile_cache;

    public MasterImageDrawable(String id, int x, int y, Html5DisplayAreaView view,
    		ImageServer server, MasterImage master) {
        super(id, x, y, master.width(), master.height());

        this.area = view.area();
        this.context = view.context();
        this.server = server;
        this.master = master;
        this.drawQueue = new HashMap<String, WebImage>();
        this.tile_cache = new WebImage[area.numZoomLevels()][][];
    }

    @Override
    public void draw() {
    	drawQueue.clear();
        WebImage[][] tiles = tile_cache[area.zoomLevel()];

        final double zoom = area.zoom();
        int width = (int) (baseWidth() * zoom);
        int height = (int) (baseHeight() * zoom);

        if (tiles == null) {
            tiles = server.renderToTiles(master, width, height);
            tile_cache[area.zoomLevel()] = tiles;
        }

        int vp_left = area.viewportLeft();
        int vp_top = area.viewportTop();
        int vp_right = vp_left + area.viewportWidth();
        int vp_bottom = vp_top + area.viewportHeight();
        
        for (int row = 0, top = 0; row < tiles.length; top += tiles[row++][0]
                .height()) {
            for (int col = 0, left = 0; col < tiles[row].length; left += tiles[row][col++]
                    .width()) {
                final WebImage tile = tiles[row][col];
                
                final int tile_left = left;
                final int tile_top = top;
                final int tile_right = tile_left + tiles[row][col].width();
                final int tile_bottom= tile_top + tiles[row][col].height();
                
                if (tile_right < vp_left || tile_left > vp_right ||
                		tile_bottom < vp_top || tile_top > vp_bottom) {
                	continue;
                }
                
                if (tile.isViewable()) {
                    ImageElement img = tile.getImageElement();
                    
                    context.save();
                    context.translate(-area.viewportLeft(), -area.viewportTop());
                    context.translate(baseLeft() * zoom, baseTop() * zoom);
                    context.drawImage(img, tile_left, tile_top, 
                    		tile.width(), tile.height());
                    context.restore();
                    continue;
                }
                
                drawQueue.put(tile.url(), tile);
                
                tile.makeViewable(new WebImage.OnLoadCallback() {
                    @Override
                    public void onLoad() {
                    	if (drawQueue.containsKey(tile.url())) {
	                        ImageElement img = tile.getImageElement();
	                        
	                        context.save();
	                        context.translate(-area.viewportLeft(), -area.viewportTop());
	                        context.translate(baseLeft() * zoom, baseTop() * zoom);
	                        // TODO Only draw if in viewport
	                        context.drawImage(img, tile_left, tile_top, 
	                        		tile.width(), tile.height());
	                        drawQueue.remove(tile.url());
	                        context.restore();
                    	}
                    }
                });
            }
        }
    }
}
