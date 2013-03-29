package rosa.scanvas.model.client;

public interface Sequence extends ResourceMap {
    String label();

    Canvas canvas(int index);

    int length();
}
