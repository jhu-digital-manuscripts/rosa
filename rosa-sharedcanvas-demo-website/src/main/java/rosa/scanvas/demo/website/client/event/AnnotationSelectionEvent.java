package rosa.scanvas.demo.website.client.event;

import rosa.scanvas.model.client.Annotation;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Indicates that an annotaion has been selected in the sidebar, 
 * to be show or hidden in the content area
 */
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
	
	/**
	 * The boolean status of the annotation: TRUE is visible, 
	 * FALSE is hidden
	 */
	public boolean getStatus() { return status; }
	
	/**
	 * The panel id
	 */
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
