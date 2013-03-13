package rose.m3;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.GregorianCalendar;

import rose.m3.RoseCollection.Book;
import rose.m3.RoseCollection.ImageList;
import rose.m3.RoseCollection.ImageTagging;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Model rose content as Shared Canvas,
 * http://www.shared-canvas.org/datamodel/spec/.
 */
public class ResourceMap {
    private static final String ORE_NS_URI = "http://www.openarchives.org/ore/terms/";
    private static final String FOAF_NS_URI = "http://xmlns.com/foaf/0.1/";
    private static final String OA_NS_URI = "http://www.w3.org/ns/oa#";
    private static final String EXIF_NS_URI = "http://www.w3.org/2003/12/exif/ns/";
    private static final String DCMI_TYPES_NS_URI = "http://purl.org/dc/dcmitype/";
    private static final String SHARED_CANVAS_NS_URI = "http://www.shared-canvas.org/ns/";
    private static final String CNT_NS_URI = "http://www.w3.org/2011/content#";

    private static final String READING_DIR_LEFT_TO_RIGHT = "Left-to-Right";
    private static final String READING_DIR_RIGHT_TO_LEFT = "Right-to-Left";

    private final Model model;

    private final Resource resource_map_type;
    private final Resource aggregation_type;
    private final Resource annotation_type;
    private final Resource annotation_list_type;
    private final Resource manifest_type;
    private final Resource sequence_type;
    private final Resource canvas_type;
    private final Resource image_type;
    private final Resource content_as_text_type;

    private final Resource painting_motivation;
    private final Resource describing_motivation;

    private final Property aggregates;
    private final Property describes;
    private final Property described_by;
    private final Property has_target;
    private final Property has_annotations;
    private final Property has_body;
    private final Property motivated_by;
    private final Property for_canvas;
    private final Property character_content;

    private final Property name;
    private final Property image_width;
    private final Property image_height;
    private final Property reading_dir;

    public ResourceMap() {
        this.model = ModelFactory.createDefaultModel();

        this.resource_map_type = model.createResource(ORE_NS_URI
                + "ResourceMap");
        this.aggregation_type = model
                .createResource(ORE_NS_URI + "Aggregation");
        this.annotation_type = model.createResource(OA_NS_URI + "Annotation");
        this.annotation_list_type = model.createResource(OA_NS_URI
                + "Annotation");
        this.sequence_type = model.createResource(SHARED_CANVAS_NS_URI
                + "Sequence");
        this.manifest_type = model.createResource(SHARED_CANVAS_NS_URI
                + "Manifest");
        this.canvas_type = model
                .createResource(SHARED_CANVAS_NS_URI + "Canvas");
        this.image_type = model.createResource(DCMI_TYPES_NS_URI + "Image");
        this.content_as_text_type = model.createResource(CNT_NS_URI
                + "ContentAsText");

        this.painting_motivation = model.createResource(SHARED_CANVAS_NS_URI
                + "painting");
        this.describing_motivation = model.createResource(OA_NS_URI
                + "describing");

        this.aggregates = model.createProperty(ORE_NS_URI + "aggregates");
        this.describes = model.createProperty(ORE_NS_URI + "describes");
        this.described_by = model.createProperty(ORE_NS_URI + "isDescribedBy");
        this.has_target = model.createProperty(OA_NS_URI + "hasTarget");
        this.has_body = model.createProperty(OA_NS_URI + "hasBody");
        this.has_annotations = model.createProperty(OA_NS_URI
                + "hasAnnotations");
        this.motivated_by = model.createProperty(OA_NS_URI + "motivatedBy");
        this.for_canvas = model.createProperty(SHARED_CANVAS_NS_URI
                + "forCanvas");
        this.character_content = model.createProperty(CNT_NS_URI + "chars");

        this.name = model.createProperty(FOAF_NS_URI + "name");
        this.image_width = model.createProperty(EXIF_NS_URI + "width");
        this.image_height = model.createProperty(EXIF_NS_URI + "height");
        this.reading_dir = model.createProperty(SHARED_CANVAS_NS_URI
                + "readingDirection");

        model.setNsPrefix("foaf", FOAF_NS_URI);
        model.setNsPrefix("ore", ORE_NS_URI);
        model.setNsPrefix("oa", OA_NS_URI);
        model.setNsPrefix("dcterms", DCTerms.NS);
        model.setNsPrefix("exif", EXIF_NS_URI);
        model.setNsPrefix("dcmitypes", DCMI_TYPES_NS_URI);
    }

    private String get_aggregation_uri(String resource_map_uri) {
        return resource_map_uri + "#aggregation";
    }

    private void add_resource_map_and_aggregation(String service_url,
            Resource agg) {
        Resource resmap = model.createResource(service_url);

        resmap.addProperty(RDF.type, resource_map_type);

        Resource agent = model.createResource();
        agent.addProperty(RDF.type, DCTerms.AgentClass);
        agent.addProperty(name, "Roman de la Rose Digital Library");

        model.add(resmap, DCTerms.creator, agent);

        // TODO What should modified value be?
        resmap.addLiteral(DCTerms.modified,
                model.createTypedLiteral(GregorianCalendar.getInstance()));

        agg.addProperty(RDF.type, aggregation_type);

        model.add(resmap, describes, agg);
        model.add(agg, described_by, resmap);
    }

    /**
     * Create a resource map and aggregation. Return the aggregation.
     * 
     * @param service_url
     * @return aggregation.
     */
    private Resource add_resource_map_and_aggregation(String service_url) {
        Resource agg = model.createResource(get_aggregation_uri(service_url));
        add_resource_map_and_aggregation(service_url, agg);
        return agg;
    }

    /**
     * Create a resource map and list aggregation. Return the aggregation.
     * 
     * @param service_url
     * @return aggregation.
     */
    private RDFList add_resource_map_and_list_aggregation(String service_url) {
        RDFList agg = model.createList();
        add_resource_map_and_aggregation(service_url, agg);
        return agg;
    }

    public Model modelCollection(String service_url, RoseCollection col) {
        Resource agg = add_resource_map_and_aggregation(service_url);

        for (int i = 0; i < col.size(); i++) {
            RoseCollection.Book b = col.getBook(i);
            Resource book_resource = model.createResource(service_url + "/"
                    + b.id());

            book_resource.addProperty(RDFS.label, b.fullName());
            model.add(agg, aggregates, book_resource);
        }

        return model;
    }

    private String get_canvas_uri(Book book, String image_id) {
        return book.dataUrl() + RoseCollection.shortImageName(image_id)
                + "#canvas";
    }

    private String get_illustration_annotation_uri(Book book, String image_id,
            int which) {
        return book.dataUrl() + RoseCollection.shortImageName(image_id)
                + "#annotation;illustration;" + which;
    }

    private String get_transcription_annotation_uri(Book book, String image_id) {
        return book.dataUrl() + RoseCollection.shortImageName(image_id)
                + "#annotation;transcription";
    }

    private String get_image_annotation_uri(Book book, String image_id) {
        return book.dataUrl() + RoseCollection.shortImageName(image_id)
                + "#image";
    }

    public Model modelReadingSequence(String service_url, Book book)
            throws IOException {
        RDFList sequence = add_resource_map_and_list_aggregation(service_url);

        sequence.addProperty(RDF.type, sequence_type);
        sequence.addProperty(reading_dir, READING_DIR_LEFT_TO_RIGHT);

        ImageList images = book.retrieveImageList();

        for (int i = 0; i < images.size(); i++) {
            String image_id = images.image(i);
            String canvas_uri = get_canvas_uri(book, image_id);

            Resource canvas = model.createResource(canvas_uri);
            canvas.addProperty(RDF.type, canvas_type);

            canvas.addLiteral(RDFS.label,
                    RoseCollection.shortImageName(image_id));

            if (!images.missing(i)) {
                canvas.addLiteral(image_width, images.width(i));
                canvas.addLiteral(image_height, images.height(i));
            }

            add_to_aggregation_list(sequence, canvas);
        }

        return model;
    }

    public Model modelManifest(String service_url, Book book)
            throws IOException {
        Resource manifest = add_resource_map_and_aggregation(service_url);

        manifest.addProperty(RDF.type, manifest_type);

        manifest.addProperty(RDFS.label, book.fullName());
        manifest.addProperty(DC.title, book.commonName());
        manifest.addProperty(DC.rights, book.permissionStatement());
        manifest.addProperty(DC.source, book.repository());
        manifest.addProperty(DC.date, book.date());

        if (book.hasIllustrationTagging()) {
            Resource r = model.createResource(service_url + "/"
                    + "annotations/illustration");
            r.addProperty(RDF.type, annotation_list_type);

            model.add(manifest, aggregates, r);
        }

        if (book.hasTranscription()) {
            Resource r = model.createResource(service_url + "/"
                    + "annotations/transcription");
            r.addProperty(RDF.type, annotation_list_type);

            model.add(manifest, aggregates, r);
        }

        {
            Resource r = model.createResource(service_url + "/" + "sequence");
            r.addProperty(RDF.type, sequence_type);
            model.add(manifest, aggregates, r);
        }

        {
            Resource r = model.createResource(service_url + "/"
                    + "annotations/images");
            r.addProperty(RDF.type, annotation_list_type);
            model.add(manifest, aggregates, r);
        }

        return model;

    }

    public Model modelTranscriptionAnnotations(String service_url, Book book)
            throws IOException {
        RDFList annotations = add_resource_map_and_list_aggregation(service_url);
        annotations.addProperty(RDF.type, annotation_list_type);

        add_transcription_annotations(book, annotations);

        return model;
    }

    private void add_to_aggregation_list(RDFList agg, Resource res) {
        if (agg.isEmpty()) {
            agg = agg.cons(res);
        } else {
            agg.add(res);
        }

        model.add(agg, aggregates, res);

        if (!agg.isValid()) {
            throw new RuntimeException("Invalid rdf list");
        }
    }

    private void add_text_annotation(RDFList agg, String annotation_uri,
            String target_uri, String text, String format) {
        Resource text_annotation = model.createResource(annotation_uri);

        text_annotation.addProperty(RDF.type, annotation_type);
        text_annotation.addProperty(motivated_by, describing_motivation);
        text_annotation.addProperty(has_target, target_uri);

        Resource text_annotation_body = model.createResource();
        text_annotation_body.addProperty(RDF.type, DCTypes.Text);
        text_annotation_body.addProperty(RDF.type, content_as_text_type);
        text_annotation_body.addProperty(DC.format, format);
        text_annotation_body.addProperty(character_content, text);

        text_annotation.addProperty(has_body, text_annotation_body);

        add_to_aggregation_list(agg, text_annotation);
    }

    private void add_illustration_annotations(Book book, RDFList annotations)
            throws IOException {
        if (!book.hasIllustrationTagging()) {
            return;
        }

        ImageList images = book.retrieveImageList();
        ImageTagging tagging = book.retrieveIllustrationTagging(images);

        for (int i = 0; i < images.size(); i++) {
            String image_id = images.image(i);
            String canvas_uri = get_canvas_uri(book, image_id);

            for (int illus : tagging.findIllusIndexes(i)) {
                String uri = get_illustration_annotation_uri(book, image_id,
                        illus);
                String text = tagging.descriptions(illus);

                add_text_annotation(annotations, uri, canvas_uri, text,
                        "text/plain");
            }
        }
    }

    private void add_transcription_annotations(Book book, RDFList annotations)
            throws IOException {
        if (!book.hasTranscription()) {
            return;
        }

        ImageList images = book.retrieveImageList();

        for (int i = 0; i < images.size(); i++) {
            String image_id = images.image(i);
            String canvas_uri = get_canvas_uri(book, image_id);

            String uri = get_transcription_annotation_uri(book, image_id);

            ByteArray data = new ByteArray(4 * 1024);
            InputStream is = new URL(images.transcriptionUrl(i)).openStream();
            data.append(is);
            is.close();

            String text = new String(data.array, 0, data.length, "UTF-8");
            add_text_annotation(annotations, uri, canvas_uri, text, "text/xml");
        }
    }

    public Model modelIllustrationDescriptionAnnotations(String service_url,
            Book book) throws IOException {
        RDFList annotations = add_resource_map_and_list_aggregation(service_url);
        annotations.addProperty(RDF.type, annotation_list_type);

        add_illustration_annotations(book, annotations);

        return model;
    }

    private void addImageAnnotations(Book book, RDFList annotations)
            throws IOException {
        ImageList images = book.retrieveImageList();

        for (int i = 0; i < images.size(); i++) {
            String image_id = images.image(i);
            String image_uri = get_image_annotation_uri(book, image_id);
            String iiif_service_url = images.iiifServiceUrl(i);
            String canvas_uri = get_canvas_uri(book, image_id);

            if (!images.missing(i)) {
                Resource image_annotation = model.createResource(image_uri);
                image_annotation.addProperty(RDF.type, annotation_type);
                image_annotation.addProperty(motivated_by, painting_motivation);

                Resource canvas = model.createResource(canvas_uri);
                canvas.addProperty(RDF.type, canvas_type);

                Resource image_annotation_body = model
                        .createResource(iiif_service_url);

                image_annotation.addProperty(has_body, image_annotation_body);
                image_annotation.addProperty(has_target, canvas);

                image_annotation_body.addProperty(DC.format, "image/jpg");
                image_annotation_body.addProperty(DCTerms.conformsTo, "IIIF");
                image_annotation_body.addProperty(RDF.type, image_type);

                add_to_aggregation_list(annotations, image_annotation);
            }
        }
    }

    public Model modelImageAnnotations(String service_url, Book book)
            throws IOException {
        RDFList annotations = add_resource_map_and_list_aggregation(service_url);
        annotations.addProperty(RDF.type, annotation_list_type);

        addImageAnnotations(book, annotations);

        return model;
    }

    public Model modelAllAnnotations(String service_url, Book book)
            throws IOException {
        RDFList annotations = add_resource_map_and_list_aggregation(service_url);
        annotations.addProperty(RDF.type, annotation_list_type);

        addImageAnnotations(book, annotations);
        add_illustration_annotations(book, annotations);
        add_transcription_annotations(book, annotations);

        return model;
    }
}
