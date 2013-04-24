package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.demo.website.client.presenter.HomePanelPresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite implements HomePanelPresenter.Display {
    private final ListBox col_listbox;
    private final Button load_button;
    private final TextBox user_textbox;
    private final CheckBox is_col_checkbox;

    public HomeView() {
        this.col_listbox = new ListBox();
        this.load_button = new Button("Load");
        this.user_textbox = new TextBox();
        this.is_col_checkbox = new CheckBox("Collection");

        FlowPanel main = new FlowPanel();

        Label panel_title = new Label("Choose a collection to access.");
        panel_title.setStylePrimaryName("PanelTitle");
        
        main.add(panel_title);
        main.add(col_listbox);

        col_listbox.setVisibleItemCount(5);

        FlowPanel toolbar_panel = new FlowPanel();
        toolbar_panel.setStylePrimaryName("PanelToolbar");
        
        Label panel_header = new Label("Access data from URL.");
        panel_header.setStylePrimaryName("PanelHeader");
        
        main.add(panel_header);
        main.add(new Label(
                "Indicate whether it is a collection of manifest or an individual manifest with the checkbox."));

        main.add(toolbar_panel);

        toolbar_panel.add(user_textbox);
        toolbar_panel.add(is_col_checkbox);
        toolbar_panel.add(load_button);

        main.setStylePrimaryName("PanelView");

        initWidget(main);
    }

    public HasValue<String> getUserUrlText() {
        return user_textbox;
    }

    public HasKeyUpHandlers getUserUrlKeyUpHandlers() {
        return user_textbox;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);
    }

    @Override
    public HasClickHandlers getCollectionList() {
        return col_listbox;
    }

    @Override
    public int getSelectedCollection() {
        return col_listbox.getSelectedIndex();
    }

    @Override
    public void setData(List<String> names) {
        col_listbox.clear();

        for (String name : names) {
            col_listbox.addItem(name);
        }
    }

    @Override
    public HasClickHandlers getLoadButton() {
        return load_button;
    }

    @Override
    public HasValue<Boolean> getUserUrlIsCollection() {
        return is_col_checkbox;
    }
}
