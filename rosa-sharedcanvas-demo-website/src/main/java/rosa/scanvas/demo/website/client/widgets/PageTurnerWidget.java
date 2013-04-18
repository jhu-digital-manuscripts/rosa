package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.dynimg.IIIFImageServer;
import rosa.scanvas.demo.website.client.dynimg.MasterImage;
import rosa.scanvas.demo.website.client.dynimg.WebImage;
import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.Window;

public class PageTurnerWidget extends Composite {
	
	private Label canvasesLabel = new Label("Pages: ");
	private TextBox pageTextBox = new TextBox();
	private Button prevButton = new Button("Prev");
	private Button jumpButton = new Button("Jump");
	private Button nextButton = new Button("Next");
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel canvasDisplayPanel = new FlowPanel();
	private FlowPanel canvasNavPanel = new FlowPanel();
	private AbsolutePanel canvasPanel = new AbsolutePanel();
	
	public FlowPanel getCanvasDisplayPanel() { return canvasDisplayPanel; }
	public HasClickHandlers getPrevButton() { return prevButton; }
	public HasClickHandlers getNextButton() { return nextButton; }
	public HasClickHandlers getJumpButton() { return jumpButton; }
	
	public String getPageText() { 
		return pageTextBox.getValue(); 
	}
	
	private IIIFImageServer iiifServer = new IIIFImageServer();
	
	public PageTurnerWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(canvasesLabel);
		mainPanel.add(canvasPanel);
		mainPanel.add(canvasNavPanel);
		
		canvasPanel.add(canvasDisplayPanel);
		
		canvasNavPanel.add(prevButton);
		canvasNavPanel.add(pageTextBox);
		canvasNavPanel.add(jumpButton);
		canvasNavPanel.add(nextButton);
		
		canvasPanel.setSize("100%", "85%");
	}
	
	private Annotation getAssociatedAnnotation(Canvas canvas, List<Annotation> annotations) {
		for (Annotation anno : annotations) {
			if (anno.body().isImage()) {
				for (AnnotationTarget target : anno.targets()) {
					if (target.uri().equals(canvas.uri())) {
						return anno;
					}
				}
			}
		}
		return null;
	}
	
	public void setData(Canvas[] canvas, List<Annotation> annotations) {
		
		for (Canvas canv : canvas) {
			FocusPanel pagePanel = new FocusPanel();
			Annotation imageAnno = getAssociatedAnnotation(canv, annotations);
			
			if (imageAnno != null) {
				String imageId = IIIFImageServer.parseIdentifier(imageAnno.body().uri());
				int width = 150;
				int height = 150;
				
				String url = iiifServer.renderAsUrl(imageId, width, height);	
				
				WebImage page = new WebImage(url, width, height);
		
				pagePanel.add(page);
				page.addStyleName("opening");
				page.makeViewable();
				
			} else {
				Label page = new Label(canv.label().replace(".", " "));
				pagePanel.add(page);
				page.addStyleName("opening");
			}
			
			canvasDisplayPanel.add(pagePanel);
			
			
			canvasesLabel.setText(canvasesLabel.getText() + canv.label() + " - ");
			
		}
		
		canvasesLabel.setText(canvasesLabel.getText().substring(0, canvasesLabel.getText().length()-3));
	}
	
}
