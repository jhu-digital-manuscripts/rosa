package rosa.scanvas.model.client.rdf;

import java.util.List;

public interface RdfGraph extends Iterable<RdfTriple> {
    /**
     * Return matching triples.
     * 
     * @param subject
     *            , null for any
     * @param predicate
     *            , null for any
     * @param object
     *            , null for any
     * @return matching triples
     * @throws RdfException
     */
    List<RdfTriple> find(String subject, String predicate, String object);

    /**
     * Return all subjects with the given RDF type.
     * 
     * @param type
     * @return matching subject nodes.
     * @throws RdfException
     */
    List<RdfNode> findRdfTypes(String type);

    /**
     * Return the first triple found which matches the subject and predicate and
     * return the object.
     * 
     * @param subject
     *            , null for any subject
     * @param predicate
     *            , null for any predicate
     * @return object or null for none found
     */
    RdfNode findObject(String subject, String predicate);

    /**
     * Return the string value of the object of the first triple found which
     * matches the subject and predicate.
     * 
     * 
     * @param subject
     *            , null for any subject
     * @param predicate
     *            , null for any predicate
     * @return object or null for none found
     */
    String findObjectStringValue(String subject, String predicate);

    /**
     * Return string values of all objects matching the subject and predicate.
     * 
     * @param subject
     * @param predicate
     * @return matching object string values
     */
    List<String> findObjectStringValues(String subject, String predicate);

    /**
     * @return number of triples in the graph.
     */
    int size();

    /**
     * Return the number value of the object of the first triple found which
     * matches the subject and predicate.
     * 
     * @param uri
     * @param exifWidth
     * @param missing
     * @return value or missing if not found or not a number
     */
    double findObjectNumberValue(String uri, String exifWidth, double missing);
}
