package rosa.scanvas.model.client;

import java.util.List;

public interface Annotation {
    String uri();

    String label();
    
    AnnotationBody body();

    List<AnnotationTarget> targets();

    String motivatedBy();
}
