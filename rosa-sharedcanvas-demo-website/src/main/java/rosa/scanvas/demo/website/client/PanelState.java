package rosa.scanvas.demo.website.client;

public class PanelState {
	
	private PanelView view;
	private String objectUri;
	private String manifestUri;
	
	PanelState() {
		view = PanelView.HOME;
	}
	
	PanelState(PanelView view, String objectUri) {
		this.view = view;
		this.objectUri = objectUri;
	}
	
	PanelState(PanelView view, String objectUri, String manifestUri) {
		this.view = view;
		this.objectUri = objectUri;
		this.manifestUri = manifestUri;
	}
	
	public PanelView getView() {
		return view;
	}
	
	public void setView(PanelView view) {
		this.view = view;
	}
	
	public String getObjectUri() {
		return objectUri;
	}
	
	public void setObjectUri(String objectUri) {
		if (!view.equals(PanelView.HOME)){ 
			this.objectUri = objectUri;
		} else {
			this.objectUri = null;
		}
	}
	
	public String getManifestUri() {
		return manifestUri;
	}
	
	public void setManifestUri(String manifestUri) {
		if (view.equals(PanelView.CANVAS) || view.equals(PanelView.SEQUENCE)) {
			this.manifestUri = manifestUri;
		} else {
			this.manifestUri = null;
		}
	}
	
}
