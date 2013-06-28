package rosa.scanvas.demo.website.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import rosa.scanvas.model.client.Annotation;
import rosa.scanvas.model.client.AnnotationList;
import rosa.scanvas.model.client.Canvas;
import rosa.scanvas.model.client.Manifest;
import rosa.scanvas.model.client.ManifestCollection;
import rosa.scanvas.model.client.Reference;
import rosa.scanvas.model.client.Sequence;
import rosa.scanvas.model.client.SharedCanvas;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;
/**
 * A container holding data operated on by a panel view.
 */
public class PanelData {
    private ManifestCollection collection;
    private Manifest manifest;
    private Sequence sequence;
    private Canvas canvas;
    private List<AnnotationList> annotationLists;
    
    private int zoom_level;
    private int[] position = new int[2];

    // TODO rethink this
    private List<Annotation> visibleAnnotations;
    private HashSet<String> annotation_status;
    private List<Annotation> imageAnnotations;

    /**
     * Load a Shared Canvas manifest
     * 
     * @param url
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to use when loading is done
     */
    public static void loadManifest(String url, final PanelData data,
            final AsyncCallback<PanelData> cb) {
        SharedCanvas.load(url, Manifest.class, new AsyncCallback<Manifest>() {
            @Override
            public void onFailure(Throwable err) {
                cb.onFailure(err);
            }

            @Override
            public void onSuccess(Manifest man) {
                data.setManifest(man);
                cb.onSuccess(data);
            }
        });
    }

    /**
     * Load a collection of Shared Canvas manifests
     * 
     * @param url
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to use when loading is done
     */
    public static void loadManifestCollection(String url, final PanelData data,
            final AsyncCallback<PanelData> cb) {
        SharedCanvas.load(url, ManifestCollection.class,
                new AsyncCallback<ManifestCollection>() {
                    @Override
                    public void onFailure(Throwable err) {
                        cb.onFailure(err);
                    }

                    @Override
                    public void onSuccess(ManifestCollection col) {
                        data.setManifestCollection(col);
                        cb.onSuccess(data);
                    }
                });
    }

    /**
     * Load a Shared Canvas sequence
     * 
     * @param url
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to use when loading is done
     */
    public static void loadSequence(String url, final PanelData data,
            final AsyncCallback<PanelData> cb) {
        SharedCanvas.load(url, Sequence.class, new AsyncCallback<Sequence>() {
            @Override
            public void onFailure(Throwable err) {
                cb.onFailure(err);
            }

            @Override
            public void onSuccess(Sequence seq) {
                data.setSequence(seq);
                cb.onSuccess(data);
            }
        });
    }

    /**
     * Load a Shared Canvas manifest, sequence, and annotation lists
     * 
     * @param manifest_url
     * @param seq_url
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to call when loading is done
     */
    public static void loadManifestSequenceAndAnnotationLists(
            final String manifest_url, final String seq_url,
            final PanelData data, final AsyncCallback<PanelData> cb) {

        SharedCanvas.load(seq_url, Sequence.class,
                new AsyncCallback<Sequence>() {
                    @Override
                    public void onFailure(Throwable err) {
                        cb.onFailure(err);
                    }

                    @Override
                    public void onSuccess(Sequence seq) {
                        data.setSequence(seq);

                        SharedCanvas.load(manifest_url, Manifest.class,
                                new AsyncCallback<Manifest>() {
                                    @Override
                                    public void onFailure(Throwable err) {
                                        cb.onFailure(err);
                                    }

                                    @Override
                                    public void onSuccess(Manifest man) {
                                        data.setManifest(man);
                                        loadAnnotationLists(
                                                man.annotationsLists(), data,
                                                cb);
                                    }
                                });
                    }
                });
    }

    /**
     * Load a single Shared Canvas annotation list
     * 
     * @param url
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to call when loading is done
     */
    public static void loadAnnotationList(String url, final PanelData data,
            final AsyncCallback<PanelData> cb) {
        SharedCanvas.load(url, AnnotationList.class,
                new AsyncCallback<AnnotationList>() {
                    @Override
                    public void onFailure(Throwable err) {
                        cb.onFailure(err);
                    }

                    @Override
                    public void onSuccess(AnnotationList al) {
                        data.getAnnotationLists().add(al);
                        cb.onSuccess(data);
                    }
                });
    }

    /**
     * Load multiple Shared Canvas annotation lists
     * 
     * @param lists
     * 			a list of references to annotation lists
     * @param data
     * 			container to hold the loaded data
     * @param cb
     * 			asynchronous callback to call when loading is done
     */
    public static void loadAnnotationLists(
            final List<Reference<AnnotationList>> lists, final PanelData data,
            final AsyncCallback<PanelData> cb) {
        load_annotation_lists(lists, 0, data, cb);
    }

    private static void load_annotation_lists(
            final List<Reference<AnnotationList>> lists, final int index,
            final PanelData data, final AsyncCallback<PanelData> cb) {

        SharedCanvas.load(lists.get(index),
                new AsyncCallback<AnnotationList>() {
                    @Override
                    public void onFailure(Throwable err) {
                        cb.onFailure(err);
                    }

                    @Override
                    public void onSuccess(AnnotationList al) {
                        data.getAnnotationLists().add(al);

                        // TODO filter here with certain guarantees? iiif?
                        List<Annotation> images = data.getImageAnnotations();

                        for (Annotation a : al) {
                            if (a.body().isImage()) {
                                images.add(a);
                            }
                        }

                        if (index + 1 < lists.size()) {
                            load_annotation_lists(lists, index + 1, data, cb);
                        } else {
                            cb.onSuccess(data);
                        }
                    }
                });
    }

    /**
     * Load a Shared Canvas manifest, sequence, and canvas with 
     * associated annotation lists
     * 
     * @param manifest_url
     * @param seq_url
     * @param data
     * @param canvas_index
     * @param cb
     */
    public static void loadManifestSequenceCanvasAndAnnotationLists(
    		final String manifest_url, final String seq_url, 
    		final PanelData data, final int canvas_index, 
    		final AsyncCallback<PanelData> cb) {
    	
    	SharedCanvas.load(seq_url, Sequence.class,
    			new AsyncCallback<Sequence>() {
                    @Override
                    public void onFailure(Throwable err) {
                        cb.onFailure(err);
                    }

                    @Override
                    public void onSuccess(Sequence seq) {
                        data.setSequence(seq);
                        data.setCanvas(data.getSequence().canvas(
                        		canvas_index));

                        SharedCanvas.load(manifest_url, Manifest.class,
                                new AsyncCallback<Manifest>() {
                                    @Override
                                    public void onFailure(Throwable err) {
                                        cb.onFailure(err);
                                    }

                                    @Override
                                    public void onSuccess(Manifest man) {
                                        data.setManifest(man);
                                        
                                        loadAnnotationLists(
                                                data.getCanvas()
                                                .hasAnnotations(), data, cb);
                                    }
                                });
                    }
                });
    	
    }
    
    public List<Annotation> getImageAnnotations() {
        return imageAnnotations;
    }

    public PanelData() {
        annotationLists = new ArrayList<AnnotationList>();
        visibleAnnotations = new ArrayList<Annotation>();
        imageAnnotations = new ArrayList<Annotation>();
        annotation_status = new HashSet<String>();
        
        zoom_level = -1;
        position[0] = -111;
        position[1] = -111;
    }

    public ManifestCollection getManifestCollection() {
        return collection;
    }

    public void setManifestCollection(ManifestCollection collection) {
        this.collection = collection;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public void setManifest(Manifest manifest) {
        this.manifest = manifest;

        annotationLists.clear();
        visibleAnnotations.clear();
        imageAnnotations.clear();
        annotation_status.clear();
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public List<AnnotationList> getAnnotationLists() {
        return annotationLists;
    }

    public List<Annotation> getVisibleAnnotations() {
        return visibleAnnotations;
    }
    
    /**
     * Set the boolean status of an annotation: visible or not visible 
     * in the main content area.
     * 
     * @param ann
     * @param status
     */
    public void setAnnotationStatus(Annotation ann, boolean status) {
    	if (status) {
    		annotation_status.add(ann.uri());
    	} else {
    		annotation_status.remove(ann.uri());
    	}
    }
    
    public boolean getAnnotationStatus(Annotation ann) {
    	return annotation_status.contains(ann.uri());
    }
    
    public int getZoomLevel() {
    	return zoom_level;
    }
    
    public void setZoomLevel(int zoom_level) {
    	this.zoom_level = zoom_level;
    }
    
    public int[] getPosition() {
    	return position;
    }
    
    public void setPosition(int[] position) {
    	this.position[0] = position.length > 0 ? position[0] : -111;
    	this.position[1] = position.length > 1 ? position[1] : -111;
    }
}
