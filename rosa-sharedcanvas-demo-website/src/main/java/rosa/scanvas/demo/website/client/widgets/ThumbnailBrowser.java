package rosa.scanvas.demo.website.client.widgets;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ThumbnailBrowser extends Composite {
    private final ScrollPanel container;
    private final FlowPanel content;

    public ThumbnailBrowser() {
        this.content = new FlowPanel();
        this.container = new ScrollPanel(content);

        // Update thumbs when scrolling

        final HandlerRegistration reg1 = container
                .addScrollHandler(new ScrollHandler() {
                    public void onScroll(ScrollEvent event) {
                        displayVisibleThumbs();
                    }
                });

        // Remove handlers when widget no longer attached

        content.addAttachHandler(new AttachEvent.Handler() {
            public void onAttachOrDetach(AttachEvent event) {
                if (!event.isAttached()) {
                    reg1.removeHandler();
                }
            }
        });

        initWidget(container);
    }

    /**
     * Set the thumbnails to display.
     */
    public void setThumbnails(List<Thumbnail> thumbs) {
        content.clear();

        for (Thumbnail thumb : thumbs) {
            content.add(thumb);
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                displayVisibleThumbs();
            }
        });
    }

    private void displayVisibleThumbs() {
        int left = container.getAbsoluteLeft();
        int right = left + container.getOffsetWidth();
        int top = container.getAbsoluteTop();
        int bottom = top + container.getOffsetHeight();

        // This widget may be occupying more space than is
        // viewable because an ancestor might be a ScrollPanel.

        // TODO Use scroll panel vertical scroll position

        for (int i = 0, n = content.getWidgetCount(); i < n; i++) {
            Thumbnail thumb = (Thumbnail) content.getWidget(i);

            int thumb_left = thumb.getAbsoluteLeft();
            int thumb_top = thumb.getAbsoluteTop();

            if (thumb_left >= left && thumb_top >= top && thumb_left < right
                    && thumb_top < bottom) {
                thumb.makeViewable();
            }
        }
    }

    public int getThumbnailIndex(Thumbnail thumb) {
        return content.getWidgetIndex(thumb);
    }
}
