package rosa.scanvas.model.client;

import java.util.List;

public interface Manifest extends ResourceMap {
    String label();

    String agent();

    String date();

    String location();

    String rights();

    String description();

    String hasRelatedDescription();

    String hasRelatedService();

    List<Reference<Sequence>> sequences();

    List<Reference<AnnotationList>> annotationsLists();
}
