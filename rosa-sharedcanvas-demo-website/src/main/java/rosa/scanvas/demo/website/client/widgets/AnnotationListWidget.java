package rosa.scanvas.demo.website.client.widgets;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
//import com.google.gwt.user.client.ui.ListBox;

public class AnnotationListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel annoListPanel = new FlowPanel();
	private FlowPanel annoControlPanel = new FlowPanel();
	
	private ScrollPanel imageScrollPanel = new ScrollPanel();
	private ScrollPanel targetedTextScrollPanel = new ScrollPanel();
	private ScrollPanel nontargetedTextScrollPanel = new ScrollPanel();
	
	private FlexTable imageAnnoList = new FlexTable();
	private FlexTable targetedTextAnnoList = new FlexTable();
	private FlexTable nontargetedTextAnnoList = new FlexTable();
	
	private TextBox searchBox = new TextBox();
	
	/*private Button moveAnnoUpButton = new Button("^");
	private Button moveAnnoDownButton = new Button("v");
	private Button moveAnnoTopButton = new Button("^^");
	private Button moveAnnoBottomButton = new Button("vv");*/
	private Button showAnnoButton = new Button("Show All");
	private Button hideAnnoButton = new Button("Hide All");
	
	public AnnotationListWidget() {
		initWidget(mainPanel);
		
		mainPanel.setStylePrimaryName("AnnotationList");
		mainPanel.add(annoListPanel);
		
		/*annoListPanel.add(new Label("Search for annotations: "));
		annoListPanel.add(searchBox);*/
		Label image_label = new Label("Images: ");
		Label tar_text_label = new Label("Text (targeted): ");
		Label nontar_text_label = new Label("Text (non-targeted): ");
		
		annoListPanel.add(image_label);
		annoListPanel.add(imageScrollPanel);
		annoListPanel.add(tar_text_label);
		annoListPanel.add(targetedTextScrollPanel);
		annoListPanel.add(nontar_text_label);
		annoListPanel.add(nontargetedTextScrollPanel);
		annoListPanel.add(annoControlPanel);
		
		imageScrollPanel.add(imageAnnoList);
		targetedTextScrollPanel.add(targetedTextAnnoList);
		nontargetedTextScrollPanel.add(nontargetedTextAnnoList);
		
		/*annoControlPanel.add(moveAnnoTopButton);
		annoControlPanel.add(moveAnnoUpButton);
		annoControlPanel.add(moveAnnoDownButton);
		annoControlPanel.add(moveAnnoBottomButton);*/
		annoControlPanel.add(showAnnoButton);
		annoControlPanel.add(hideAnnoButton);
		
		image_label.addStyleName("SectionLabel");
		tar_text_label.addStyleName("SectionLabel");
		nontar_text_label.addStyleName("SectionLabel");
		
		imageScrollPanel.addStyleName("ScrollPanel");
		targetedTextScrollPanel.addStyleName("ScrollPanel");
		nontargetedTextScrollPanel.addStyleName("ScrollPanel");
		
		imageAnnoList.getColumnFormatter().addStyleName(0, "CheckboxColumn");
		targetedTextAnnoList.getColumnFormatter().addStyleName(0, "CheckboxColumn");
		nontargetedTextAnnoList.getColumnFormatter().addStyleName(0, "CheckboxColumn");
	}

	public FlowPanel getMainPanel() { return mainPanel; }
	public FlowPanel getAnnoListPanel() { return annoListPanel; }
	public FlowPanel getannoControlPanel() { return annoControlPanel; }
	public TextBox getSearchBox() { return searchBox; }
	public FlexTable getImageAnnoList() { return imageAnnoList; }
	public FlexTable getTargetedTextAnnoList() { return targetedTextAnnoList; }
	public FlexTable getNontargetedTextAnnoList() { return nontargetedTextAnnoList; }
/*	public Button getMoveAnnoUpButton() { return moveAnnoUpButton; }
	public Button getMoveAnnoDownButton() { return moveAnnoDownButton; }
	public Button getMoveAnnoTopButton() { return moveAnnoTopButton; }
	public Button getMoveAnnoBottomButton() { return moveAnnoBottomButton; }*/
	public Button getShowAnnoButton() { return showAnnoButton; }
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
