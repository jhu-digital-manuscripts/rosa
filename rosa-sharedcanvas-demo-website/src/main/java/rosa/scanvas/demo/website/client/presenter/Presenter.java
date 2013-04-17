package rosa.scanvas.demo.website.client.presenter;

import rosa.scanvas.demo.website.client.PanelData;

import com.google.gwt.user.client.ui.HasWidgets;

public interface Presenter {
	public void go(HasWidgets container);
	public void setData(PanelData data);
	public void setSize(String width, String height);
}
