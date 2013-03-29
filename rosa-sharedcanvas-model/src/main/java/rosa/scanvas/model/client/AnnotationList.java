package rosa.scanvas.model.client;

public interface AnnotationList extends ResourceMap {
    Annotation annotation(int index);

    int length();
}
