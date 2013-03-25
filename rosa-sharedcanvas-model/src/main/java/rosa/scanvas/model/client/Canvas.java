package rosa.scanvas.model.client;

import java.util.List;

public interface Canvas {
    String label();

    AnnotationBody body();

    List<AnnotationTarget> targets();

    List<AnnotationList> annotationLists();
}
