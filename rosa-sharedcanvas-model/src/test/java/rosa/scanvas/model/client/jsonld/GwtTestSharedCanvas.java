package rosa.scanvas.model.client.jsonld;

import java.util.List;

import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtTestSharedCanvas extends AbstractGwtTest {
    private final static String COLLECTION_ENDPOINT = "http://rosetest.library.jhu.edu/sc/";
    private final static String MANIFEST_ENDPOINT = COLLECTION_ENDPOINT
            + "Douce195";

    private <T> void check_refs(List<Reference<T>> refs, Class<T> type,
            List<String> aggregates) {
        for (Reference<T> ref : refs) {
            assertNotNull(ref.uri());

            String label = ref.label();

            if (label != null) {
                assertFalse(label.isEmpty());
            }

            assertEquals(type, ref.type());
            assertTrue(aggregates.contains(ref.uri()));
        }
    }

    private void check_manifest_collection(ManifestCollection col) {
        assertNotNull(col);
        assertNotNull(col.label());
        assertNotNull(col.creatorName());

        List<String> aggregates = col.aggregates();
        List<Reference<Manifest>> refs = col.manifests();

        assertTrue(aggregates.size() > 0);
        assertTrue(refs.size() > 0);
        assertEquals(aggregates.size(), refs.size());

        check_refs(refs, Manifest.class, aggregates);
    }

    private void check_manifest(Manifest man) {
        assertNotNull(man);
        assertNotNull(man.label());
        assertNotNull(man.creatorName());
        assertNotNull(man.agent());
        assertNotNull(man.date());
        assertNotNull(man.hasRelatedDescription());

        List<String> aggregates = man.aggregates();

        assertTrue(aggregates.size() > 0);

        List<Reference<AnnotationList>> als = man.annotationsLists();
        List<Reference<Sequence>> seqs = man.sequences();

        assertEquals(aggregates.size(), als.size() + seqs.size());

        check_refs(als, AnnotationList.class, aggregates);
        check_refs(seqs, Sequence.class, aggregates);
    }

    public void testManifestCollection() {
        checkRemoteSharedCanvas(COLLECTION_ENDPOINT, ManifestCollection.class,
                new AsyncCallback<ManifestCollection>() {
                    public void onFailure(Throwable error) {
                        fail(error.getMessage());
                    }

                    public void onSuccess(ManifestCollection col) {
                        check_manifest_collection(col);
                    }
                });
    }

    public void testManifest() {
        checkRemoteSharedCanvas(MANIFEST_ENDPOINT, Manifest.class,
                new AsyncCallback<Manifest>() {
                    public void onFailure(Throwable error) {
                        fail(error.getMessage());
                    }

                    public void onSuccess(Manifest man) {
                        check_manifest(man);
                    }
                });
    }
}
