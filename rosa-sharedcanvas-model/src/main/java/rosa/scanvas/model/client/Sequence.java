package rosa.scanvas.model.client;


public interface Sequence {
    String label();

    Canvas canvas(int index);
    
    int length();
}
