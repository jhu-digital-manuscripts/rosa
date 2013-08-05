package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.presenter.ManifestCollectionPanelPresenter;
import rosa.scanvas.demo.website.client.widgets.CellListResources;
import rosa.scanvas.demo.website.client.widgets.ReferenceCell;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Reference;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * View that displays a Shared Canvas manifest collection.
 */
public class CollectionView extends BasePanelView implements
        ManifestCollectionPanelPresenter.Display {

    private final Panel main;
    private final ScrollPanel top;
    private final CellList<Reference<Manifest>> cell_list;
    private final SingleSelectionModel<Reference<Manifest>> selection_model;
    private final Label collection_label;

    public CollectionView() {
        CellList.Resources cell_res = GWT.create(CellListResources.class);

        main = new FlowPanel();
        top = new ScrollPanel();
        top.setStylePrimaryName("View");

        cell_list = new CellList<Reference<Manifest>>(
                new ReferenceCell<Manifest>(), cell_res);

        top.add(cell_list);
        cell_list.addStyleName("CellList");

        selection_model = new SingleSelectionModel<Reference<Manifest>>();
        cell_list.setSelectionModel(selection_model);

        Label panel_title = new Label(Messages.INSTANCE.collectionInstruction());
        panel_title.setStylePrimaryName("PanelTitle");

        main.add(panel_title);

        collection_label = new Label();
        collection_label.setStylePrimaryName("PanelHeader");

        main.add(collection_label);
        main.add(top);

        addContent(main);
    }

    public void setCollection(List<Reference<Manifest>> col, String label) {
        collection_label.setText(label);

        cell_list.setPageSize(col.size());
        cell_list.setRowCount(col.size(), true);
        cell_list.setRowData(0, col);
    }

    public Reference<Manifest> getSelectedManifest() {
        return selection_model.getSelectedObject();
    }

    public void addSelectionChangeEventHandler(
            SelectionChangeEvent.Handler handler) {
        cell_list.getSelectionModel().addSelectionChangeHandler(handler);
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        top.setSize((width - 22) + "px", (height - 140) + "px");

        int count = height / 25;

        if (count < 10) {
            count = 10;
        }
    }
}
