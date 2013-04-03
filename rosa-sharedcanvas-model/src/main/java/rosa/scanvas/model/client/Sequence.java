package rosa.scanvas.model.client;

public interface Sequence extends ResourceMap {
    String label();

    String readingDirection();
    
    Canvas canvas(int index);

    int length();
    
    // TODO sc:hasContentRange
}
