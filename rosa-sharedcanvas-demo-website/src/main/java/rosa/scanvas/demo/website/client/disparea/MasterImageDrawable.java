package rosa.scanvas.demo.website.client.disparea;

import java.util.HashMap;
import java.util.Map;

import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;

public class MasterImageDrawable implements DisplayAreaDrawable {
	private final static Image loading= new Image(/*"images/qgztP.gif"*/
			"http://jimpunk.net/Loading/wp-content/uploads/loading5.gif");
	
    private final MasterImageDisplayElement el;
    
    // String tile url -> [left,top]
    private final Map<String, int[]> pending_draws;
    
    private WebImage[][][] tile_cache;
    private DisplayArea last_area;

    public MasterImageDrawable(MasterImageDisplayElement el) {
        this.el = el;
        this.pending_draws = new HashMap<String, int[]>();
    }

    @Override
    public void draw(final Context2d context, final DisplayArea area, final OnDrawnCallback cb) {
    	if (area != last_area) {
            last_area = area;
            tile_cache = new WebImage[area.numZoomLevels()][][];
        }

        pending_draws.clear();

        WebImage[][] tiles = tile_cache[area.zoomLevel()];

        final double zoom = area.zoom();
        int width = (int) (el.baseWidth() * zoom);
        int height = (int) (el.baseHeight() * zoom);
        
        loading.setWidth(width + "px");
        loading.setHeight(height + "px");

        if (tiles == null) {
            tiles = el.imageServer().renderToTiles(el.masterImage(), width, height);
            tile_cache[area.zoomLevel()] = tiles;
        }

        int vp_left = area.viewportLeft();
        int vp_top = area.viewportTop();
        int vp_right = vp_left + area.viewportWidth();
        int vp_bottom = vp_top + area.viewportHeight();

        
        // On load draw the image if it has not been cancelled.
        
        WebImage.OnLoadCallback onload_cb = new WebImage.OnLoadCallback() {
            @Override
            public void onLoad(WebImage image) {
                int[] xy = pending_draws.get(image.url());
                
                if (xy != null) {
                    pending_draws.remove(image.url());

                    ImageElement img = image.getImageElement();

                    context.save();
                    context.translate(-area.viewportLeft(),
                            -area.viewportTop());
                    context.drawImage(img, xy[0], xy[1],
                            image.width(), image.height());
                    context.restore();
                    
                    if (pending_draws.isEmpty()) {
                    	cb.onDrawn();
                    }
                }
            }
        };
        
        for (int row = 0, top = 0; row < tiles.length; top += tiles[row++][0]
                .height()) {
            for (int col = 0, left = 0; col < tiles[row].length; left += tiles[row][col++]
                    .width()) {
                final WebImage tile = tiles[row][col];

                final int tile_left = left;
                final int tile_top = top;
                final int tile_right = tile_left + tiles[row][col].width();
                final int tile_bottom = tile_top + tiles[row][col].height();
                
                // Only load and draw tiles if they are within the viewport

                if (tile_right < vp_left || tile_left > vp_right
                        || tile_bottom < vp_top || tile_top > vp_bottom) {
                    continue;
                }

                if (tile.isViewable()) {
                    ImageElement img = tile.getImageElement();

                    context.save();
                    context.translate(-area.viewportLeft(), -area.viewportTop());
                    context.drawImage(img, tile_left, tile_top, tile.width(),
                            tile.height());
                    context.restore();
                    
                    continue;
                }

                context.save();
                context.translate(-area.viewportLeft(), -area.viewportTop());
                context.drawImage(ImageElement.as(loading.getElement()),
                		tile_left, tile_top, loading.getOffsetWidth(),
                		loading.getOffsetHeight());
                context.restore();
                
                pending_draws.put(tile.url(), new int[]{tile_left, tile_top});
                tile.makeViewable(onload_cb);
            }
        }
        
        if (pending_draws.isEmpty()) {
        	cb.onDrawn();
        }
    }
}
