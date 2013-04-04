package rosa.scanvas.viewer.client.view;

import java.util.List;

import rosa.scanvas.viewer.client.presenter.CollectionPresenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class CollectionView extends Composite implements CollectionPresenter.Display {
	
	private final String LIST_WIDTH = "30em";
	
	private FlexTable manifestList = new FlexTable();
	private Label viewLabel = new Label();
	
	public CollectionView() {
		DockPanel mainPanel = new DockPanel();
		ScrollPanel scrollPanel = new ScrollPanel();
		initWidget(mainPanel);
		
		mainPanel.add(scrollPanel, DockPanel.CENTER);
		mainPanel.add(viewLabel, DockPanel.NORTH);
		scrollPanel.add(manifestList);
		scrollPanel.setSize(LIST_WIDTH, "10em");
	}
	
	public void setData(List data, Class type) {
		manifestList.removeAllRows();
		
		for (int i=0; i<data.size(); i++) {
			DecoratorPanel panel = new DecoratorPanel();
			panel.setWidth("100%");
			panel.add(new Label(data.get(i).toString()));
			
			manifestList.setWidget(i, 0, panel);
		}
		
		/*Iterator it = data.iterator();
		
		while (it.hasNext()) {
			int i = 0;
			
			DecoratorPanel panel = new DecoratorPanel();
			panel.setWidth("100%");
			panel.add(new Label(it.next().toString()));
			
			manifestList.setWidget(i++, 0, panel);
		}*/
	}

	public int getSelectedRow(ClickEvent event) { 
		int selectedRow = -1;
		HTMLTable.Cell cell = manifestList.getCellForEvent(event);
		
		if (cell != null && cell.getCellIndex() > 0) {
			selectedRow = cell.getRowIndex();
		}
		
		return selectedRow; 
	}
	
/*	public String getSelectedManifest() { 
		String data = null;
		
		for(int i=0; i<manifestList.getRowCount(); i++) {
			CheckBox checkBox = (CheckBox)manifestList.getWidget(i, 0);
			if (checkBox.getValue()) {
				data = String.valueOf(i);
			}
		}
		
		return data; 
	}*/
	
	public HasText getViewLabel() { return viewLabel; }
	public HasClickHandlers getList() { return manifestList; }
	public Widget asWidget() { return this; }

	public void setLabel(String label) {
		viewLabel.setText(label);
	}
	
}
