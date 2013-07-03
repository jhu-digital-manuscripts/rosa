package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * Presents a list of manifest collections which a user can select. The user can
 * entire the url to a manifest collection or a manifest directly.
 */
public class HomePanelPresenter extends BasePanelPresenter {
    public interface Display extends BasePanelPresenter.Display {
        void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler);
        
        String getSelectedCollection();

        void setData(List<String> names);

        HasClickHandlers getLoadButton();

        HasValue<String> getUserUrlText();

        HasKeyUpHandlers getUserUrlKeyUpHandlers();

        HasValue<Boolean> getUserUrlIsCollection();
    }

    private static final List<String> col_titles;
    private static final HashMap<String, String> collections;

    static {
        col_titles = new ArrayList<String>();
        col_titles.add("Roman de la Rose Digital library");
        col_titles.add("Test data");
        
        collections = new HashMap<String, String>();
        collections.put("Roman de la Rose Digital library",
        		"http://rosetest.library.jhu.edu/sc");
        collections.put("Test data", "http://rosetest.library.jhu.edu/sctest");
    }

    private final Display display;

    public HomePanelPresenter(Display display, HandlerManager eventBus,
            int panel_id) {
    	super(display, eventBus, panel_id);
        this.display = display;

        bind();

        display.setData(col_titles);
    }

    private void bind() {
    	display.addSelectionChangeEventHandler(new SelectionChangeEvent.Handler() {
    		public void onSelectionChange(SelectionChangeEvent event) {
    			doCollectionLoad(display.getSelectedCollection());
    		}
    	});

        display.getLoadButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doUserLoad();
            }
        });

        display.getUserUrlKeyUpHandlers().addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    doUserLoad();
                }
            }
        });
    }
    
    private void doCollectionLoad(String sel) {
    	if (sel == null) {
    		return;
    	}
    	
    	PanelState state = new PanelState(PanelView.MANIFEST_COLLECTION,
    			collections.get(sel));
    	PanelRequestEvent event = new PanelRequestEvent(
    			PanelRequestEvent.PanelAction.CHANGE, panelId(), state);
    	eventBus().fireEvent(event);
    }

    private void doUserLoad() {
        String url = display.getUserUrlText().getValue();

        if (url.isEmpty()) {
            return;
        }

        boolean is_col = display.getUserUrlIsCollection().getValue();

        PanelState state;

        if (is_col) {
            state = new PanelState(PanelView.MANIFEST_COLLECTION, url);
        } else {
            state = new PanelState(PanelView.MANIFEST, url);
        }

        PanelRequestEvent event = new PanelRequestEvent(
                PanelRequestEvent.PanelAction.CHANGE, panelId(), state);
        eventBus().fireEvent(event);
    }

    @Override
    public void display(int width, int height, PanelData data) {
    	super.display(width, height, data);
    	
        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }
}
