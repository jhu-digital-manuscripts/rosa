package rosa.scanvas.demo.website.client.view;

import java.util.List;

import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.demo.website.client.presenter.CollectionPresenter;

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
	
    // TODO Should be in css.
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
	
	public void setData(List<Reference<Manifest>> data) {
		manifestList.removeAllRows();
		
		System.out.println("Data List size: "+data.size());
		
		for (int i=0; i<data.size(); i++) {
			DecoratorPanel panel = new DecoratorPanel();
			panel.setWidth("100%");
			panel.add(new Label(data.get(i).label()));
			
			manifestList.setWidget(i, 0, panel);
		}
	}

	public int getSelectedRow(ClickEvent event) { 
		int selectedRow = -1;
		HTMLTable.Cell cell = manifestList.getCellForEvent(event);
		
		if (cell != null) {
			selectedRow = cell.getRowIndex();
		}
		
		return selectedRow; 
	}
	
	public HasText getViewLabel() { return viewLabel; }
	public HasClickHandlers getList() { return manifestList; }
	public Widget asWidget() { return this; }
	
}
