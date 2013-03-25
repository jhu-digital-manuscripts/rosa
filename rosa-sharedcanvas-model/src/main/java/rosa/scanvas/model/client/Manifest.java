package rosa.scanvas.model.client;

import java.util.List;

public interface Manifest {
    String id();

    List<Sequence> sequences();

    String label();

    String agent();

    String date();
}
