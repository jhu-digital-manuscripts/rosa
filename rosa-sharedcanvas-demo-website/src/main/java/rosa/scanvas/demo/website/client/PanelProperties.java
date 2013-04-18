package rosa.scanvas.demo.website.client;


public class PanelProperties {
	
	private int row;
	private int col;
	private int tab;
	private int index;
	private final String id;
	private final String view;
	
	public PanelProperties(int index, String id, String view, int tab) {
		row = index/2;
		col = index%2;
		this.id = id;
		this.tab = tab;
		this.view = view;
		this.index = index;
	}
	
	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}
	
	public void setIndex(int index) {
		this.index = index;
		row = index/2;
		col = index%2;
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
	
	public int getTab() {
		return tab;
	}
	
	@Override
    public int hashCode() {
        return id.hashCode();
    }
	
	@Override
	public boolean equals(Object o) {
		if ( !(o instanceof PanelProperties) ) {
			return false;
		}
		
		PanelProperties props = (PanelProperties) o;
		return id.equals(props.getId());
	}
	
}
