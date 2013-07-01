package rosa.scanvas.demo.website.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellList.Style;

public interface CellListResources extends CellList.Resources {
	
	@Source(
		"rosa/scanvas/demo/website/client/CellList.css")
	CellList.Style cellListStyle();

	interface ListStyle extends CellList.Style {
		
	}
}
