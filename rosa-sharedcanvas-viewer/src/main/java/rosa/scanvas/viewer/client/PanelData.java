package rosa.scanvas.viewer.client;

import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.Canvas;

public class PanelData {
	
	private ManifestCollection collection;
	private Manifest manifest;
	private Sequence sequence;
	private Canvas canvas;
	
	public ManifestCollection getCollection() {
		return collection;
	}
	public void setCollection(ManifestCollection collection) {
		this.collection = collection;
	}
	public Manifest getManifest() {
		return manifest;
	}
	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}
	public Sequence getSequence() {
		return sequence;
	}
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}
	public Canvas getCanvas() {
		return canvas;
	}
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
}
