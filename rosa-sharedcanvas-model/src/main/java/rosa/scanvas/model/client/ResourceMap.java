package rosa.scanvas.model.client;

import java.util.Date;
import java.util.List;

/**
 * An OAI-ORE Resource Map.
 */
public interface ResourceMap {
    String url();

    String aggregation();

    Date modified();

    String creatorName();

    List<String> aggregates();
}
