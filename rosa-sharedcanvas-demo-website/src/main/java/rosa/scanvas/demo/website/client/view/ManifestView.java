package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.ManifestPanelPresenter;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ManifestView extends Composite implements
        ManifestPanelPresenter.Display {
    private final ListBox manifest_listbox;
    private final Label view_label;
    private final Panel main;

    public ManifestView() {
        this.manifest_listbox = new ListBox();
        this.view_label = new Label();
        this.main = new FlowPanel();

        manifest_listbox.setVisibleItemCount(10);

        main.add(view_label);
        main.add(new Label("Choose a sequence"));
        main.add(manifest_listbox);

        main.setStylePrimaryName("PanelView");
        
        initWidget(main);        
    }

    public int getSelectedRow(ClickEvent event) {
        return manifest_listbox.getSelectedIndex();
    }

    public void setData(List<Reference<Sequence>> seq) {
        manifest_listbox.clear();

        for (int i = 0; i < seq.size(); i++) {
            manifest_listbox.addItem(seq.get(i).uri());
        }
    }

    public HasText getViewLabel() {
        return view_label;
    }

    public HasClickHandlers getList() {
        return manifest_listbox;
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);
    }
}
