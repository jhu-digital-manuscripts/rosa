package rosa.scanvas.model.client;

/**
 * A reference to an instance of a type with a uri and a label.
 */
public interface Reference<T> {
    String uri();

    Class<T> type();

    String label();
}
