package rosa.scanvas.model.client;

import java.util.List;

public interface Manifest extends ResourceMap {
    List<Sequence> sequences();

    String label();

    String agent();

    String date();

    String rights();

    String source();

    List<AnnotationList> annotationsLists();
}
