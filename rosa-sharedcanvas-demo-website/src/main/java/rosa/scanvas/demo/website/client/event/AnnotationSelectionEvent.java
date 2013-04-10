package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.model.client.Annotation;

import com.google.gwt.event.shared.GwtEvent;

public class AnnotationSelectionEvent extends GwtEvent<AnnotationSelectionHandler> {
	public static Type<AnnotationSelectionHandler> TYPE = new Type<AnnotationSelectionHandler>();
	private final Annotation annotation;
	private final boolean status;
	private final int panel;
	
	public AnnotationSelectionEvent(Annotation annotation, boolean status, int panel) {
		this.annotation = annotation;
		this.status = status;
		this.panel = panel;
	}
	
	public Annotation getAnnotation() { return annotation; }
	public boolean getStatus() { return status; }
	public int getPanel() { return panel; }
	
	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<AnnotationSelectionHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AnnotationSelectionHandler handler) {
		handler.onSelection(this);
	}

}
