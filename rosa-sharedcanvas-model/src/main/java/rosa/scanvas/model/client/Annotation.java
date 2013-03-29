package rosa.scanvas.model.client;

import java.util.List;

public interface Annotation {

    AnnotationBody body();

    List<AnnotationTarget> targets();

    List<AnnotationList> annotationLists();
}
