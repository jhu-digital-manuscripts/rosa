package rosa.scanvas.demo.website.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Label;
//import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

public class AnnotationListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel annoListPanel = new FlowPanel();
	private FlowPanel moveButtonPanel = new FlowPanel();
	
	private ScrollPanel imageScrollPanel = new ScrollPanel();
	private ScrollPanel targetedTextScrollPanel = new ScrollPanel();
	private ScrollPanel nontargetedTextScrollPanel = new ScrollPanel();
	
	private FlexTable imageAnnoList = new FlexTable();
	private FlexTable targetedTextAnnoList = new FlexTable();
	private FlexTable nontargetedTextAnnoList = new FlexTable();
	
	private TextBox searchBox = new TextBox();
	
	private Button moveAnnoUpButton = new Button("^");
	private Button moveAnnoDownButton = new Button("v");
	private Button moveAnnoTopButton = new Button("^^");
	private Button moveAnnoBottomButton = new Button("vv");
	private Button hideAnnoButton = new Button("Hide");
	
	public AnnotationListWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(annoListPanel);
		
		annoListPanel.add(new Label("Search for annotations: "));
		annoListPanel.add(searchBox);
		annoListPanel.add(new Label("Images"));
		annoListPanel.add(imageScrollPanel);
		annoListPanel.add(new Label("Text (targeted)"));
		annoListPanel.add(targetedTextScrollPanel);
		annoListPanel.add(new Label("Text (non-targeted)"));
		annoListPanel.add(nontargetedTextScrollPanel);
		annoListPanel.add(moveButtonPanel);
		
		imageScrollPanel.add(imageAnnoList);
		targetedTextScrollPanel.add(targetedTextAnnoList);
		nontargetedTextScrollPanel.add(nontargetedTextAnnoList);
		
		moveButtonPanel.add(moveAnnoTopButton);
		moveButtonPanel.add(moveAnnoUpButton);
		moveButtonPanel.add(moveAnnoDownButton);
		moveButtonPanel.add(moveAnnoBottomButton);
		moveButtonPanel.add(hideAnnoButton);
		
		imageScrollPanel.setSize("100%","10em");
		targetedTextScrollPanel.setSize("100%", "10em");
		nontargetedTextScrollPanel.setSize("100%", "10em");
		imageAnnoList.getColumnFormatter().setWidth(0, "20px");
		targetedTextAnnoList.getColumnFormatter().setWidth(0, "20px");
		nontargetedTextAnnoList.getColumnFormatter().setWidth(0, "20px");
		
		
	}

	public FlowPanel getMainPanel() { return mainPanel; }
	public FlowPanel getAnnoListPanel() { return annoListPanel; }
	public FlowPanel getMoveButtonPanel() { return moveButtonPanel; }
	public TextBox getSearchBox() { return searchBox; }
	public FlexTable getImageAnnoList() { return imageAnnoList; }
	public FlexTable getTargetedTextAnnoList() { return targetedTextAnnoList; }
	public FlexTable getNontargetedTextAnnoList() { return nontargetedTextAnnoList; }
	public Button getMoveAnnoUpButton() { return moveAnnoUpButton; }
	public Button getMoveAnnoDownButton() { return moveAnnoDownButton; }
	public Button getMoveAnnoTopButton() { return moveAnnoTopButton; }
	public Button getMoveAnnoBottomButton() { return moveAnnoBottomButton; }
	public Button getHideAnnoButton() { return hideAnnoButton; }
	
	/*public List<Integer> getSelectedRows(ClickEvent event) {
		List<Integer> selectedRows = new ArrayList<Integer>();
		
	//	for (int i=0; i<)
		return selectedRows;
	}*/
	
	public void clearLists() {
		imageAnnoList.removeAllRows();
		targetedTextAnnoList.removeAllRows();
		nontargetedTextAnnoList.removeAllRows();
	}
	
	public void hide() {
		this.hide();
	}
	
	public void show() {
		this.show();
	}
	
}
