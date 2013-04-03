package rosa.scanvas.model.client;

import java.util.List;

public interface AnnotationBody {
    boolean isSingleton();

    boolean isChoice();

    boolean isComposite();

    AnnotationBody defaultItem();

    List<AnnotationBody> items();

    public String format();

}
