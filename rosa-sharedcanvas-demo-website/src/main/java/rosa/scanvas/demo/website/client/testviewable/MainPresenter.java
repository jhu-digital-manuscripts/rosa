package rosa.scanvas.demo.website.client.testviewable;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.demo.website.client.disparea.MasterImageDisplayElement;
import rosa.scanvas.demo.website.client.disparea.MasterImageDrawable;
import rosa.scanvas.demo.website.client.disparea.PolygonDisplayElement;
import rosa.scanvas.demo.website.client.disparea.PolygonDrawable;
import rosa.scanvas.demo.website.client.disparea.TextDisplayElement;
import rosa.scanvas.demo.website.client.disparea.TextDrawable;
import rosa.scanvas.demo.website.client.dynimg.IIIFImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 */
public class MainPresenter {
	
    public interface Display extends IsWidget {
        Label getLabel();
        
        Button getPolyButton();
        
        Button getImgButton();
         
        Button getClearButton();
        
        Button getResetButton();
        
        Button getCloseButton();
        
        Button getTextButton();
        
        DisplayAreaView getDisplayAreaWidget();
    }
    
    private final HasWidgets container;
    private final Display display;
    	
    private double zoom = 1.0;
    private int num_poly = 0;
    private int num_img = 0;
    
    private List<DisplayElement> els = new ArrayList<DisplayElement>();
    
    public MainPresenter(Display display, HasWidgets container) {
    	this.container = container;
    	this.display = display;
    	
    	this.container.add(display.asWidget());
    	bind();
    }
    
    private void bind() {
    	display.getPolyButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			drawPolygon();
    		}
    	});
    	
    	display.getImgButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			drawImage();
    		}
    	});
    	
    	display.getResetButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			doReset();
    		}
    	});
    	
    	display.getClearButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			doClear();
    		}
    	});
    	
    	display.getTextButton().addClickHandler(new ClickHandler() {
    		public void onClick(ClickEvent event) {
    			drawText();
    		}
    	});
    }
    
    public Button getCloseButton() {
    	return display.getCloseButton();
    }
    
    /**
     * Create a polygon drawable with the predefined coordinates
     */
    private void drawPolygon() {
    	// create PolygonDrawable
    	DisplayAreaView view = display.getDisplayAreaWidget();
    	
    	/*final int[][] coords = 
    		{ {300, 400}, {400, 300}, {500, 400}, {450, 500}, {350, 500}, {300, 400} };
    	final int[] bounds = { 300, 300, 500, 500 };*/
    	final int[][] coords = 
    		{ {800, 900}, {900, 800}, {1000, 900}, {950, 1000}, {850, 1000}, {800, 900} };
    	final int[] bounds = { 800, 800, 1000, 1000 };
    	
    	PolygonDisplayElement el = new PolygonDisplayElement("poly"+(num_poly++), bounds[0],
    			bounds[1], bounds[2], bounds[3], coords);
    	el.setVisible(true);
    	el.setStackingOrder(0);
    	el.setDrawable(new PolygonDrawable(el));
    	
    	els.add(el);
    	view.area().setContent(els);
    	//view.area().initZoomLevels();
    	view.redraw();  	
    }
    
    /**
     * Create an image drawable with a predefined url
     */
    private void drawImage() {
    	// create MasterImageDrawable
    	IIIFImageServer iiif_server = IIIFImageServer.instance();
    	DisplayAreaView view = display.getDisplayAreaWidget();
    	
    	final String url = 
    		"http://rosetest.library.jhu.edu/iiif/rose%2FLudwigXV7%2FLudwigXV7.014r.tif/full/full/0/native.jpg";
    	String id = IIIFImageServer.parseIdentifier(url);
    	
    	MasterImage master = new MasterImage(id, DispAreaTestViewable.width, 
    			DispAreaTestViewable.height);
    	
    	MasterImageDisplayElement el = new MasterImageDisplayElement("img"+(num_img++),
    			0, 0, iiif_server, master);
    	el.setVisible(true);
    	el.setStackingOrder(1);
    	el.setDrawable(new MasterImageDrawable(el));
    	
    	els.add(el);
    	view.area().setContent(els);
    	//view.area().initZoomLevels();
    	view.redraw();
    }
    
    /**
     * Create a text drawable with predefined data
     */
    private void drawText() {
    	DisplayAreaView view = display.getDisplayAreaWidget();
    	
    	String text = "Dont j'ay este moult deceuz.";
    	int[][] coords_txt = 
    		{ {1703, 578}, {2752, 558}, {2752, 634}, {1703, 658}, {1703, 578} };
    	int[] bounds_txt = { 1703, 578, 1049, 80 };
    	
    	TextDisplayElement el3 = new TextDisplayElement("txt", bounds_txt[0],
    			bounds_txt[1], bounds_txt[2], bounds_txt[3], text, coords_txt);
    	el3.setVisible(true);
    	el3.setStackingOrder(0);
    	el3.setDrawable(new TextDrawable(el3));
    	els.add(el3);
    	
    	view.area().setContent(els);
    	view.redraw();
    }
    
    /**
     * Clear the canvas
     */
    private void doClear() {
    	DisplayAreaView view = display.getDisplayAreaWidget();
    	els.clear();
    	
    	view.area().setContent(els);
    	view.redraw();
    }
    
    /**
     * Reset the position and zoom of canvas
     */
    private void doReset() {
    	DisplayAreaView view = display.getDisplayAreaWidget();
    	view.resetDisplay();
    }
    
}
