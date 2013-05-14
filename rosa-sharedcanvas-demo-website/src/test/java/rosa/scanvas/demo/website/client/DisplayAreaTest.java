package rosa.scanvas.demo.website.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.demo.website.client.disparea.ZoomLevels;

public class DisplayAreaTest {
	private final int WIDTH = 5000, HEIGHT = 5000;
	private final int VP_WIDTH = 500, VP_HEIGHT = 500;
	private final int DE_WIDTH = 50, DE_HEIGHT = 50;
	
	class DisplayElementMockImpl extends DisplayElement {
		public DisplayElementMockImpl(String id, int x, int y, int width, int height) {
			super(id, x, y, width, height);
		}
		
		public void draw() {}
	}
	
	/*@Test
	public void testZoomLevels() {
		double[] zooms = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		ZoomLevels zl = new ZoomLevels(zooms);
		
		assertNotNull(zl);
		assertTrue(zl.size() > 0);
		assertEquals(11, zl.size());
		
		ZoomLevels zoom = ZoomLevels.guess(WIDTH, HEIGHT, VP_WIDTH, VP_HEIGHT);
		assertNotNull(zoom);
		assertTrue(zoom.size() > 0);
	}
	
	@Test
	public void testDisplayAreaFields() {
		DisplayArea da = new DisplayArea(WIDTH, HEIGHT, VP_WIDTH, VP_HEIGHT);
		check_display_area_fields(da);
	}*/

/*	@Test
	public void testDisplayAreaZooms() {
		DisplayArea da = new DisplayArea(WIDTH, HEIGHT, VP_WIDTH, VP_HEIGHT);
		check_area_zooms(da);
	}
	
	@Test
	public void testFindInViewport() {
		DisplayArea da = new DisplayArea(WIDTH, HEIGHT, VP_WIDTH, VP_HEIGHT);
		da.setViewportBaseCenter(WIDTH/2, HEIGHT/2);
		// viewport center: (2500, 2500)
		// width = height = 500
		// left = 2250; top = 2250;
		// right = 2750; bottom = 2750;
		
		addCheckerboardElements(da);
		
		assertNotNull(da.iterator());
		assertTrue(da.iterator().hasNext());
		
		List<DisplayElement> de_list = da.findInViewport();
		assertNotNull(de_list);
		int orig_size = de_list.size();
		assertTrue(orig_size > 0);
		
		assertTrue(da.zoomIn());
		assertTrue(da.zoomIn());
		
		de_list = da.findInViewport();
		assertTrue(orig_size > de_list.size());
	}*/
	
/*	@Test
	public void testFindContaining() {
		DisplayArea da = new DisplayArea(WIDTH, HEIGHT, VP_WIDTH, VP_HEIGHT);
		da.setViewportBaseCenter(WIDTH/2, HEIGHT/2);
		
		addCheckerboardElements(da);
		
		for (int i=0; i<100; i++) {
			for (int j=0; j<100; j++) {
				// set test point in the middle of the tiles
				int x = i * DE_WIDTH + DE_WIDTH/2;
				int y = j * DE_HEIGHT + DE_HEIGHT/2;
				
				if ((i * 100 + j)%2 == 0) {
					assertEquals(2, da.findContaining(x, y).size());
				} else {
					assertEquals(1, da.findContaining(x, y).size());
				}
			}
		}
	}*/
	
	/**
	 * Add one dislay elements at odd index, add two display elements at
	 * even index
	 */
	private void addCheckerboardElements(DisplayArea da) {
		List<DisplayElement> elements = new ArrayList<DisplayElement>();
		// add 1 element for odd index, add 2 elements for event index
		for (int i=0; i<100; i++) {
			for (int j=0; j<100; j++) {
				DisplayElement el = new DisplayElementMockImpl(String.valueOf(i*100 + j) + "a",
						i * DE_WIDTH, j * DE_HEIGHT, DE_WIDTH, DE_HEIGHT);
				elements.add(el);
				int index = i * 100 + j;
				if (index%2 == 0) {
					DisplayElement ele = new DisplayElementMockImpl(
							String.valueOf(i*100 + j) + "b",
							i * DE_WIDTH, j * DE_HEIGHT, DE_WIDTH, DE_HEIGHT);
					elements.add(ele);
				}
			}
		}
		da.setContent(elements);
	}
	
	/**
	 * Test zoom methods of the display area
	 */
	private void check_area_zooms(DisplayArea da) {
		assertTrue(da.numZoomLevels() > 0);
		for (int i=0; i<da.numZoomLevels()-1; i++) {
			assertTrue(da.zoomIn());
			check_zoom_level(da.zoom());
		}
		assertFalse(da.zoomIn());
		assertTrue(da.atMaxZoom());
		
		for (int i=0; i<da.numZoomLevels()-1; i++) {
			assertTrue(da.zoomOut());
			check_zoom_level(da.zoom());
		}
		assertFalse(da.zoomOut());
		assertFalse(da.atMaxZoom());
	}
	
	/**
	 * Make sure a zoom level is between 0 and 1
	 */
	private void check_zoom_level(double zoom) {
		assertTrue(zoom >= 0);
		assertTrue(zoom <= 1);
	}
	
	/**
	 * Make sure that all fields are not null
	 */
	private void check_display_area_fields(DisplayArea da) {
		assertNotNull(da);
		
		assertNotNull(da.viewportBaseCenterX());
		assertNotNull(da.viewportBaseCenterY());
		assertNotNull(da.viewportWidth());
		assertNotNull(da.viewportHeight());
		assertNotNull(da.viewportBaseWidth());
		assertNotNull(da.viewportBaseHeight());
		assertNotNull(da.baseWidth());
		assertNotNull(da.baseHeight());
		assertNotNull(da.width());
		assertNotNull(da.height());
		assertNotNull(da.viewportTop());
		assertNotNull(da.viewportLeft());
		
		assertNotNull(da.zoomLevel());
		assertNotNull(da.numZoomLevels());
		assertNotNull(da.zoom());
	}
}
