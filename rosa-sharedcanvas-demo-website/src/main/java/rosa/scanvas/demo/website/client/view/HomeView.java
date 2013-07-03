package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.Messages;
import rosa.scanvas.demo.website.client.presenter.HomePanelPresenter;
import rosa.scanvas.demo.website.client.widgets.CellListResources;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class HomeView extends BasePanelView implements HomePanelPresenter.Display {
	
	private final FlowPanel main;
	private final ScrollPanel top;
    private final Button load_button;
    private final TextBox user_textbox;
    private final CheckBox is_col_checkbox;
    
    private final CellList<String> cell_list;
    private final SingleSelectionModel<String> selection_model;

    public HomeView() {
    	CellList.Resources cell_res = GWT.create(CellListResources.class); 
    	
    	this.load_button = new Button(Messages.INSTANCE.load());
        this.user_textbox = new TextBox();
        this.is_col_checkbox = new CheckBox("Collection");
        
        TextCell text_cell = new TextCell();
        this.cell_list = new CellList<String>(text_cell, cell_res);
        this.selection_model = new SingleSelectionModel<String>();
        
        cell_list.addStyleName("CellList");
        cell_list.setWidth(250 + "px");
        cell_list.setSelectionModel(selection_model);

        main = new FlowPanel();
        top = new ScrollPanel(main);
        top.setStylePrimaryName("View");

        Label panel_title = new Label(Messages.INSTANCE.homeLabel());
        panel_title.setStylePrimaryName("PanelTitle");

        main.add(panel_title);
        main.add(cell_list);

        FlowPanel toolbar_panel = new FlowPanel();
        toolbar_panel.setStylePrimaryName("PanelToolbar");

        Label panel_header = new Label(Messages.INSTANCE.homeLabelUser());
        panel_header.setStylePrimaryName("PanelHeader");

        main.add(panel_header);
        main.add(new Label(Messages.INSTANCE.homeUserInstruction()));

        main.add(toolbar_panel);

        toolbar_panel.add(user_textbox);
        toolbar_panel.add(is_col_checkbox);
        toolbar_panel.add(load_button);

        addContent(top);
    }

    public HasValue<String> getUserUrlText() {
        return user_textbox;
    }

    public HasKeyUpHandlers getUserUrlKeyUpHandlers() {
        return user_textbox;
    }

    @Override
    public void setData(List<String> names) {
    	cell_list.setRowCount(names.size(), true);
    	cell_list.setRowData(0, names);
    }

    @Override
    public HasClickHandlers getLoadButton() {
        return load_button;
    }

    @Override
    public HasValue<Boolean> getUserUrlIsCollection() {
        return is_col_checkbox;
    }
    
    @Override
    public void addSelectionChangeEventHandler(SelectionChangeEvent.Handler handler) {
    	selection_model.addSelectionChangeHandler(handler);
    }
    
    @Override
    public String getSelectedCollection() {
    	return selection_model.getSelectedObject();
    }
    
    @Override
    public void resize(int width, int height) {
    	super.resize(width, height);
    	top.setSize((width - 22)+"px", (height - 50)+"px");
    }
}
