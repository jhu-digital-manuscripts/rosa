package rosa.scanvas.viewer.client.presenter;

import rosa.scanvas.viewer.client.PanelData;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter {
	public void go(HasWidgets container);
	public void setData(PanelData data);
}
