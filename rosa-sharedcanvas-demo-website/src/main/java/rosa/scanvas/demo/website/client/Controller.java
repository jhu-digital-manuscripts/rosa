package rosa.scanvas.demo.website.client;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasWidgets;

public interface Controller extends ValueChangeHandler<String> {
	public void go(HasWidgets container);
}
