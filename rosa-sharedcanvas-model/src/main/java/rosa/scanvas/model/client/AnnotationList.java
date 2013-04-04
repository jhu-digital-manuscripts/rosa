package rosa.scanvas.model.client;

/**
 * Ordered list of annotations.
 */
public interface AnnotationList extends ResourceMap, Iterable<Annotation> {
    String label();

    Annotation annotation(int index);

    int size();

    String forCanvas();

    String forMotivation();
}
