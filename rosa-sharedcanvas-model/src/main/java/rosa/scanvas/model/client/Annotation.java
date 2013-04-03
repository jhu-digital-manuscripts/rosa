package rosa.scanvas.model.client;

import java.util.List;

public interface Annotation {
    List<AnnotationBody> bodies();

    List<AnnotationTarget> targets();

    List<AnnotationList> annotationLists();
}
