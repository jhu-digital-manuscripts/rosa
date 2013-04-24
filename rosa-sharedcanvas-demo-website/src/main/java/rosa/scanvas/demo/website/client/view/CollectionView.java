package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.ManifestCollectionPanelPresenter;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Reference;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class CollectionView extends Composite implements
        ManifestCollectionPanelPresenter.Display {
    private Label title_label;
    private ListBox collections_listbox;
    
    public CollectionView() {
        this.title_label = new Label();

        Panel main = new FlowPanel();

        this.collections_listbox = new ListBox(false);
        this.collections_listbox.setVisibleItemCount(10);

        main.add(title_label);
        main.add(collections_listbox);

        main.setStylePrimaryName("PanelView"); 

        initWidget(main);
    }
	
	public void setData(List<Reference<Manifest>> data) {
        collections_listbox.clear();

        for (int i = 0; i < data.size(); i++) {
            collections_listbox.addItem(data.get(i).label());
        }
    }

    public int getSelectedRow(ClickEvent event) {
        return collections_listbox.getSelectedIndex();
    }

    public HasText getViewLabel() {
        return title_label;
    }

    public HasClickHandlers getList() {
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
}
