package rosa.scanvas.model.client;

import java.util.List;

public interface AnnotationBody {
    String uri();

    boolean isChoice();

    boolean isComposite();

    boolean isText();

    boolean isImage();
    
    boolean isType(String type_uri);
    
    String textContent();

    AnnotationBody defaultItem();

    List<AnnotationBody> otherItems();

    String format();

    String conformsTo();
}
