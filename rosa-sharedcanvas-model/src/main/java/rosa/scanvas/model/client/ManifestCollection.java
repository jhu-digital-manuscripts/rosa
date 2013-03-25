package rosa.scanvas.model.client;

import java.util.List;

public interface ManifestCollection {
    String id();

    String label();

    List<Manifest> manifests();
}
