package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.presenter.ManifestPanelPresenter;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManifestView extends BasePanelView implements
        ManifestPanelPresenter.Display {
	
	private final Panel main;
	private final ScrollPanel top;
    private final ListBox sequence_listbox;
    private final Label manifest_label;

    public ManifestView() {
        main = new FlowPanel();
        top = new ScrollPanel(main);
        top.setStylePrimaryName("PanelView");

        Label panel_title = new Label("Choose a sequence to view.");
        panel_title.setStylePrimaryName("PanelTitle");

        this.sequence_listbox = new ListBox(false);
        this.sequence_listbox.setVisibleItemCount(5);

        this.manifest_label = new Label();
        manifest_label.setStylePrimaryName("PanelHeader");

        main.add(panel_title);
        main.add(manifest_label);
        main.add(sequence_listbox);

        //initWidget(top);
        addContent(top);
    }

    public int getSelectedSequence() {
        return sequence_listbox.getSelectedIndex();
    }

    public void setManifest(Manifest manifest) {
        sequence_listbox.clear();

        String label = manifest.label();

        if (label == null) {
            label = "Unknown title";
        }

        manifest_label.setText(label);

        for (Reference<Sequence> ref : manifest.sequences()) {
            String seq_label = ref.label();

            if (seq_label == null) {
                seq_label = "Sequence";
            }

            sequence_listbox.addItem(seq_label);
        }
    }

    public HasClickHandlers getSequenceList() {
        return sequence_listbox;
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        top.setSize((width - 22) + "px", (height - 50) + "px");
    }
/*    
    @Override
    public void selected(boolean is_selected) {
    	if (is_selected) {
    		top.addStyleName("PanelSelected");
    	} else {
    		top.removeStyleName("PanelSelected");
    	}
    }*/
}
