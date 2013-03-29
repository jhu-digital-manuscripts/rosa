package rosa.scanvas.model.client.impl;

import java.util.List;

import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.rdf.RdfDataset;
import rosa.scanvas.model.client.rdf.RdfException;

public class ManifestImpl extends ResourceMapImpl implements Manifest {
    public ManifestImpl(RdfDataset ds) throws RdfException {
        super(ds);
    }

    @Override
    public List<Sequence> sequences() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String label() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String agent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String date() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String rights() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String source() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AnnotationList> annotationsLists() {
        // TODO Auto-generated method stub
        return null;
    }
}
