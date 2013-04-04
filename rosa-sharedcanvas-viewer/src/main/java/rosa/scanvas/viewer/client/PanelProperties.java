package rosa.scanvas.viewer.client;

import java.util.List;

import rosa.scanvas.model.client.ManifestCollection;

public class PanelProperties {
	
	private int row;
	private int col;
	private int index;
	private final String id;
	private final String view;
	private List dataList;
	private ManifestCollection collection;
	
	public PanelProperties(int index, String id, String view) {
		row = index/2;
		col = index%2;
		this.id = id;
		this.view = view;
		this.index = index;
	}
	
/*	public PanelProperties(int index, String id, String view, List dataList) {
		row = index/2;
		col = index%2;
		this.id = id;
		this.view = view;
		this.index = index;
		this.dataList = dataList;
	}*/

	public void setData(ManifestCollection collection) {
		this.collection = collection;
		dataList = collection.manifests();
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
	
	public ManifestCollection getCollection() {
		return collection;
	}
	
	public List getDataList() {
		return dataList;
	}
	
}
