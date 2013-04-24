package rosa.scanvas.demo.website.client;



import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase. Using
 * <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 * 
 * If you run the tests using the Maven command line, you will have to navigate
 * with your browser to a specific url given by Maven. See
 * http://mojo.codehaus.org/gwt-maven-plugin/user-guide/testing.html for
 * details.
 */
public class GwtTestSharedCanvasDemoWebsite extends GWTTestCase {

    /**
     * Must refer to a valid module that sources this class.
     */
    public String getModuleName() {
        return "rosa.scanvas.demo.website.SharedCanvasDemoWebsiteJUnit";
    }

    public void testModule() {
        assertTrue(true);
    }

    // TODO separate testcase and tests for HistoryInfo
    public void testHistoryState() {
        PanelState home = new PanelState();
        HistoryState state;
        String token;

        state = new HistoryState(home);

        assertNotNull(state);
        assertEquals(1, state.panelStates().size());
        assertEquals(home, state.panelStates().get(0));

        token = state.toToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(state, HistoryState.parseHistoryToken(token));

        PanelState manifest = new PanelState(PanelView.MANIFEST,
                "http://example.com/moo");

        state.panelStates().add(manifest);

        assertEquals(2, state.panelStates().size());
        assertEquals(manifest, state.panelStates().get(1));

        token = state.toToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(state, HistoryState.parseHistoryToken(token));

        PanelState canvas = new PanelState(PanelView.CANVAS,
                "http://example.com/moo/seq", "http://example.com/moo/", 10);

        state.panelStates().add(canvas);

        assertEquals(3, state.panelStates().size());
        assertEquals(canvas, state.panelStates().get(2));

        token = state.toToken();

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(state, HistoryState.parseHistoryToken(token));

    }
}
