package rosa.scanvas.viewer.client.widgets;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

public class ManifestListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel collectionPanel = new FlowPanel();
	private FlowPanel manifestPanel = new FlowPanel();
	private FlowPanel sequencePanel = new FlowPanel();
	
	private CheckBox collectionCheckBox = new CheckBox();
	private CheckBox manifestCheckBox = new CheckBox();
	private CheckBox sequenceCheckBox = new CheckBox();
	
	private ListBox sequencePickerBox = new ListBox();
	
	public ManifestListWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(collectionPanel);
		mainPanel.add(new HTML("<br><br><br><br>"));
		mainPanel.add(manifestPanel);
		mainPanel.add(new HTML("<br><br><br><br>"));
		mainPanel.add(sequencePanel);
		mainPanel.add(new HTML("<br><br><br><br>"));
		mainPanel.add(new Label("Pick a different sequence: "));
		mainPanel.add(sequencePickerBox);
		
		collectionPanel.add(collectionCheckBox);
		collectionCheckBox.setText("Collection");
		collectionPanel.add(new Label("Collection text"));
		
		manifestPanel.add(manifestCheckBox);
		manifestCheckBox.setText("Manifest");
		manifestPanel.add(new Label("Manifest text"));
		
		sequencePanel.add(sequenceCheckBox);
		sequenceCheckBox.setText("Sequence");
		sequencePanel.add(new Label("Sequence text"));
		
		collectionCheckBox.setValue(true);
		manifestCheckBox.setValue(true);
		sequenceCheckBox.setValue(true);
		
		sequencePickerBox.setWidth("75%");
	}

	public FlowPanel getMainPanel() { return mainPanel; }
	public FlowPanel getCollectionPanel() { return collectionPanel; }
	public FlowPanel getManifestPanel() { return manifestPanel; }
	public FlowPanel getSequencePanel() { return sequencePanel; }
	public CheckBox getCollectionCheckBox() { return collectionCheckBox; }
	public CheckBox getManifestCheckBox() { return manifestCheckBox; }
	public CheckBox getSequenceCheckBox() { return sequenceCheckBox; }
	public ListBox getSequencePickerBox() { return sequencePickerBox; }
	
	public void hide() {
		this.setVisible(false);
	}
	
	public void show() {
		this.setVisible(true);
	}
	
}
