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
    List<RdfNode> withRdfType(String type);

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
     * Return the first triple found which matches the subject and predicate and
     * return the string value of the object.
     * 
     * @param subject
     *            , null for any subject
     * @param predicate
     *            , null for any predicate
     * @return object or null for none found
     */
    String findObjectStringValue(String subject, String predicate);

    int size();
}
