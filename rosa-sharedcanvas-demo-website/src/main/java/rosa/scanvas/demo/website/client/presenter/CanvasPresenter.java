package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;
import rosa.scanvas.demo.website.client.PanelProperties;
import rosa.scanvas.demo.website.client.disparea.DisplayAreaWidget;
import rosa.scanvas.demo.website.client.disparea.DisplayElement;
import rosa.scanvas.model.client.Annotation;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CanvasPresenter implements Presenter {
    public interface Display {
        Label getLabel();

        DisplayAreaWidget getDisplayAreaWidget();

        Widget asWidget();
    }

    private final Display display;
    private final HandlerManager event_bus;
    private final PanelProperties props;

    public CanvasPresenter(Display display, HandlerManager eventBus,
            PanelProperties props) {
        this.display = display;
        this.event_bus = eventBus;
        this.props = props;
    }

    @Override
    public void go(HasWidgets container) {
        if (container instanceof FlexTable) {
            ((FlexTable) container).setWidget(props.getRow(), props.getCol(),
                    display.asWidget());
        }

    }

    // TODO Can save display elements and operate on them for efficiency
    private void setAnnotationVisible(Annotation ann, boolean status) {
        DisplayAreaWidget da = display.getDisplayAreaWidget();
        DisplayElement el = da.area().get(ann.uri());

        if (el != null) {
            el.setVisible(status);
        }

        da.redraw();
    }

    @Override
    public void setData(PanelData data) {
        DisplayAreaWidget da = display.getDisplayAreaWidget();

        
        
    }


    @Override
    public void setSize(int width, int height) {

    }
    
    @Override
    public void setIndex(int index) {
    	// used to update the index (row and column) in the PanelProperties, due to the use of a FlexTable
    }
}
