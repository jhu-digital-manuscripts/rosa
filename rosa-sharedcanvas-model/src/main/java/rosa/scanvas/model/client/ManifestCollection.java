package rosa.scanvas.model.client;

import java.util.List;

public interface ManifestCollection extends ResourceMap {
    String label();

    List<Manifest> manifests();
}
