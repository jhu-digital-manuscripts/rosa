package rosa.scanvas.demo.website.client.view;

import rosa.scanvas.demo.website.client.presenter.HomePanelPresenter;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class HomeView extends Composite implements HomePanelPresenter.Display {
    private final Anchor roseDataLink;
    private final Anchor testDataLink;
    private final Button goButton;
    private final TextBox userUrlText;

    // TODO use listbox for collections

    public HomeView() {
        this.roseDataLink = new Anchor();
        this.testDataLink = new Anchor();
        this.goButton = new Button("Load");
        this.userUrlText = new TextBox();

        FlowPanel main = new FlowPanel();

        main.add(new Label("Choose a collection to access"));

        // TODO put these in a list box
        
        main.add(roseDataLink);
        main.add(testDataLink);

        FlowPanel go_panel = new FlowPanel();

        main.add(new Label("Access data from URL"));
        go_panel.add(userUrlText);
        go_panel.add(goButton);

        main.add(go_panel);
        main.setStylePrimaryName("PanelView");

        initWidget(main);
    }

    public Anchor getRoseDataLink() {
        return roseDataLink;
    }

    public Anchor getTestDataLink() {
        return testDataLink;
    }

    public HasClickHandlers getGoButton() {
        return goButton;
    }

    public HasValue<String> getUserUrlText() {
        return userUrlText;
    }

    public HasKeyUpHandlers getUserUrlKeyUpHandlers() {
        return userUrlText;
    }

    public Widget asWidget() {
        return this;
    }

    @Override
    public void resize(int width, int height) {
        setPixelSize(width, height);
    }
}
