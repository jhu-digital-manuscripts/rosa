package rosa.scanvas.model.client.jsonld;

import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationBody;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.AnnotationSelector;
import rosa.scanvas.model.client.AnnotationTarget;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class GwtTestSCTestData extends AbstractGwtTest {
	private final static String BASE_URL = 
			"http://rosetest.library.jhu.edu/sctest/";
			//"http://localhost:8080/rosa-sharedcanvas-test-endpoint/";
	private final static String MANIFEST_ENDPOINT = BASE_URL
			+ "manifest";
	private final static String ANNOTATION_LIST_ENDPOINT = BASE_URL 
			+ "canvas/14r/annotations";
	private final static String SEQUENCE_ENDPOINT = BASE_URL
			+ "sequence";
	private final static String TARGETED_LIST_ENDPOINT = BASE_URL
			+ "canvas/14r/transcriptions";
	
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
	
	private void checkManifest(Manifest manifest) {
		assertNotNull(manifest);
		assertNotNull(manifest.label());
		assertNotNull(manifest.rights());
		
		List<String> aggregates = manifest.aggregates();
		List<Reference<Sequence>> sequenceList = manifest.sequences();
		List<Reference<AnnotationList>> annoLists = manifest.annotationsLists();
		
		check_refs(sequenceList, Sequence.class, aggregates);
		check_refs(annoLists, AnnotationList.class, aggregates);
	}
	
	private void checkSequence(Sequence sequence) {
		assertNotNull(sequence);
		assertNotNull(sequence.label());
		assertNotNull(sequence.readingDirection());
		assertTrue(sequence.size() > 0);
		
		List<String> aggregates = sequence.aggregates();
		
		assertTrue(aggregates.size() > 0);
        assertEquals(aggregates.size(), sequence.size());
        
        for (Canvas canv : sequence) {
        	assertNotNull(canv.uri());
            assertNotNull(canv.label());
            
            assertTrue(canv.height() > 0);
            assertTrue(canv.width() > 0);

            assertTrue(aggregates.contains(canv.uri()));
            
            assertNotNull(canv.hasAnnotations());
        }
	}
	
	private void checkAnnotationList(AnnotationList annoList) {
		assertNotNull(annoList);
        assertNotNull(annoList.uri());
        assertNotNull(annoList.forCanvas());
       
        List<String> aggregates = annoList.aggregates();

        assertTrue(aggregates.size() > 0);

        assertEquals(aggregates.size(), annoList.size());

        for (int i = 0; i < annoList.size(); i++) {
            Annotation anno = annoList.annotation(i);

            assertNotNull(anno);
            assertNotNull(anno.uri());

            assertTrue(anno.targets().size() > 0);

            for (AnnotationTarget target : anno.targets()) {
                assertNotNull(target.uri());
                if (target.isSpecificResource()) {
                	assertNotNull(target.hasSource());
                	
                	AnnotationSelector selector = target.hasSelector();
                    assertNotNull(selector);
                    
                    assertTrue(selector.isSvgSelector());
                    assertTrue(selector.hasTextContent());
                    assertNotNull(selector.textContent());
                    assertFalse(selector.textContent().isEmpty());
                } else {
                	assertNull(target.hasSource());
                	assertNull(target.hasSelector());
                }
            }

            AnnotationBody body = anno.body();
            assertNotNull(body);

            assertNotNull(body.uri());
            assertNotNull(body.format());

            if (body.isImage()) {
                assertNull(body.defaultItem());
                assertTrue(body.otherItems().isEmpty());
                assertTrue(body.format().startsWith("image"));
                assertEquals("IIIF", body.conformsTo());
            } else if (body.isText()) {
                assertNull(body.defaultItem());
                assertTrue(body.otherItems().isEmpty());

                assertTrue(body.format().startsWith("text"));
                assertNotNull(body.textContent());
                assertFalse(body.textContent().isEmpty());
            }
        }
	}
	
	public void testManifest() {
		checkRemoteSharedCanvas(MANIFEST_ENDPOINT, Manifest.class, 
				new AsyncCallback<Manifest>() {
			public void onFailure(Throwable error) {
				fail("Failed to load the SC test manifest:: " 
						+ error.getMessage());
			}
			
			public void onSuccess(Manifest result) {
				checkManifest(result);
			}
		});
	}
	
	public void testSequence() {
		checkRemoteSharedCanvas(SEQUENCE_ENDPOINT, Sequence.class,
				new AsyncCallback<Sequence>() {
			public void onFailure(Throwable error) {
				fail("Failed to load the SC test sequence:: "
						+ error.getMessage());
			}
			
			public void onSuccess(Sequence result) {
				checkSequence(result);
			}
		});
	}
	
	/**
	 * This annotation list is for a single canvas and includes
	 * image annotations and text annotations with SVG selectors
	 */
	public void testAnnotationList() {
		checkRemoteSharedCanvas(ANNOTATION_LIST_ENDPOINT, AnnotationList.class,
				new AsyncCallback<AnnotationList>() {
			public void onFailure(Throwable error) {
				fail("Failed to load the SC test annotation list:: "
						+ error.getMessage());
			}
			
			public void onSuccess(AnnotationList result) {
				checkAnnotationList(result);
			}
		});
	}
	
	/**
	 * This list includes only text annotations with SVG selectors
	 */
	public void testTargetedAnnotationList() {
		checkRemoteSharedCanvas(TARGETED_LIST_ENDPOINT, AnnotationList.class,
				new AsyncCallback<AnnotationList>() {
			public void onFailure(Throwable error) {
				fail("Failed to load the SC test annotation list:: "
						+ error.getMessage());
			}
			
			public void onSuccess(AnnotationList result) {
				checkAnnotationList(result);
			}
		});
	}
	
}
