package rosa.scanvas.viewer.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class DockPanel extends DockLayoutPanel {

	public DockPanel(Unit unit) {
		super(unit);
	}
	
	@Override
	public void add(Widget widget) {
		if (getCenter() != null) {
			remove(getCenter());
		}
		super.add(widget);
	}

}
