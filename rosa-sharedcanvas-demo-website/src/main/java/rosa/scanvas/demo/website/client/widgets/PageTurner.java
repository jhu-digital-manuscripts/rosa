package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import rosa.scanvas.demo.website.client.dynimg.ImageServer;
import rosa.scanvas.demo.website.client.dynimg.WebImage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class PageTurner extends Composite {
    private final Grid display;
    private final ImageServer image_server;

    private int position;
    private List<Opening> openings;
    private int page_width, page_height;

    public PageTurner(ImageServer image_server) {
        this.image_server = image_server;
        this.display = new Grid(2, 2);

        FlowPanel main = new FlowPanel();

        FlowPanel toolbar = new FlowPanel();

        Button prev_button = new Button("Prev");
        TextBox goto_textbox = new TextBox();
        Button goto_button = new Button("Goto");
        Button next_button = new Button("Next");

        next_button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (position + 1 < openings.size()) {
                    position++;
                    display(openings.get(position));
                }
            }
        });

        prev_button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (position > 0) {
                    position--;
                    display(openings.get(position));
                }
            }
        });

        toolbar.add(prev_button);
        toolbar.add(goto_textbox);
        toolbar.add(goto_button);
        toolbar.add(next_button);

        main.add(display);
        main.add(toolbar);

        main.setStylePrimaryName("PageTurner");
        
        initWidget(main);
    }

    public void setOpenings(List<Opening> openings, int page_width,
            int page_height) {
        this.position = 0;
        this.openings = openings;

        resize(page_width, page_height);
    }

    public void resize(int page_width, int page_height) {
        this.page_width = page_width;
        this.page_height = page_height;

        if (openings != null) {
            display(openings.get(position));
        }
    }

    private void display(Opening opening) {
        if (opening.getVerso() == null) {
            display.setWidget(0, 0, new FlowPanel());
        } else {
            WebImage image = image_server.renderToRectangle(opening.getVerso(),
                    page_width, page_height);
            image.makeViewable();
            display.setWidget(0, 0, image);
        }

        if (opening.getVersoLabel() == null) {
            display.setWidget(1, 0, new Label());
        } else {
            display.setWidget(1, 0, new Label(opening.getVersoLabel()));
        }

        if (opening.getRecto() == null) {
            display.setWidget(0, 1, new FlowPanel());
        } else {
            WebImage image = image_server.renderToRectangle(opening.getRecto(),
                    page_width, page_height);
            image.makeViewable();
            display.setWidget(0, 1, image);
        }

        if (opening.getRectoLabel() == null) {
            display.setWidget(1, 1, new Label());
        } else {
            display.setWidget(1, 1, new Label(opening.getRectoLabel()));
        }
    }
}