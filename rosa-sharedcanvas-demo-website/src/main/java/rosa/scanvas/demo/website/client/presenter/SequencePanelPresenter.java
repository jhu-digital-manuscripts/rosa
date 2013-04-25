package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelState;
import rosa.scanvas.demo.website.client.PanelView;
import rosa.scanvas.demo.website.client.dynimg.IIIFImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.dynimg.WebImage;
import rosa.scanvas.demo.website.client.event.PanelDisplayedEvent;
import rosa.scanvas.demo.website.client.event.PanelRequestEvent;
import rosa.scanvas.demo.website.client.widgets.PageTurnerWidget;
import rosa.scanvas.demo.website.client.widgets.Thumbnail;
import rosa.scanvas.demo.website.client.widgets.ThumbnailBrowser;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class SequencePanelPresenter implements PanelPresenter {

    // TODO
    private static IIIFImageServer iiif_server = new IIIFImageServer();

    public interface Display extends IsWidget {
        PageTurnerWidget getPageTurnerWidget();

        ThumbnailBrowser getThumbnailBrowser();

        void setSelectedTab(int index);

        HasSelectionHandlers<Integer> getTabPanelSelector();

        void resize(int width, int height);
    }

    private final HandlerManager eventBus;
    private final Display display;
    private final int panel_id;
    private int thumb_size;
    private int tab;

    private PanelData data;
    private int canvasIndex;
    private Canvas[] canvas = new Canvas[2];

    public SequencePanelPresenter(Display display, HandlerManager eventBus,
            int panel_id) {
        this.eventBus = eventBus;
        this.display = display;
        this.panel_id = panel_id;
        this.thumb_size = 128;

        bind();
    }

    /**
     * Add handlers to listen for DOM events
     */
    private void bind() {
        display.getPageTurnerWidget().getPrevButton()
                .addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        doPrevious();
                    }
                });

        display.getPageTurnerWidget().getNextButton()
                .addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        doNext();
                    }
                });

        display.getPageTurnerWidget().getJumpButton()
                .addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        doJump();
                    }
                });
    }

    /**
     * Once the thumbnail data has been set, add handlers to listen for DOM
     * events
     */
    private void bindThumbnails(List<Thumbnail> thumbs) {
        for (final Thumbnail thumb : thumbs) {
            thumb.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    gotoCanvasView(display.getThumbnailBrowser()
                            .getThumbnailIndex(thumb));
                }
            });
        }
    }

    private void gotoCanvasView(int canvas_index) {
        PanelState state = new PanelState(PanelView.CANVAS, data.getSequence()
                .uri(), data.getManifest().uri(), canvas_index);
        PanelRequestEvent event = new PanelRequestEvent(
                PanelRequestEvent.PanelAction.CHANGE, panel_id, state);
        eventBus.fireEvent(event);
    }

    private void bindPageTurner(final PanelData data) {
        FlowPanel main = display.getPageTurnerWidget().getCanvasDisplayPanel();

        FocusPanel panel = (FocusPanel) main.getWidget(0);
        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doPageTurnerClick(data, 0);
            }
        });

        panel = (FocusPanel) main.getWidget(1);
        panel.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doPageTurnerClick(data, 1);
            }
        });
    }

    /**
     * Set data in Page Turner to the next opening
     */
    private void doNext() {
        canvasIndex += 2;
        canvas[0] = data.getSequence().canvas(canvasIndex - 1);
        canvas[1] = data.getSequence().canvas(canvasIndex);

        display.getPageTurnerWidget().setData(canvas,
                data.getImageAnnotations());
    }

    /**
     * Set data in Page Turner to the previous opening
     */
    private void doPrevious() {

    }

    /**
     * Set data in Page Turner to the opening specified in the text box
     */
    private void doJump() {
    }

    private void doPageTurnerClick(final PanelData data, int index) {

    }

    // ------------------------------------------------

    /**
     * Returns the index of the canvas of the first page with label 1r, or 001r,
     * etc. If no such canvas exists, returns NULL.
     */
    private int findFirstPage() {
        Iterator<Canvas> it = data.getSequence().iterator();
        int index = 0;

        while (it.hasNext()) {
            Canvas canv = it.next();

            if (canv.label().equals("1r") || canv.label().equals("001r")) {
                return index;
            }
            index++;
        }
        return -1;
    }

    private void setPageTurner() {

        if (canvas[0] != null) {

        } else {
            canvasIndex = findFirstPage();
            if (canvasIndex > 0) {
                canvas[0] = data.getSequence().canvas(canvasIndex - 1);
                canvas[1] = data.getSequence().canvas(canvasIndex);

                display.getPageTurnerWidget().setData(canvas,
                        data.getImageAnnotations());
            }
        }
    }

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    private List<Thumbnail> construct_thumbs(Sequence sequence,
            List<Annotation> annotations) {
        List<Thumbnail> result = new ArrayList<Thumbnail>();

        // canvas uri -> annotation
        // TODO maybe panel data should contain various uri mappings...
        Map<String, Annotation> targets = new HashMap<String, Annotation>();

        for (Annotation a : annotations) {
            // TODO favor image annotations of the whole canvas

            for (AnnotationTarget at : a.targets()) {
                if (!at.isSpecificResource()) {
                    targets.put(at.uri(), a);
                }
            }
        }

        for (Canvas canvas : sequence) {
            Annotation a = targets.get(canvas.uri());

            if (a != null) {
                MasterImage master = as_master_image(a, canvas);
                WebImage image = iiif_server.renderToSquare(master, thumb_size);
                Thumbnail thumb = new Thumbnail(image, a.label());
                result.add(thumb);
            }
        }

        return result;
    }

    // Assume annotation is image covering whole canvas using iiif
    private MasterImage as_master_image(Annotation a, Canvas canvas) {
        String id = IIIFImageServer.parseIdentifier(a.body().uri());
        return new MasterImage(id, canvas.width(), canvas.height());
    }

    @Override
    public void display(PanelData data) {
        this.data = data;

        if (tab == 0) {
            List<Thumbnail> thumbs = construct_thumbs(data.getSequence(),
                    data.getImageAnnotations());
            display.getThumbnailBrowser().setThumbnails(thumbs);
            bindThumbnails(thumbs);
        } else if (tab == 1) {
            setPageTurner();
            bindPageTurner(data);
        }

        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        eventBus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        // TODO scale thumb size

        display.resize(width, height);
    }
}
