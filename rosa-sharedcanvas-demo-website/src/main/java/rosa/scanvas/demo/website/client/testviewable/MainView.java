package rosa.scanvas.demo.website.client.testviewable;

import rosa.scanvas.demo.website.client.disparea.DisplayArea;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Provides the viewable elements of the DisplayArea test environment.
 */
public class MainView extends Composite implements MainPresenter.Display {
	
	private final Panel main;
	private final Label label;
	private final Button polyButton = new Button("Polygon");
	private final Button imgButton = new Button("Image");
	private final Button clearButton = new Button("Clear");
	private final Button resetButton = new Button("Reset");
	private final Button closeButton = new Button("Close");
	private final Button txtButton = new Button("Text");
	private final Button zoomInButton = new Button("Zoom In");
	private final Button zoomOutButton = new Button("Zoom Out");
	private final Button staticImgButton = new Button("Static Img");
	
	private final DisplayAreaView area_view;
	
	public MainView(int width, int height, int vp_width, int vp_height) {
		this.main = new FlowPanel();
		this.label = new Label();
		
		DisplayArea area = new DisplayArea(width, height, vp_width, vp_height);
		this.area_view = new DisplayAreaView();
		this.area_view.display();
		this.area_view.lockDisplay(false);
		
		main.add(area_view);
		main.add(label);
		main.add(polyButton);
		main.add(imgButton);
		main.add(txtButton);
		main.add(staticImgButton);
		main.add(clearButton);
		main.add(zoomInButton);
		main.add(zoomOutButton);
		main.add(resetButton);
		main.add(closeButton);
		
		initWidget(main);
	}
	
	@Override
	public Label getLabel() { return label; }
	
	@Override
	public Button getPolyButton() { return polyButton; }
	
	@Override
	public Button getImgButton() { return imgButton; }
	
	@Override
	public Button getClearButton() { return clearButton; }
	
	@Override
	public Button getResetButton() { return resetButton; }
	
	@Override
	public Button getCloseButton() { return closeButton; }
	
	@Override
	public DisplayAreaView getDisplayAreaWidget() { return area_view; }
	
	@Override
	public Button getTextButton() { return txtButton; }
	
	@Override
	public Button getZoomInButton() { return zoomInButton; }
	
	@Override
	public Button getZoomOutButton() { return zoomOutButton; }
	
	@Override
	public Button getStaticImgButton() { return staticImgButton; }
	
}
