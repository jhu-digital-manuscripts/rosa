package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.presenter.HomePresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite implements HomePresenter.Display{

	private Hyperlink roseDataLink = new Hyperlink();
	private Hyperlink testDataLink = new Hyperlink();
	
	private Button goButton = new Button("Go");
	private TextBox userUrlText = new TextBox();
	
	// TODO Why a focuspanel here?
	
	public HomeView() {
		FocusPanel focusPanel = new FocusPanel();
		DecoratorPanel dPanel = new DecoratorPanel();
		FlowPanel mainPanel = new FlowPanel();
		FlowPanel goPanel = new FlowPanel();
		initWidget(focusPanel);
		
		focusPanel.add(dPanel);
		dPanel.add(mainPanel);
		mainPanel.add(new Label("some instructions....."));
		mainPanel.add(roseDataLink);
		mainPanel.add(testDataLink);
		mainPanel.add(goPanel);
		
		goPanel.add(userUrlText);
		goPanel.add(goButton);
		
		dPanel.setWidth("100%");
		dPanel.setHeight("100%");
	}
	
	public Hyperlink getRoseDataLink() { return roseDataLink; }
	public Hyperlink getTestDataLink() { return testDataLink; }
	public HasClickHandlers getGoButton() { return goButton; }
	public HasValue<String> getUserUrlText() { return userUrlText; }
	public HasKeyUpHandlers getUserUrlKeyUpHandlers() { return userUrlText; }
	public Widget asWidget() { return this; }
	
}
