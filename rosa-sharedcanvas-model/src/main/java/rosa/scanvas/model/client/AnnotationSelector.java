package rosa.scanvas.model.client;

public interface AnnotationSelector {
    boolean isType(String type_uri);

    boolean isFragmentSelector();

    String conformsTo();

    String fragmentValue();

    boolean isSvgSelector();

    boolean hasTextContent();

    String textContent();
}
