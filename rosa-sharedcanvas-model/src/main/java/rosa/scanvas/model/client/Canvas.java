package rosa.scanvas.model.client;

import java.util.List;

public interface Canvas {
    String label();

    int width();

    int height();

    List<AnnotationList> forCanvas();
}
