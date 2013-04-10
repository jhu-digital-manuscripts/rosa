package rosa.scanvas.demo.website.client.widgets;

import java.util.Iterator;

import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class ThumbnailWidget extends Composite {
	
	private final int THUMB_PANEL_WIDTH = 5;

	private FlowPanel mainPanel = new FlowPanel();
	private FlowPanel searchPanel = new FlowPanel();
	private ScrollPanel tablePanel = new ScrollPanel();
	
	private FlexTable thumbTable = new FlexTable();
	private TextBox searchBox = new TextBox();
	
	private Button searchButton = new Button();
	private Button closePanelButton = new Button();
	
	public ThumbnailWidget() {
		initWidget(mainPanel);
		mainPanel.add(searchPanel);
		mainPanel.add(tablePanel);
		
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		searchPanel.add(closePanelButton);
		
		tablePanel.add(thumbTable);
		
		/*mainPanel.setHeight("100%");
		mainPanel.setWidth("100%");*/
		searchButton.setText("Search");
		closePanelButton.setText("Close Panel");
	}
	
	public void setData(Sequence sequence) {
		searchPanel.add(new Label("Size: "+sequence.size()));
		int index = 0;
		
		Iterator<Canvas> it = sequence.iterator();
		while (it.hasNext()) {
			Canvas canvas = it.next();
			
			thumbTable.setWidget(index/4, index%4, new Label(canvas.label(), true));
			index++;
		}
		
	}
	
	public int getSelectedRow(ClickEvent event) {
		int selectedRow = -1;
		HTMLTable.Cell cell = thumbTable.getCellForEvent(event);
		
		if (cell != null) {
			selectedRow = cell.getRowIndex();
		}
		
		return selectedRow;
	}
	
	public int getSelectedColumn(ClickEvent event) {
		int selectedCol = -1;
		HTMLTable.Cell cell = thumbTable.getCellForEvent(event);
		
		if (cell != null) {
			selectedCol = cell.getCellIndex();
		}
		
		return selectedCol;
	}

	public ScrollPanel getTablePanel() { return tablePanel; }
	public void setTablePanel(ScrollPanel tablePanel) { this.tablePanel = tablePanel; }
	public FlexTable getThumbTable() { return thumbTable; }
	public TextBox getSearchBox() { return searchBox; }
	public Button getSearchButton() { return searchButton; }
	public Button getClosePanelButton() { return closePanelButton; }
	
}
