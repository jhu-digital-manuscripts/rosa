package rosa.scanvas.demo.website.client;

import java.util.ArrayList;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.Canvas;

public class PanelData {
	
	private ManifestCollection collection;
	private Manifest manifest;
	private Sequence sequence;
	private Canvas canvas;
	private List<AnnotationList> annotationLists;
	private List<Annotation> visibleAnnotations;
	
	private List<Annotation> imageAnnotations;
	public List<Annotation> getImageAnnotations() {
		return imageAnnotations;
	}
	
	public PanelData() {
		annotationLists = new ArrayList<AnnotationList>();
		visibleAnnotations = new ArrayList<Annotation>();
		imageAnnotations = new ArrayList<Annotation>();
	}
	
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
		
		annotationLists.clear();
		visibleAnnotations.clear();
		imageAnnotations.clear();
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
	public List<AnnotationList> getAnnotationLists() {
		return annotationLists;
	}
	public List<Annotation> getVisibleAnnotations() {
		return visibleAnnotations;
	}
	
	
	public String tokenForData() {
		String token = "";
		
		if (collection != null) { token += collection.uri(); }
		if (manifest != null) { token += manifest.uri(); }
		if (sequence != null) { token += sequence.uri(); }
		if (canvas != null) { token += canvas.uri(); }
		
		return token;
	}
	
	
	public void clearCollection() {
		setCollection(null);
		clearManifest();
	}
	public void clearManifest() {
		setManifest(null);
		clearSequence();
	}
	public void clearSequence() {
		setSequence(null);
		clearCanvas();
	}
	public void clearCanvas() {
		setCanvas(null);
	}
}
