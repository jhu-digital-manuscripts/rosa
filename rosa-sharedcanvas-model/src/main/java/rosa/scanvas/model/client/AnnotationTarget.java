package rosa.scanvas.model.client;

public interface AnnotationTarget {
    String uri();
    
    boolean isSpecificResource();
    
    String hasSource();
    
    AnnotationSelector hasSelector();
}
