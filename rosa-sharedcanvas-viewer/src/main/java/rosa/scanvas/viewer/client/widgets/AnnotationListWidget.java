package rosa.scanvas.viewer.client.widgets;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AnnotationListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel annoListPanel = new FlowPanel();
	private FlowPanel displayedListPanel = new FlowPanel();
	private FlowPanel moveButtonPanel = new FlowPanel();
	
	private TextBox searchBox = new TextBox();
	private ListBox imageAnnoList = new ListBox();
	private ListBox targetedTextAnnoList = new ListBox();
	private ListBox nontargetedTextAnnoList = new ListBox();
	private ListBox displayedAnnoList = new ListBox();
	
	private Button showAnnoButton = new Button("Show");
	private Button moveAnnoUpButton = new Button("^");
	private Button moveAnnoDownButton = new Button("v");
	private Button moveAnnoTopButton = new Button("^^");
	private Button moveAnnoBottomButton = new Button("vv");
	private Button hideAnnoButton = new Button("Hide");
	
	public AnnotationListWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(annoListPanel);
		mainPanel.add(displayedListPanel);
		
		annoListPanel.add(new Label("Search for annotations: "));
		annoListPanel.add(searchBox);
		annoListPanel.add(new Label("Images"));
		annoListPanel.add(imageAnnoList);
		annoListPanel.add(new Label("Text (targeted)"));
		annoListPanel.add(targetedTextAnnoList);
		annoListPanel.add(new Label("Text (non-targeted)"));
		annoListPanel.add(nontargetedTextAnnoList);
		annoListPanel.add(showAnnoButton);
		
		displayedListPanel.add(new Label("Displayed Annotations"));
		displayedListPanel.add(displayedAnnoList);
		displayedListPanel.add(moveButtonPanel);
		
		moveButtonPanel.add(moveAnnoTopButton);
		moveButtonPanel.add(moveAnnoUpButton);
		moveButtonPanel.add(moveAnnoDownButton);
		moveButtonPanel.add(moveAnnoBottomButton);
		moveButtonPanel.add(hideAnnoButton);
		
		imageAnnoList.setVisibleItemCount(4);
		imageAnnoList.setWidth("100%");
		targetedTextAnnoList.setVisibleItemCount(4);
		targetedTextAnnoList.setWidth("100%");
		nontargetedTextAnnoList.setVisibleItemCount(4);
		nontargetedTextAnnoList.setWidth("100%");
		displayedAnnoList.setVisibleItemCount(6);
		displayedAnnoList.setWidth("100%");
	}

	public FlowPanel getMainPanel() { return mainPanel; }
	public FlowPanel getAnnoListPanel() { return annoListPanel; }
	public FlowPanel getDisplayedListPanel() { return displayedListPanel; }
	public FlowPanel getMoveButtonPanel() { return moveButtonPanel; }
	public TextBox getSearchBox() { return searchBox; }
	public ListBox getImageAnnoList() { return imageAnnoList; }
	public ListBox getTargetedTextAnnoList() { return targetedTextAnnoList; }
	public ListBox getNontargetedTextAnnoList() { return nontargetedTextAnnoList; }
	public ListBox getDisplayedAnnoList() { return displayedAnnoList; }
	public Button getShowAnnoButton() { return showAnnoButton; }
	public Button getMoveAnnoUpButton() { return moveAnnoUpButton; }
	public Button getMoveAnnoDownButton() { return moveAnnoDownButton; }
	public Button getMoveAnnoTopButton() { return moveAnnoTopButton; }
	public Button getMoveAnnoBottomButton() { return moveAnnoBottomButton; }
	public Button getHideAnnoButton() { return hideAnnoButton; }
	
	public void hide() {
		this.hide();
	}
	
	public void show() {
		this.show();
	}
	
}
