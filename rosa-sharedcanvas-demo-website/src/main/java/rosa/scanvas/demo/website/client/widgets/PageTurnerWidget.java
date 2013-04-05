package rosa.scanvas.demo.website.client.widgets;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PageTurnerWidget extends Composite {
	
	private Label canvasesLabel = new Label();
	private TextBox pageTextBox = new TextBox();
	private Button closePanelButton = new Button("Close Panel");
	private Button prevButton = new Button("Prev");
	private Button jumpButton = new Button("Jump");
	private Button nextButton = new Button("Next");
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel canvasNavPanel = new FlowPanel();
	private AbsolutePanel canvasPanel = new AbsolutePanel();
	
	public PageTurnerWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(closePanelButton);
		mainPanel.add(canvasesLabel);
		mainPanel.add(canvasPanel);
		mainPanel.add(canvasNavPanel);
		
		canvasNavPanel.add(prevButton);
		canvasNavPanel.add(pageTextBox);
		canvasNavPanel.add(jumpButton);
		canvasNavPanel.add(nextButton);
		
		/*mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");*/
		closePanelButton.setText("Close Panel");
	}
	
	public String getPageText() {
		return pageTextBox.getValue();
	}
	
}
