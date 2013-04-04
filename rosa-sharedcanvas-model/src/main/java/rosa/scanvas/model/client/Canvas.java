package rosa.scanvas.model.client;

import java.util.List;

public interface Canvas {
    String uri();
    
    String label();

    int width();

    int height();

    List<Reference<AnnotationList>> hasAnnotations();
}
