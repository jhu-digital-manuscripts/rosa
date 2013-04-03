package rosa.scanvas.model.client;

import java.util.Date;
import java.util.List;

/**
 * An OAI-ORE Resource Map.
 */
public interface ResourceMap {
    String uri();

    String aggregation_uri();

    Date modified();

    String creatorName();

    /**
     * @return List of all resources which are aggregated.
     */
    List<String> aggregates();

    /**
     * @param type_uri
     * @param type
     * @return References to aggregated resources of the given type.
     */
    <T> List<Reference<T>> aggregatedReferences(String type_uri, Class<T> type);
}
