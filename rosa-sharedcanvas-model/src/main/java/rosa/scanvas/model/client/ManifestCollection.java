package rosa.scanvas.model.client;

import java.util.List;

/**
 * A labeled collection of references to manifests.
 */
public interface ManifestCollection extends ResourceMap {
    String label();

    List<Reference<Manifest>> manifests();
}
