package rosa.scanvas.model.client.rdf.impl;

import java.util.Iterator;

import rosa.scanvas.model.client.rdf.AbstractRdfGraph;
import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfTriple;

import com.google.gwt.json.client.JSONArray;

public class RdfGraphJson extends AbstractRdfGraph {
    private final JSONArray triples;

    public RdfGraphJson(JSONArray triples) throws RdfException {
        this.triples = triples;

        if (triples == null || triples.isArray() == null) {
            throw new RdfException("Invalid dataset: " + triples);
        }

        indexTriples();
    }

    @Override
    public int size() {
        return triples.size();
    }

    public String toString() {
        return triples.toString();
    }

    @Override
    public Iterator<RdfTriple> iterator() {
        return new Iterator<RdfTriple>() {
            int next = 0;

            @Override
            public boolean hasNext() {
                return next < triples.size();
            }

            @Override
            public RdfTriple next() {
                return new RdfTripleJson(triples.get(next++).isObject());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
