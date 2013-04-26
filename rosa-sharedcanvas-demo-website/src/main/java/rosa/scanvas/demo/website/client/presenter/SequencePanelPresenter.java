package rosa.scanvas.demo.website.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
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
import rosa.scanvas.demo.website.client.widgets.Opening;
import rosa.scanvas.demo.website.client.widgets.PageTurner;
import rosa.scanvas.demo.website.client.widgets.Thumbnail;
import rosa.scanvas.demo.website.client.widgets.ThumbnailBrowser;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class SequencePanelPresenter implements PanelPresenter {
    public interface Display extends IsWidget {
        PageTurner getPageTurner();

        ThumbnailBrowser getThumbnailBrowser();

        void setSelectedTab(int index);

        HasSelectionHandlers<Integer> getTabPanelSelector();

        void resize(int width, int height);
    }

    private final HandlerManager eventBus;
    private final Display display;
    private final int panel_id;
    private int page_width, page_height;
    private int thumb_size;
    private int tab;
    boolean thumb_browser_setup;
    boolean page_turner_setup;

    private PanelData data;

    public SequencePanelPresenter(Display display, HandlerManager eventBus,
            int panel_id) {
        this.eventBus = eventBus;
        this.display = display;
        this.panel_id = panel_id;
        this.thumb_size = 128;
        this.page_width = 200;
        this.page_height = 300;
        this.thumb_browser_setup = false;
        this.page_turner_setup = false;

        display.getTabPanelSelector().addSelectionHandler(
                new SelectionHandler<Integer>() {
                    @Override
                    public void onSelection(SelectionEvent<Integer> sel) {
                        tab = sel.getSelectedItem();

                        if (tab == 0) {
                            if (!page_turner_setup) {
                                setup_page_turner();
                            }
                        } else if (tab == 1) {
                            if (!thumb_browser_setup) {
                                setup_thumb_browser();
                            }
                        }
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

    @Override
    public Widget asWidget() {
        return display.asWidget();
    }

    private Map<String, Annotation> map_targets(List<Annotation> annotations) {
        // target uri -> annotation
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

        return targets;
    }

    private List<Thumbnail> construct_thumbs(Sequence sequence,
            List<Annotation> annotations) {
        List<Thumbnail> result = new ArrayList<Thumbnail>();

        IIIFImageServer iiif_server = IIIFImageServer.instance();
        Map<String, Annotation> targets = map_targets(annotations);

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

    private List<Opening> construct_openings(Sequence sequence,
            List<Annotation> annotations) {
        List<Opening> openings = new ArrayList<Opening>();

        Map<String, Annotation> targets = map_targets(annotations);

        int seq_size = sequence.size();

        for (int i = 0; i < seq_size;) {
            Canvas c1 = sequence.canvas(i++);
            Canvas c2 = i + 1 < seq_size ? sequence.canvas(i++) : null;

            Annotation a1 = targets.get(c1.uri());
            Annotation a2 = c2 == null ? null : targets.get(c2.uri());

            MasterImage verso = null;
            String verso_label = null;
            MasterImage recto = null;
            String recto_label = null;

            if (a1 != null) {
                verso = as_master_image(a1, c1);
                verso_label = a1.label();
            }

            if (a2 != null) {
                recto = as_master_image(a2, c2);
                recto_label = a2.label();
            }

            openings.add(new Opening(verso, verso_label, recto, recto_label));
        }

        return openings;
    }

    // Assume annotation is image covering whole canvas using iiif
    private MasterImage as_master_image(Annotation a, Canvas canvas) {
        String id = IIIFImageServer.parseIdentifier(a.body().uri());
        return new MasterImage(id, canvas.width(), canvas.height());
    }

    private void setup_thumb_browser() {
        List<Thumbnail> thumbs = construct_thumbs(data.getSequence(),
                data.getImageAnnotations());
        display.getThumbnailBrowser().setThumbnails(thumbs);
        bindThumbnails(thumbs);
        thumb_browser_setup = true;
    }

    private void setup_page_turner() {
        List<Opening> openings = construct_openings(data.getSequence(),
                data.getImageAnnotations());

        display.getPageTurner().setOpenings(openings, page_width, page_height);

        page_turner_setup = true;
    }

    @Override
    public void display(PanelData data) {
        this.data = data;

        thumb_browser_setup = false;
        page_turner_setup = false;

        if (tab == 0) {
            setup_page_turner();
        } else if (tab == 1) {
            setup_thumb_browser();
        }

        PanelDisplayedEvent event = new PanelDisplayedEvent(panel_id, data);
        eventBus.fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
        page_width = (width / 2) - 20;
        page_height = height - 50;

        display.getPageTurner().resize(page_width, page_height);

        // TODO scale thumb size

        display.resize(width, height);
    }
}
