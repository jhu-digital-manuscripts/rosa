package rosa.scanvas.model.client.rdf;

public class RdfException extends RuntimeException {

    public RdfException() {
        super();
    }

    public RdfException(String message, Throwable cause) {
        super(message, cause);
    }

    public RdfException(String message) {
        super(message);
    }

    public RdfException(Throwable cause) {
        super(cause);
    }
}
