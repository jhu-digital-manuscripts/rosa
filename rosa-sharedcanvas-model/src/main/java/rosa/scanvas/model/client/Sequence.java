package rosa.scanvas.model.client;

/**
 * An ordered list of canvases.
 */
public interface Sequence extends ResourceMap, Iterable<Canvas> {
    String label();

    String readingDirection();

    Canvas canvas(int index);

    /**
     * @return Number of canvases in sequence.
     */
    int size();

    Range hasContentRange();
}
