package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class HomePanelPresenter implements PanelPresenter {
    // TODO Change this to have list of collections

    public interface Display extends IsWidget {
        Anchor getRoseDataLink();

        Anchor getTestDataLink();

        HasClickHandlers getGoButton();

        HasValue<String> getUserUrlText();

        HasKeyUpHandlers getUserUrlKeyUpHandlers();

        void resize(int width, int height);
    }

    private final Display display;
    private final HandlerManager eventBus;
    private final int panel_id;

    public HomePanelPresenter(Display display, HandlerManager eventBus,
            int panel_id) {
        this.display = display;
        this.eventBus = eventBus;
        this.panel_id = panel_id;

        display.getRoseDataLink().setHTML(
                "<i>Roman de la Rose</i> Digital library");
        display.getTestDataLink().setText("Collection of test data");

        bind();
    }

    private void bind() {
        display.getRoseDataLink().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent click_event) {
                PanelState state = new PanelState(
                        PanelView.MANIFEST_COLLECTION,
                        "http://rosetest.library.jhu.edu/sc");
                PanelRequestEvent event = new PanelRequestEvent(
                        PanelRequestEvent.PanelAction.CHANGE, panel_id, state);
                eventBus.fireEvent(event);
            }
        });

        display.getGoButton().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doGoClick();
            }
        });

        display.getUserUrlKeyUpHandlers().addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    doGoClick();
                }
            }
        });
    }

    private void doGoClick() {
        // TODO the case of real data: when someone puts in a real URL
        // data will be read from URL, then it must be determined if it is a
        // collection of manifests
        // or a single manifest
        // String url = display.getUserUrlText().getValue();
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    @Override
    public void display(PanelData data) {
    }

    @Override
    public void resize(int width, int height) {
        display.resize(width, height);
    }
}
