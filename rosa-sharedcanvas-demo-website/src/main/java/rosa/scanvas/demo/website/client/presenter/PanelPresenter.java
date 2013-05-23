package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public interface PanelPresenter extends IsWidget {
    /**
     * @return View of the presenter.
     */
    Widget asWidget();

    /**
     * Completely reinitialize the view at the given size with the given data.
     * 
     * @param width
     * @param height
     * @param data
     */
    void display(PanelData data);

    /**
     * Set the pixel size of the view as specified.
     * 
     * @param width
     * @param height
     */
    void resize(int width, int height);
    
    /**
     * Set the selection status of the panel. Useful for styling.
     * 
     * @param is_selected
     */
    void selected(boolean is_selected);
}
