package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.presenter.ManifestCollectionPanelPresenter;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class CollectionView extends BasePanelView implements
        ManifestCollectionPanelPresenter.Display {
	
	private final Panel main;
	private final ScrollPanel top;
    private Label collection_label;
    
    private final CellList<String> cell_list;
    private final SingleSelectionModel<String> selection_model;

    public CollectionView() {
        main = new FlowPanel();
        top = new ScrollPanel();
        top.setStylePrimaryName("View");
        
        TextCell text_cell = new TextCell();
        this.cell_list = new CellList<String>(text_cell);
        this.selection_model = new SingleSelectionModel<String>();
        
        top.add(cell_list);
        cell_list.addStyleName("CellList");
        cell_list.setSelectionModel(selection_model);

        Label panel_title = new Label(Messages.INSTANCE.collectionInstruction());
        panel_title.setStylePrimaryName("PanelTitle");

        main.add(panel_title);

        this.collection_label = new Label();
        collection_label.setStylePrimaryName("PanelHeader");

        main.add(collection_label);
        main.add(top);
        
        addContent(main);
    }
    
    public void setCollection(List<String> col, String label) {
    	collection_label.setText(label);
    	
    	cell_list.setPageSize(col.size());
    	cell_list.setRowCount(col.size(), true);
    	cell_list.setRowData(0, col);
    }
    	
    
    public String getSelectedManifest() {
    	return selection_model.getSelectedObject();
    }
    
    public void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler) {
    	selection_model.addSelectionChangeHandler(handler);
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
    	//top.setSize((width - 22) + "px", (height - 50) + "px");
    	top.setSize((width - 22) + "px", (height - 140) + "px");
    	
        int count = height / 25;

        if (count < 10) {
            count = 10;
        }

    }
}
