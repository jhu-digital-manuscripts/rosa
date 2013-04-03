package rosa.scanvas.model.client.rdf.impl;

import rosa.scanvas.model.client.rdf.RdfException;
import rosa.scanvas.model.client.rdf.RdfDataset;
import rosa.scanvas.model.client.rdf.RdfGraph;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class RdfDatasetJson implements RdfDataset {
    private final JSONObject dataset;

    public RdfDatasetJson(JSONObject o) throws RdfException {
        this.dataset = o;

        if (dataset == null || dataset.isObject() == null
                || !dataset.containsKey("@default")) {
            throw new RdfException("Invalid dataset: " + dataset);
        }
    }

    @Override
    public RdfGraph defaultGraph() throws RdfException {
        return graph("@default");
    }

    @Override
    public String[] graphNames() {
        String[] result = new String[dataset.size() - 1];

        int next = 0;
        for (String key : dataset.keySet()) {
            if (!key.equals("@default")) {
                result[next++] = key;
            }
        }

        return result;
    }

    @Override
    public RdfGraph graph(String name) throws RdfException {
        JSONValue val = dataset.get(name);

        if (val == null) {
            return null;
        }

        return new RdfGraphJson(val.isArray());
    }

    public String toString() {
        return dataset.toString();
    }
}
