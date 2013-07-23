package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.presenter.ManifestPanelPresenter;
import rosa.scanvas.demo.website.client.widgets.CellListResources;
import rosa.scanvas.demo.website.client.widgets.ReferenceCell;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ManifestView extends BasePanelView implements
        ManifestPanelPresenter.Display {
	
	private final ScrollPanel top;
    private final Label manifest_label;
    
    private final CellList<Reference<Sequence>> cell_list;
    private final SingleSelectionModel<Reference<Sequence>> selection_model;

    public ManifestView() {
    	CellList.Resources cell_res = GWT.create(CellListResources.class);
    	
    	Panel main = new FlowPanel();
        top = new ScrollPanel(main);
        top.setStylePrimaryName("View");
        
        this.cell_list = new CellList<Reference<Sequence>>(new ReferenceCell<Sequence>(), cell_res);
        this.selection_model = new SingleSelectionModel<Reference<Sequence>>();
        
        cell_list.addStyleName("CellList");
        cell_list.setSelectionModel(selection_model);

        Label panel_title = new Label(Messages.INSTANCE.manifestInstruction());
        panel_title.setStylePrimaryName("PanelTitle");

        this.manifest_label = new Label();
        manifest_label.setStylePrimaryName("PanelHeader");

        main.add(panel_title);
        main.add(manifest_label);
        main.add(cell_list);

        addContent(top);
    }

    public void setManifest(List<Reference<Sequence>> sequences, String label) {
        manifest_label.setText(label);
        
        cell_list.setPageSize(sequences.size());
        cell_list.setRowCount(sequences.size(), true);
        cell_list.setRowData(0, sequences);
    }

    public Reference<Sequence> getSelectedSequence() {
    	return selection_model.getSelectedObject();
    }
    
    public void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler) {
    	selection_model.addSelectionChangeHandler(handler);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        top.setSize((width - 22) + "px", (height - 50) + "px");
    }
}
