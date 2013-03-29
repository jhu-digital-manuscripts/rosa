package rosa.scanvas.model.client.rdf;

public interface RdfDataset {
    RdfGraph defaultGraph();

    String[] graphNames();

    RdfGraph graph(String name);
}
