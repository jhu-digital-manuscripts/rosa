package rosa.scanvas.demo.website.client;


public class PanelProperties {
	
	private int row;
	private int col;
	private int index;
	private final String id;
	private final String view;
	
	public PanelProperties(int index, String id, String view) {
		row = index/2;
		col = index%2;
		this.id = id;
		this.view = view;
		this.index = index;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getId() {
		return id;
	}
	
	public String getView() {
		return view;
	}
	
}
