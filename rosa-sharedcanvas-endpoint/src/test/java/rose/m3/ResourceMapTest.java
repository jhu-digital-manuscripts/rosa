package rose.m3;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Model;

// TODO Test RoseCollection separately
// TODO More real tests that trace out graph
// TODO These tests rely on http://romandelarose.org being up

public class ResourceMapTest {
    private ResourceMap resmap;
    private RosaCollection col;

    @Before
    public void setup() throws IOException {
        col = new RosaCollection("http://romandelarose.org/data/", "rose", "roman de la rose");
        resmap = new ResourceMap();
    }

    @Test
    public void testCollectionAggregation() {
        Model model = resmap.modelCollection("/rose", col);

        assertNotNull(model);
        assertTrue(model.size() > 0);
    }

    @Test
    public void testManifest() throws IOException {
        Model model = resmap.modelManifest("/rose/Douce195",
                col.findBook("Douce195"));

        assertNotNull(model);
        assertTrue(model.size() > 0);
    }

    @Test
    public void testSequence() throws IOException {
        Model model = resmap.modelReadingSequence("/rose/Douce195/sequence",
                col.findBook("Douce195"));

        assertNotNull(model);
        assertTrue(model.size() > 0);
    }

    @Test
    public void testAllAnnotations() throws IOException {
        Model model = resmap.modelAllAnnotations("/rose/Douce195/annotations",
                col.findBook("Douce195"));

        assertNotNull(model);
        assertTrue(model.size() > 0);
    }

    @Test
    public void testCanvasAnnotations() throws IOException {
        Model model = resmap.modelAllAnnotationsOfCanvas(
                "/rose/Douce195/canvas/1r/annotations",
                col.findBook("Douce195"), "1r");

        assertNotNull(model);
        assertTrue(model.size() > 0);
    }
}
