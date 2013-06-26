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
	
	private FlowPanel seq = new FlowPanel();
	
	private FlexTable collectionTable = new FlexTable();
	private FlexTable manifestTable = new FlexTable();
	private FlexTable sequenceTable = new FlexTable();
	
	private ListBox sequencePickerBox = new ListBox();
	
	public ManifestListWidget() {
		initWidget(mainPanel);
		mainPanel.addStyleName("ManifestList");
		
		mainPanel.add(collectionPanel);
		mainPanel.add(manifestPanel);
		mainPanel.add(sequencePanel);
		
		collectionPanel.setContent(collectionTable);
		collectionPanel.setHeader(new HTML("Collection: "));
		collectionPanel.setOpen(true);
		
		manifestPanel.setContent(manifestTable);
		manifestPanel.setHeader(new HTML("Manifest: "));
		manifestPanel.setOpen(true);
		
		sequencePanel.setContent(seq);
		sequencePanel.setHeader(new HTML("Sequence: "));
		sequencePanel.setOpen(true);
		
		seq.addStyleName("Sequence");
		seq.add(sequenceTable);
		seq.add(new Label("Pick a different sequence: "));
		seq.add(sequencePickerBox);
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
	
	/**
	 * Displays metadata
	 */
	public void setMetadata(PanelData data) {
		if (data == null) {
			collectionPanel.setVisible(false);
			manifestPanel.setVisible(false);
			sequencePanel.setVisible(false);
			return;
		}
		int i = 0;
		// TODO emit event when a new sequence is selected from 'sequence picker'
		clearLabels();

		ManifestCollection collection = data.getManifestCollection();
		if (collection != null) {
			collectionPanel.setVisible(true);
			collectionTable.setWidget(0, 1, new HTML(collection.label()));
			collectionTable.setWidget(1, 1, new HTML(
					"Items: " + collection.manifests().size()));
		} else {
			collectionPanel.setVisible(false);
		}
		
		Manifest manifest = data.getManifest();
		i = 0;
		if (manifest != null) {
			manifestPanel.setVisible(true);
			manifestTable.setWidget(i, 0, new Label("Title: "));
			manifestTable.setWidget(i++, 1, new HTML(manifest.label()));
			
			if (manifest.agent() != null) {
				manifestTable.setWidget(i, 0, new Label("Agent: "));
				manifestTable.setWidget(i++, 1, new HTML(manifest.agent()));
			}
			
			if (manifest.location() != null) {
				manifestTable.setWidget(i, 0, new Label("Location:"));
				manifestTable.setWidget(i++, 1, new HTML(manifest.location()));
			}
			
			if (manifest.date() != null) {
				manifestTable.setWidget(i, 0, new Label("Date: "));
				manifestTable.setWidget(i++, 1, new HTML(manifest.date()));
			}
			
			if (manifest.description() != null) {
				manifestTable.setWidget(i, 0, new Label("Description:"));
				manifestTable.setWidget(i++, 1, new HTML(manifest.description()));
			}
			
			if (manifest.rights() != null) {
				manifestTable.setWidget(i, 0, new Label("Rights: "));
				manifestTable.setWidget(i++, 1, new HTML(manifest.rights()));
			}
			
			for (int j = 0; j < manifestTable.getRowCount(); j++) {
				manifestTable.getCellFormatter().setStylePrimaryName(j, 0, "MetadataSubtitle");
			}
		} else {
			manifestPanel.setVisible(false);
		}

		Sequence sequence = data.getSequence();
		if (sequence != null) {
			sequencePanel.setVisible(true);
			sequenceTable.setWidget(0, 1, new HTML(sequence.label()));
			sequenceTable.setWidget(1, 1, new HTML("Images: " + sequence.size()));

			int index = 0;
			for (Reference<Sequence> ref : manifest.sequences()) {
				sequencePickerBox.addItem(ref.label());
				sequencePickerBox.setValue(index, ref.uri());
				
				if (ref.uri().equals(sequence.uri())) {
					sequencePickerBox.setSelectedIndex(index);
				}
				index++;
			}
		} else {
			sequencePanel.setVisible(false);
		}
	}
	
	public void hide() {
		this.setVisible(false);
	}
	
	public void show() {
		this.setVisible(true);
	}
	
}
