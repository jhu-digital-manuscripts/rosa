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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.Window;

public class SequencePanelPresenter extends BasePanelPresenter {
    public interface Display extends BasePanelPresenter.Display {
        PageTurner getPageTurner();

        ThumbnailBrowser getThumbnailBrowser();

        void setSelectedTab(int index);

        HasSelectionHandlers<Integer> getTabPanelSelector();
    }

    private final Display display;
    private int page_width, page_height;
    private int thumb_size;
    private int tab;
    boolean thumb_browser_setup;
    boolean page_turner_setup;

    private PanelData data;
    List<Opening> openings;
    
    private final double step;
    private double scale;
    
    private int panel_width;
    private int panel_height;

    public SequencePanelPresenter(Display display, HandlerManager eventBus,
            int panel_id) {
    	super(display, eventBus, panel_id);
        this.display = display;
        
        this.thumb_size = 128;
        this.page_width = 200;
        this.page_height = 300;
        
        this.step = 0.1;
        this.scale = 0.1;
        
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
                            	scale = 0.4;
                                setup_thumb_browser();
                            }
                        }
                    }
                });
        
        final PageTurner turner = display.getPageTurner();
        
        turner.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	int canvas_index = turner.getClickedIndex();
                gotoCanvasView(canvas_index);
            }
        });
        
        turner.addTouchEndHandler(new TouchEndHandler() {
        	public void onTouchEnd(TouchEndEvent event) {
        		if (!turner.isDragging() 
        				&& event.getChangedTouches().length() == 1) {
        			int canvas_index = turner.getClickedIndex();
        			gotoCanvasView(canvas_index);
        		}
        	}
        });
        
        final ThumbnailBrowser browser = display.getThumbnailBrowser();
        // TODO be smarter about initial size and step size
        browser.getZoomInButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (scale + step <= 1.0) {
        			scale += step;
        			setup_thumb_browser();
        		}
        	}
        });
        
        browser.getZoomOutButton().addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
        		if (scale - step >= step) {
        			scale -= step;
        			setup_thumb_browser();
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
                	gotoCanvasView(thumb.canvasIndex());
                }
            });
        }
    }

    private void gotoCanvasView(int canvas_index) {
        PanelState state = new PanelState(PanelView.CANVAS, data.getSequence()
                .uri(), data.getManifest().uri(), canvas_index);
        PanelRequestEvent event = new PanelRequestEvent(
                PanelRequestEvent.PanelAction.CHANGE, panelId(), state);
        eventBus().fireEvent(event);
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
            for (AnnotationTarget at : a.targets()) {
                if (!at.isSpecificResource()) {
                    targets.put(at.uri(), a);
                }
            }
        }

        return targets;
    }

    /**
     * Thumbnails are generated based off of the openings in the Page Turner. This is
     * used for formatting, giving the thumbnails the look of an opening in a
     * manuscript.
     */
    private List<Thumbnail> construct_thumbs_from_openings() {
    	if (openings == null) {
    		openings = build_openings(data.getSequence(), 
    				data.getImageAnnotations());
    	}
    	
    	List<Thumbnail> result = new ArrayList<Thumbnail>();
    	IIIFImageServer iiif_server = IIIFImageServer.instance();
    	
    	for (Opening opening : openings) {
    		if (opening == null) {
    			continue;
    		}
    		
    		if (opening.getVerso() != null) {
    			WebImage image_v = iiif_server.renderToSquare(opening.getVerso(),
    				(int) (thumb_size * scale));
    			Thumbnail thumb_v = new Thumbnail(image_v, opening.getVersoLabel(),
    				opening.getVersoIndex());
    			thumb_v.addStyleName("Verso");
    			
    			result.add(thumb_v);
    		}
    		
    		if (opening.getRecto() != null) {
    			WebImage image_r = iiif_server.renderToSquare(opening.getRecto(),
    				(int) (thumb_size * scale));
    			Thumbnail thumb_r = new Thumbnail(image_r, opening.getRectoLabel(),
    				opening.getRectoIndex());
    			thumb_r.addStyleName("Recto");
    			
    			result.add(thumb_r);
    		}
    	}
    	
    	return result;
    }
    
    /**
     * From the canvases and image annotations, construct openings for
     * the manuscript consisting of two pages.  Any "openings" that consist 
     * of a single image/page are represented as an opening with only a 
     * Verso side.
     * 
     * @param sequence
     * @param annotations
     */
    private List<Opening> build_openings(Sequence sequence, 
    		List<Annotation> annotations) {
    	List<Opening> openings = new ArrayList<Opening>();
    	Map<String, Annotation> targets = map_targets(annotations);
    	int seq_size = sequence.size();
    	
        int frontmatter_pastedown = 0;
        int endmatter_pastedown = seq_size;
    	
        // Does this book have pastedown pages? This is used to determine openings
        // vs single images.
    	for (int i = 0; i < seq_size; i++) {
    		Canvas c = sequence.canvas(i);
    		Annotation a = targets.get(c.uri());
    		
    		if (a == null) {
    			continue;
    		}
    		
    		if (a.label().toLowerCase().equals("frontmatter.pastedown")) {
				frontmatter_pastedown = i;
			} else if (a.label().toLowerCase().equals("endmatter.pastedown")) {
				endmatter_pastedown = i;
			}
    	}
    	
    	// Create openings. Single images are treated as an opening with
    	// no recto.
    	for (int i = 0; i < seq_size; i++) {
        	Canvas c1 = sequence.canvas(i);
        	Annotation a1 = targets.get(c1.uri());
        	
        	if (a1 == null) {
        		continue;
        	}
        	
        	MasterImage verso = as_master_image(a1, c1);
        	MasterImage recto = null;
        	String verso_label = a1.label();
        	String recto_label = null;
        	int verso_index = i;
        	int recto_index = 0;
        	
        	if (i >= frontmatter_pastedown && i <= endmatter_pastedown
        			&& !verso_label.toLowerCase().endsWith("r")) {
        		Canvas c2 = i + 1 < seq_size ? sequence.canvas(++i) : null;
        		Annotation a2 = c2 == null ? null : targets.get(c2.uri());
        		
        		if (a2 != null) {
        			recto = as_master_image(a2, c2);
                    recto_label = a2.label();
                    recto_index = i;
        		}
        	}
        	openings.add(new Opening(verso, verso_label, verso_index, 
            		recto, recto_label, recto_index));
        }
    	
    	return openings;
    }

    /**
     * Callback that lets this presenter know when a new opening is displayed
     * in the Page Turner, so that data can be updated.
     */
    PageTurner.NewOpeningCallback opening_cb = new PageTurner.NewOpeningCallback() {
    	@Override
    	public void onNewOpening(PanelData opening) {
    		data.setCanvas(opening.getCanvas());
    		data.getAnnotationLists().clear();
    		data.getAnnotationLists().addAll(opening.getAnnotationLists());
   		
    		SequencePanelPresenter.super.display(panel_width, panel_height, data);
    	}
    };
    
    @Override
    protected void bind_annotation_checkbox(CheckBox checkbox, Annotation ann) {
    	display.getPageTurner().bindAnnotationCheckbox(checkbox, ann);
    }
    
    // Assume annotation is image covering whole canvas using iiif
    private MasterImage as_master_image(Annotation a, Canvas canvas) {
        String id = IIIFImageServer.parseIdentifier(a.body().uri());
        return new MasterImage(id, canvas.width(), canvas.height());
    }

    private void setup_thumb_browser() {
    	List<Thumbnail> thumbs = construct_thumbs_from_openings();
        display.getThumbnailBrowser().setThumbnails(thumbs);
        display.getThumbnailBrowser().resize(panel_width, panel_height
        		- display.getContextHeight());
        bindThumbnails(thumbs);
        thumb_browser_setup = true;
    }

    private void setup_page_turner() {
        openings = build_openings(data.getSequence(), data.getImageAnnotations());
        display.getPageTurner().setOpenings(data.getSequence(), openings,
        		page_width, page_height, opening_cb);
        page_turner_setup = true;
    }

    @Override
    public void display(int width, int height, PanelData data) {
        super.display(width, height, data);
        this.data = data;
        
        if (!isResized()) {
        	this.panel_width = width;
        	this.panel_height = height;
        }
        
        page_width = (panel_width / 2) - 20;
        page_height = panel_height - 132 - display.getContextHeight();
        thumb_size = page_width > page_height ? page_height : page_width;

        thumb_browser_setup = false;
        page_turner_setup = false;

        if (tab == 0) {
            setup_page_turner();
        } else if (tab == 1) {
            setup_thumb_browser();
        }
        
        display.resize(panel_width, panel_height);
        PanelDisplayedEvent event = new PanelDisplayedEvent(panelId(), data);
        eventBus().fireEvent(event);
    }

    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
    	this.panel_width = width;
    	this.panel_height = height;
    	
        page_width = (width / 2) - 20;
        page_height = height - 132 - display.getContextHeight();

        thumb_size = page_width > page_height ? page_height : page_width;
        
        display.getPageTurner().resize(page_width, page_height);
        display.getThumbnailBrowser().resize(width, height - display.getContextHeight());

        display.resize(width, height);
    }
}
