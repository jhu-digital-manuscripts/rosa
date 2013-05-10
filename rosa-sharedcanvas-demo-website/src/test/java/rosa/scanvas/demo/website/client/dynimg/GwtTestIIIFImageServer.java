package rosa.scanvas.demo.website.client.dynimg;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Some tests depend on IIIF endpoint.
 */
public class GwtTestIIIFImageServer extends GWTTestCase {

    public String getModuleName() {
        return "rosa.scanvas.demo.website.SharedCanvasDemoWebsiteJUnit";
    }

    public void testLoadMasterImage() {
        final String id = "http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif";

        delayTestFinish(10000);

        IIIFImageServer.loadMasterImage(id, new AsyncCallback<MasterImage>() {
            @Override
            public void onSuccess(MasterImage mi) {
                assertEquals(id, mi.id());
                assertEquals(5742, mi.height());
                assertEquals(3732, mi.width());
                finishTest();
            }

            @Override
            public void onFailure(Throwable err) {
                fail(err.getMessage());
                finishTest();
            }
        });
    }

    public void testParseIdentifier() {
        String id;

        id = IIIFImageServer.parseIdentifier("");
        assertNull(id);

        id = IIIFImageServer.parseIdentifier("asd;lk19xcz//asdasdfjlk");
        assertNull(id);

        id = IIIFImageServer.parseIdentifier("http://example.com/blah");
        assertNull(id);

        id = IIIFImageServer
                .parseIdentifier("http://example.com/iiif/moo/info.json");
        assertEquals("http://example.com/iiif/moo", id);

        id = IIIFImageServer
                .parseIdentifier("http://www.example.org/image-service/abcd1234/full/full/0/native.jpg");
        assertEquals("http://www.example.org/image-service/abcd1234", id);

        id = IIIFImageServer
                .parseIdentifier("http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif/full/300,/0/native.jpg");
        assertEquals(
                "http://rosetest.library.jhu.edu/iiif/rose%2FDouce195%2FDouce195.001r.tif",
                id);
    }
    
    public void testRenderToTiles() {
    	IIIFImageServer iiif_server = IIIFImageServer.instance();
		final String url = 
				"http://rosetest.library.jhu.edu/iiif/rose%2FLudwigXV7%2FLudwigXV7.014r.tif"
						+ "/full/full/0/native.jpg";
		
		final String id = iiif_server.parseIdentifier(url);
		final int width = 3816;
		final int height= 5429;
		final int tile_size = iiif_server.tileSize();

		MasterImage master = new MasterImage(id, width, height);
		WebImage[][] tiles = iiif_server.renderToTiles(master, width, height);
		
		assertNotNull(tiles);
		assertTrue(tiles.length > 0);
		assertTrue(tiles[0].length > 0);
		
		for (int row = 0; row < tiles.length; row++) {
			for (int col = 0; col < tiles[row].length; col++) {
				WebImage tile = tiles[row][col];
				
				int tile_width = tile.width();
				int tile_height = tile.height();
				
				String tile_id = iiif_server.parseIdentifier(tile.url());
				assertEquals(id, tile_id);
				
				ImageElement img = tile.getImageElement();
				int img_width = img.getWidth();
				int img_height = img.getHeight();
				/*assertEquals(tile_width, img_width);
				assertEquals(tile_height, img_height);*/
			//	System.out.println("__Tile Width,Height = [" + tile_width + ", " + tile_height + "]"
			//			+ "____ImgElement width, height = [" + img_width + ", " + img_height + "]");
				
				if ((row == tiles.length - 1) || (col == tiles[row].length - 1)) {
					continue;
				}
				// make sure all tiles are square and of the same dimensions
				// except for the last row/column
				assertEquals(tile_size, tile_width);
				assertEquals(tile_size, tile_height);
			}
		}
	}
}
