package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.ManifestCollectionPanelPresenter;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class CollectionView extends Composite implements
        ManifestCollectionPanelPresenter.Display {
	
	private final Panel main;
	private final ScrollPanel top;
    private Label collection_label;
    private ListBox collections_listbox;

    public CollectionView() {
        main = new FlowPanel();
        top = new ScrollPanel(main);
        top.setStylePrimaryName("PanelView");

        Label panel_title = new Label("Choose a manifest to view.");
        panel_title.setStylePrimaryName("PanelTitle");

        main.add(panel_title);

        this.collections_listbox = new ListBox(false);
        this.collections_listbox.setVisibleItemCount(10);

        this.collection_label = new Label();
        collection_label.setStylePrimaryName("PanelHeader");

        main.add(collection_label);
        main.add(collections_listbox);

        initWidget(top);
    }

    public void setCollection(ManifestCollection col) {
        collections_listbox.clear();

        String label = col.label() == null ? "Unknown collection" : col.label();
        collection_label.setText(label);

        List<Reference<Manifest>> manifests = col.manifests();

        for (int i = 0; i < manifests.size(); i++) {
            collections_listbox.addItem(manifests.get(i).label());
        }
    }

    public int getSelectedManifest() {
        return collections_listbox.getSelectedIndex();
    }

    public HasClickHandlers getManifestList() {
        return collections_listbox;
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);

        int count = height / 20;

        if (count < 10) {
            count = 10;
        }

        collections_listbox.setVisibleItemCount(count);
    }
    
    @Override
    public void selected(boolean is_selected) {
    	if (is_selected) {
    		top.addStyleName("PanelSelected");
    	} else {
    		top.removeStyleName("PanelSelected");
    	}
    }
}
