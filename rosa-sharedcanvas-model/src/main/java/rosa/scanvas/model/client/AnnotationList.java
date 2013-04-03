package rosa.scanvas.model.client;

public interface AnnotationList extends ResourceMap {
    String label();

    Annotation annotation(int index);

    int length();

    // TODO forCanvas, forMotivation
}
