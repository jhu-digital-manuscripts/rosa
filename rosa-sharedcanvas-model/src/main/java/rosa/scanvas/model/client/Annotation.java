package rosa.scanvas.model.client;

import java.util.List;

public interface Annotation {
    String uri();

    AnnotationBody body();

    List<String> targets();

    String motivatedBy();
}
