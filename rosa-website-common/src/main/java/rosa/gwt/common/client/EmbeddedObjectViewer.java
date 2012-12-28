package rosa.gwt.common.client;

import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InsertPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Handles the display of some raw html object, like flash, and allows it to be
 * resized and poppped up. A toolbar is displayed above the object.
 */
public class EmbeddedObjectViewer extends Composite {
    public interface DisplayCallback {
        /**
         * Returns html to display in the viewer that takes up the given
         * dimensions.
         * 
         * @param width
         *            in css
         * @param height
         *            in css
         * @return html
         */
        public String display(String width, String height);
    }

    private final HorizontalPanel toolbar;

    public EmbeddedObjectViewer(final DisplayCallback cb,
            final int resizeincrement, final int minwidth, final int maxwidth,
            final double aspectratio, final String popup_title) {
        final FlowPanel main = new FlowPanel();

        initWidget(main);

        this.toolbar = new HorizontalPanel();
        toolbar.setSpacing(2);

        main.add(toolbar);

        final Button smaller = new Button(Labels.INSTANCE.decreaseSize());
        final Button larger = new Button(Labels.INSTANCE.increaseSize());
        Button popupwin = new Button(Labels.INSTANCE.popup());

        toolbar.add(smaller);
        toolbar.add(larger);

        toolbar.add(popupwin);

        ClickHandler resizelistener = new ClickHandler() {
            public void onClick(ClickEvent event) {
                Widget obj = main.getWidget(1);
                int width = obj.getOffsetWidth();

                if (event.getSource() == larger) {
                    width += resizeincrement;
                } else if (event.getSource() == smaller) {
                    width -= resizeincrement;
                }

                smaller.setEnabled(width > minwidth);
                larger.setEnabled(width < maxwidth);

                if (width >= minwidth && width <= maxwidth) {
                    main.remove(1);
                    main.add(wrap(cb, aspectratio, width));
                }
            }
        };

        larger.addClickHandler(resizelistener);
        smaller.addClickHandler(resizelistener);

        popupwin.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Widget obj = main.getWidget(1);

                int width = obj.getOffsetWidth();
                int height = obj.getOffsetHeight();

                // TODO Use SafeHtml

                String html = "<html><head><title>" + popup_title
                        + "</title></head><body>" + cb.display("100%", "100%")
                        + "</body></html>";

                Util.popupWindowHTML("_blank", width, height, html,
                        "toolbar=no,menubar=no,scrollbars=no,resizable=yes");
            }
        });

        smaller.setEnabled(false);
        larger.setEnabled(true);

        main.add(wrap(cb, aspectratio, minwidth));
    }

    private Widget wrap(DisplayCallback cb, double aspectratio, int width) {
        int height = (int) (width * aspectratio);

        String width_css = width + "px";
        String height_css = height + "px";

        HTML w = new HTML(cb.display(width_css, height_css));

        w.setWidth(width_css);
        w.setHeight(height_css);

        return w;
    }

    public InsertPanel toolbar() {
        return toolbar;
    }
}
