package rosa.scanvas.viewer.client.widgets;

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
	
	public void setData(String[] data) {
		for(int i=0; i<(data.length/THUMB_PANEL_WIDTH); i++) {
			for(int j=0; j<THUMB_PANEL_WIDTH; j++) {
				thumbTable.setWidget(i, j, new Label(data[i*THUMB_PANEL_WIDTH + j]));
			}
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
