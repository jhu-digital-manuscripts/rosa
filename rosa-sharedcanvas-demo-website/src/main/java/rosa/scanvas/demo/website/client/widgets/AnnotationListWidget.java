package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.demo.website.client.Messages;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AnnotationListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel annoListPanel = new FlowPanel();
	private FlowPanel annoControlPanel = new FlowPanel();
	
	private ScrollPanel imageScrollPanel = new ScrollPanel();
	private ScrollPanel targetedTextScrollPanel = new ScrollPanel();
	
	private FlexTable imageAnnoList = new FlexTable();
	private FlexTable targetedTextAnnoList = new FlexTable();
	
	private Button showAnnoButton = new Button(Messages.INSTANCE.showAll());
	private Button hideAnnoButton = new Button(Messages.INSTANCE.hideAll());
	
	public AnnotationListWidget() {
		initWidget(mainPanel);
		
		mainPanel.setStylePrimaryName("AnnotationList");
		mainPanel.add(annoListPanel);
		
		Label image_label = new Label(Messages.INSTANCE.images());
		Label tar_text_label = new Label(Messages.INSTANCE.text());
		
		annoListPanel.add(image_label);
		annoListPanel.add(imageScrollPanel);
		annoListPanel.add(tar_text_label);
		annoListPanel.add(targetedTextScrollPanel);
		annoListPanel.add(annoControlPanel);
		
		imageScrollPanel.add(imageAnnoList);
		targetedTextScrollPanel.add(targetedTextAnnoList);
		
		annoControlPanel.add(showAnnoButton);
		annoControlPanel.add(hideAnnoButton);
		
		image_label.addStyleName("SectionLabel");
		tar_text_label.addStyleName("SectionLabel");
		
		imageScrollPanel.addStyleName("ScrollPanel");
		targetedTextScrollPanel.addStyleName("ScrollPanel");
		
		imageAnnoList.getColumnFormatter().addStyleName(0, "CheckboxColumn");
		targetedTextAnnoList.getColumnFormatter().addStyleName(0, "CheckboxColumn");
	}

	public FlexTable getImageAnnoList() { return imageAnnoList; }
	public FlexTable getTargetedTextAnnoList() { return targetedTextAnnoList; }
	public Button getShowAnnoButton() { return showAnnoButton; }
	public Button getHideAnnoButton() { return hideAnnoButton; }
	
	/**
	 * Clear entries in all annotation lists
	 */
	public void clearLists() {
		imageAnnoList.removeAllRows();
		targetedTextAnnoList.removeAllRows();
	}
}
