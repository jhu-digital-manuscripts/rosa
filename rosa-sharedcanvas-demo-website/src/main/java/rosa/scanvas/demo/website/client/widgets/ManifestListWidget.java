package rosa.scanvas.demo.website.client.widgets;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

public class ManifestListWidget extends Composite {
	
	private FlowPanel mainPanel = new FlowPanel();
	
	private DisclosurePanel collectionPanel = new DisclosurePanel();
	private DisclosurePanel manifestPanel = new DisclosurePanel();
	private DisclosurePanel sequencePanel = new DisclosurePanel();
	
	private FlexTable collectionTable = new FlexTable();
	private FlexTable manifestTable = new FlexTable();
	private FlexTable sequenceTable = new FlexTable();
	
	private ListBox sequencePickerBox = new ListBox();
	
	public ManifestListWidget() {
		initWidget(mainPanel);
		
		mainPanel.add(collectionPanel);
		mainPanel.add(manifestPanel);
		mainPanel.add(sequencePanel);
		mainPanel.add(new Label("Pick a different sequence: "));
		mainPanel.add(sequencePickerBox);
		
		collectionPanel.setContent(collectionTable);
		collectionPanel.setHeader(new HTML("Collection: "));
		collectionPanel.getHeader().setStylePrimaryName("MetadataTitle");
		collectionPanel.setOpen(true);
		collectionPanel.setStylePrimaryName("SidebarItem");
		
		manifestPanel.setContent(manifestTable);
		manifestPanel.setHeader(new HTML("Manifest: "));
		manifestPanel.getHeader().setStylePrimaryName("MetadataTitle");
		manifestPanel.setOpen(true);
		manifestPanel.setStylePrimaryName("SidebarItem");
		
		sequencePanel.setContent(sequenceTable);
		sequencePanel.setHeader(new HTML("Sequence: "));
		sequencePanel.getHeader().setStylePrimaryName("MetadataTitle");
		sequencePanel.setOpen(true);
		sequencePanel.setStylePrimaryName("SidebarItem");
		
		
		manifestTable.setWidget(0, 0, new Label("Title: "));
		manifestTable.setWidget(1, 0, new Label("Agent: "));
		manifestTable.setWidget(2, 0, new Label("Location: "));
		manifestTable.setWidget(3, 0, new Label("Date: "));
		manifestTable.setWidget(4, 0, new Label("Description: "));
		manifestTable.setWidget(5, 0, new Label("Rights: "));
		for (int i=0; i<manifestTable.getRowCount(); i++) {
			manifestTable.getCellFormatter().setStylePrimaryName(i, 0, "MetadataSubtitle");
		}
		
		sequencePickerBox.setWidth("75%");
	}

	public FlowPanel getMainPanel() { return mainPanel; }
	public DisclosurePanel getCollectionPanel() { return collectionPanel; }
	public DisclosurePanel getManifestPanel() { return manifestPanel; }
	public DisclosurePanel getSequencePanel() { return sequencePanel; }
	public ListBox getSequencePickerBox() { return sequencePickerBox; }
	
	public void newCollectionLabel(String text, int row) {
		collectionTable.setWidget(row, 1, new HTML(text));
	}
	
	public void newManifestLabel(String text, int row) {
		manifestTable.setWidget(row, 1, new HTML(text));
	}
	
	public void newSequenceLabel(String text, int row) {
		sequenceTable.setWidget(row, 1, new HTML(text));
	}
	
	public void clearLabels() {
		for (int i=0; i<collectionTable.getRowCount(); i++) {
			collectionTable.setWidget(i, 1, null);
		}
		for (int i=0; i<manifestTable.getRowCount(); i++) {
			manifestTable.setWidget(i, 1, null);
		}
		for (int i=0; i<sequenceTable.getRowCount(); i++) {
			sequenceTable.setWidget(i, 1, null);
		}
		sequencePickerBox.clear();
	}
	
	public void setMetadata(PanelData data) {
		// TODO emit event when a new sequence is selected from 'sequence picker'
		clearLabels();

		ManifestCollection collection = data.getManifestCollection();
		if (collection != null) {
			collectionTable.setWidget(0, 1, new HTML(collection.label()));
			collectionTable.setWidget(1, 1, new HTML(
					"Items: " + collection.manifests().size()));
		}

		Manifest manifest = data.getManifest();
		if (manifest != null) {
			manifestTable.setWidget(0, 1, new HTML(manifest.label()));
			manifestTable.setWidget(1, 1, new HTML(
					((manifest.agent() != null) ? manifest.agent() : "" )));
			manifestTable.setWidget(2, 1, new HTML(
					((manifest.location() != null) ? manifest.location() : "")));
			manifestTable.setWidget(3, 1, new HTML(
					((manifest.date() != null) ? manifest.date() : "")));
			manifestTable.setWidget(4, 1, new HTML(
					((manifest.description() != null) ? manifest.description() : "")));
			manifestTable.setWidget(5, 1, new HTML(
					((manifest.rights() != null) ? manifest.rights() : "")));
		}

		Sequence sequence = data.getSequence();
		if (sequence != null) {
			sequenceTable.setWidget(0, 1, new HTML(sequence.label()));
			sequenceTable.setWidget(1, 1, new HTML("Images: " + sequence.size()));
			
			sequencePickerBox.addItem(sequence.label());
			for (Reference<Sequence> ref : manifest.sequences()) {
				if (!ref.label().equals(sequence.label())) {
					sequencePickerBox.addItem(ref.label());
				}
			}
		}
	}
	
	public void hide() {
		this.setVisible(false);
	}
	
	public void show() {
		this.setVisible(true);
	}
	
}
