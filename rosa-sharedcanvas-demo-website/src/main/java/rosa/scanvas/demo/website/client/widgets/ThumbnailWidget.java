package rosa.scanvas.demo.website.client.widgets;

import java.lang.IndexOutOfBoundsException;

import java.util.Iterator;
import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.dynimg.IIIFImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.dynimg.WebImage;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ThumbnailWidget extends Composite {
	
	/**
	 * Widget holding a thumbnail image and an associated label
	 */
	public class ThumbnailImageWidget extends FocusPanel {
		private WebImage image;
		private Label label = new Label();
		private FlowPanel mainPanel = new FlowPanel();
		
		private final String collection;
		private final String manifest;
		private final String sequence;
		private final String canvas;
		
		public ThumbnailImageWidget(Annotation anno, String collection, 
				String manifest, String sequence, String canvas) {
			add(mainPanel);
			
			this.collection = collection;
			this.manifest = manifest;
			this.sequence = sequence;
			this.canvas = canvas;
			
			if (anno != null) {
				image = initImage(anno);
				mainPanel.add(image);
			}
			
			mainPanel.add(label);
		}
		
		private WebImage initImage(Annotation anno) {
			IIIFImageServer iiifServer = new IIIFImageServer();
			
			String imageId = IIIFImageServer.parseIdentifier(anno.body().uri());
			int width = 50;
			int height = 50;
			
			String url = iiifServer.renderAsUrl(imageId, width, height);	
			
			return new WebImage(url, width, height);
		}
		
		public void makeViewable() {
			image.makeViewable();
		}
		
		public void setLabel(String text) {
			label.setText(text);
		}
		
		public String getCanvasUri() { return canvas; }
		public String getCollectionUri() { return collection; }
		public String getManifestUri() { return manifest; }
		public String getSequenceUri() { return sequence; }
	}
	
// -------------------------------------------------------------------
	
	private final int THUMB_PANEL_WIDTH = 4;

	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel searchPanel = new FlowPanel();
	private ScrollPanel tablePanel = new ScrollPanel();
	
	private FlowPanel thumbTable = new FlowPanel();
	private TextBox searchBox = new TextBox();
	
	private Button searchButton = new Button();
	private Button closePanelButton = new Button();
	
	public ThumbnailWidget() {
		initWidget(mainPanel);
		mainPanel.add(searchPanel);
		mainPanel.add(tablePanel);
		
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		searchPanel.add(closePanelButton);
		searchPanel.setHeight("30px");
		
		tablePanel.add(thumbTable);
		tablePanel.setSize("100%", "90%");
		
		searchButton.setText("Search");
		closePanelButton.setText("Close Panel");
	}
	
	/**
	 * Set the thumbnail data: thumbnail images plus canvas labels
	 */
	public void setData(PanelData data) {
		Sequence sequence = data.getSequence();
		List<Annotation> images = data.getImageAnnotations();
		searchPanel.add(new Label("Size: "+sequence.size()));
		int index = 0;
		
		Iterator<Canvas> it = sequence.iterator();
		while (it.hasNext()) {
			Canvas canvas = it.next();
			ThumbnailImageWidget thumb = new ThumbnailImageWidget(
					getAssociatedAnnotation(canvas, images), 
							data.getCollection().uri(),
							data.getManifest().uri(),
							sequence.uri(),
							String.valueOf(index));
			thumb.setLabel(canvas.label().replace(".", " "));
			thumb.addStyleName("thumbnail");
			
			thumbTable.add(thumb);
			index++;
		}
		
		// Show the first visible thumbnails
		loadThumbnails(0);
	}
	
	/**
	 * returns the first annotation that has its target as the given canvas.
	 * if no annotations meet this criteria, NULL is returned
	 */
	private Annotation getAssociatedAnnotation(Canvas canvas, List<Annotation> images) {
		for (Annotation anno : images) {
			Iterator<AnnotationTarget> it = anno.targets().iterator();
			while (it.hasNext()) {
				if (it.next().uri().equals(canvas.uri())) {
					images.remove(anno);
					return anno;
				}
			}
		}
		return null;
	}
	
	/**
	 * Load the images for any thumbnails made viewable
	 * @param visible The previous vertical scroll position, in pixels
	 */
	public void loadThumbnails(int visible) {
		int left = tablePanel.getAbsoluteLeft();
		int right = left + tablePanel.getOffsetWidth();
		int top = tablePanel.getAbsoluteTop();
		int bottom = top + tablePanel.getOffsetHeight();
		
		int size = thumbTable.getWidgetCount();
		for (int i=0; i<size; i++) {
			ThumbnailImageWidget thumb = (ThumbnailImageWidget) thumbTable.getWidget(i);
			
			int thumbLeft = thumb.getAbsoluteLeft();
			int thumbTop = thumb.getAbsoluteTop();
			
			if (thumbLeft >= left && thumbTop >= top && 
					thumbLeft < right && thumbTop < bottom) {
				
				thumb.makeViewable();
			}
			
		}
	}
	
	/**
	 * Returns the current vertical scroll position in pixels
	 */
	public int getVerticalScrollPosition() {
		return tablePanel.getVerticalScrollPosition();
	}
	
	/**
	 * Returns the height of a row in pixels
	 */
	public int getRowHeight(int row) {
		return thumbTable.getWidget(0).getOffsetHeight();
	}

	public ScrollPanel getTablePanel() { return tablePanel; }
	public void setTablePanel(ScrollPanel tablePanel) { this.tablePanel = tablePanel; }
	public FlowPanel getThumbTable() { return thumbTable; }
	public TextBox getSearchBox() { return searchBox; }
	public Button getSearchButton() { return searchButton; }
	public Button getClosePanelButton() { return closePanelButton; }
	
}
