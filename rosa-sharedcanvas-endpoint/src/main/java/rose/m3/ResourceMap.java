package rose.m3;

import java.io.IOException;

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
import com.hp.hpl.jena.vocabulary.RDF;

// TODO last modified dcterms

public class ResourceMap {
	private static final String ORE_NS_URI = "http://www.openarchives.org/ore/terms/";
	private static final String FOAF_NS_URI = "http://xmlns.com/foaf/0.1/";
	private static final String OAC_NS_URI = "http://www.openannotation.org/ns/";
	private static final String EXIF_NS_URI = "http://www.w3.org/2003/12/exif/ns/";
	private static final String DCMI_TYPES_NS_URI = "http://purl.org/dc/dcmitype/";
	private static final String DMS_NS_URI = "http://dms.stanford.edu/ns/";

	private final Model model;
	private final Resource resource_map_type;
	private final Resource aggregation_type;
	// private final Resource annotation_type;
	private final Resource annotation_body_type;
	private final Resource annotation_target_type;
	// private final Resource annotation_list_type;
	private final Resource text_annotation_list_type;
	private final Resource image_annotation_list_type;
	private final Resource image_annotation_type;
	private final Resource text_annotation_type;
	private final Resource sequence_type;
	private final Resource canvas_type;
	private final Resource image_type;

	private final Property image_width;
	private final Property image_height;
	private final Property aggregates;
	private final Property describes;
	private final Property described_by;
	private final Property has_target;
	private final Property has_body;
	private final Property name;

	public ResourceMap() {
		this.model = ModelFactory.createDefaultModel();

		this.resource_map_type = model.createResource(ORE_NS_URI
				+ "ResourceMap");

		this.aggregation_type = model
				.createResource(ORE_NS_URI + "Aggregation");

		// this.annotation_type = model.createResource(OAC_NS_URI +
		// "Annotation");

		// this.annotation_list_type = model.createResource(DMS_NS_URI
		// + "AnnotationList");
		this.text_annotation_list_type = model.createResource(DMS_NS_URI
				+ "TextAnnotationList");
		this.image_annotation_list_type = model.createResource(DMS_NS_URI
				+ "ImageAnnotationList");
		this.image_annotation_type = model.createResource(DMS_NS_URI
				+ "ImageAnnotation");
		this.text_annotation_type = model.createResource(DMS_NS_URI
				+ "TextAnnotation");
		this.sequence_type = model.createResource(DMS_NS_URI + "Sequence");
		this.canvas_type = model.createResource(DMS_NS_URI + "Canvas");
		this.image_type = model.createResource(DCMI_TYPES_NS_URI + "Image");

		this.annotation_target_type = model.createResource(OAC_NS_URI
				+ "Target");

		this.annotation_body_type = model.createResource(OAC_NS_URI + "Body");

		this.aggregates = model.createProperty(ORE_NS_URI + "aggregates");

		this.describes = model.createProperty(ORE_NS_URI + "describes");

		this.described_by = model.createProperty(ORE_NS_URI + "isDescribedBy");

		this.has_target = model.createProperty(OAC_NS_URI + "hasTarget");
		this.has_body = model.createProperty(OAC_NS_URI + "hasBody");

		this.name = model.createProperty(FOAF_NS_URI + "name");

		this.image_width = model.createProperty(EXIF_NS_URI + "width");
		this.image_height = model.createProperty(EXIF_NS_URI + "height");

		model.setNsPrefix("foaf", FOAF_NS_URI);
		model.setNsPrefix("ore", ORE_NS_URI);
		model.setNsPrefix("oac", OAC_NS_URI);
		model.setNsPrefix("dcterms", DCTerms.NS);
		model.setNsPrefix("exif", EXIF_NS_URI);
		model.setNsPrefix("dcmitypes", DCMI_TYPES_NS_URI);
		model.setNsPrefix("dms", DMS_NS_URI);
	}

	public Model model(String service_url, RoseCollection col) {
		Resource resmap = model.createResource(service_url);
		Resource agg = model.createResource(col.dataUrl());

		resmap.addProperty(RDF.type, resource_map_type);
        resmap.addProperty(DCTerms.creator,
                model.createResource()
                        .addProperty(name, "Rose digital library"));

		agg.addProperty(RDF.type, aggregation_type);

		model.add(resmap, describes, agg);
		model.add(agg, described_by, resmap);

		for (int i = 0; i < col.size(); i++) {
			RoseCollection.Book b = col.getBook(i);
			Resource book_resource = model.createResource(service_url + "/"
					+ b.id());

			book_resource.addProperty(DC.title, b.commonName());
			model.add(agg, aggregates, book_resource);
		}

		return model;
	}

	public Model modelReadingSequence(String service_url, Book book)
			throws IOException {
		Resource resmap = model.createResource(service_url);
		RDFList sequence = model.createList();

		resmap.addProperty(RDF.type, resource_map_type);
		resmap.addProperty(
				DCTerms.creator,
				model.createResource().addProperty(name,
						"Roman de la Rose Digital Library"));

		ImageList images = book.retrieveImageList();

		for (int i = 0; i < images.size(); i++) {
			String image_id = images.image(i);
			String image_uri = book.dataUrl() + image_id;
			String canvas_uri = image_uri + "#canvas";

			Resource canvas = model.createResource(canvas_uri);
			canvas.addProperty(RDF.type, annotation_target_type);

			if (!images.missing(i)) {

				canvas.addLiteral(image_width, images.width(i));
				canvas.addLiteral(image_height, images.height(i));
			}

			canvas.addProperty(RDF.type, canvas_type);
			model.add(sequence, aggregates, canvas);

			if (sequence.isEmpty()) {
				sequence = sequence.cons(canvas);
			} else {
				sequence.add(canvas);
			}
		}

		sequence.addProperty(RDF.type, aggregation_type);

		if (!sequence.isValid()) {
			throw new RuntimeException("Invalid rdf list");
		}

		model.add(resmap, describes, sequence);
		model.add(sequence, described_by, resmap);

		return model;
	}

	public Model modelManifest(String service_url, Book book)
			throws IOException {
		Resource resmap = model.createResource(service_url);
		Resource manifest = model.createResource(book.dataUrl());

		manifest.addProperty(RDF.type, aggregation_type);

		resmap.addProperty(RDF.type, resource_map_type);
		resmap.addProperty(
				DCTerms.creator,
				model.createResource().addProperty(name,
						"Roman de la Rose Digital Library"));

		manifest.addProperty(DC.title, book.commonName());
		manifest.addProperty(DC.rights, book.permissionStatement());
		manifest.addProperty(DC.source, book.repository());
		manifest.addProperty(DC.date, book.date());

		if (book.hasIllustrationTagging()) {
			Resource r = model.createResource(service_url + "/" + "illus");
			r.addProperty(RDF.type, text_annotation_list_type);

			model.add(manifest, aggregates, r);
		}

		if (book.hasTranscription()) {
			Resource r = model.createResource(service_url + "/" + "trans");
			r.addProperty(RDF.type, text_annotation_list_type);

			model.add(manifest, aggregates, r);
		}

		{
			Resource r = model.createResource(service_url + "/" + "seq");
			r.addProperty(RDF.type, sequence_type);
			model.add(manifest, aggregates, r);

		}

		{
			Resource r = model.createResource(service_url + "/" + "images");
			r.addProperty(RDF.type, image_annotation_list_type);
			model.add(manifest, aggregates, r);
		}

		model.add(resmap, describes, manifest);
		model.add(manifest, described_by, resmap);

		return model;

	}

	public Model modelTranscriptionAnnotations(String service_url, Book book)
			throws IOException {
		Resource resmap = model.createResource(service_url);
		Resource annotations = model.createResource(book.imagesUrl());

		resmap.addProperty(RDF.type, resource_map_type);
		resmap.addProperty(
				DCTerms.creator,
				model.createResource().addProperty(name,
						"Roman de la Rose Digital Library"));

		ImageList images = book.retrieveImageList();

		for (int i = 0; i < images.size(); i++) {
			String image_id = images.image(i);
			String image_uri = book.dataUrl() + image_id;
			String canvas_uri = image_uri + "#canvas";

			if (book.hasTranscription()) {
				Resource text_annotation = model.createResource(image_uri
						+ "#transcription");
				Resource text_annotation_body = model.createResource(images
						.transcriptionUrl(i));

				text_annotation.addProperty(RDF.type, text_annotation_type);
				text_annotation.addProperty(has_body, text_annotation_body);
				text_annotation.addProperty(has_target, canvas_uri);
				text_annotation_body
						.addProperty(RDF.type, annotation_body_type);

				model.add(annotations, aggregates, text_annotation);
			}
		}

		annotations.addProperty(RDF.type, aggregation_type);

		model.add(resmap, describes, annotations);
		model.add(annotations, described_by, resmap);

		return model;

	}

	public Model modelIllustrationDescriptionAnnotations(String service_url,
			Book book) throws IOException {
		Resource resmap = model.createResource(service_url);
		Resource annotations = model.createResource(book.imagesUrl());

		resmap.addProperty(RDF.type, resource_map_type);
		resmap.addProperty(
				DCTerms.creator,
				model.createResource().addProperty(name,
						"Roman de la Rose Digital Library"));

		annotations.addProperty(RDF.type, aggregation_type);

		model.add(resmap, describes, annotations);
		model.add(annotations, described_by, resmap);

		if (!book.hasIllustrationTagging()) {
			return model;
		}

		ImageList images = book.retrieveImageList();
		ImageTagging tagging = book.retrieveIllustrationTagging(images);

		for (int i = 0; i < images.size(); i++) {
			String image_id = images.image(i);
			String image_uri = book.dataUrl() + image_id;
			String canvas_uri = image_uri + "#canvas";

			if (book.hasIllustrationTagging()) {
				String descr = tagging.description(i);

				if (!descr.isEmpty()) {
					Resource text_annotation = model.createResource(image_uri
							+ "#illustag");
					Resource text_annotation_body = model.createResource();
					text_annotation_body.addProperty(DC.description, descr);
					text_annotation.addProperty(RDF.type, text_annotation_type);
					text_annotation.addProperty(has_body, text_annotation_body);
					text_annotation.addProperty(has_target, canvas_uri);
					text_annotation_body.addProperty(RDF.type,
							annotation_body_type);

					model.add(annotations, aggregates, text_annotation);
				}
			}
		}

		return model;

	}

	public Model modelImageAnnotations(String service_url, Book book)
			throws IOException {
		Resource resmap = model.createResource(service_url);
		Resource annotations = model.createResource(book.imagesUrl());

		resmap.addProperty(RDF.type, resource_map_type);
		resmap.addProperty(
				DCTerms.creator,
				model.createResource().addProperty(name,
						"Roman de la Rose Digital Library"));

		ImageList images = book.retrieveImageList();

		for (int i = 0; i < images.size(); i++) {
			String image_id = images.image(i);
			String image_uri = book.dataUrl() + image_id;
			//String fsi_image_uri = images.displayUrl(i);
			String iiif_service_url = images.iiifServiceUrl(i);
			
			String canvas_uri = image_uri + "#canvas";

			if (!images.missing(i)) {
				Resource image_annotation = model.createResource(image_uri);
				image_annotation.addProperty(RDF.type, image_annotation_type);

				Resource canvas = model.createResource(canvas_uri);
				canvas.addProperty(RDF.type, canvas_type);
				
				Resource image_annotation_body = model
						.createResource(iiif_service_url);

				
		        image_annotation_body.addProperty(DCTerms.conformsTo,
		                "IIIF");
		        
				image_annotation_body.addLiteral(image_width, images.width(i));
				image_annotation_body
						.addLiteral(image_height, images.height(i));

				image_annotation.addProperty(has_body, image_annotation_body);
				image_annotation.addProperty(has_target, canvas);
				image_annotation_body.addProperty(RDF.type,
						annotation_body_type);
				image_annotation_body.addProperty(RDF.type, image_type);

				model.add(annotations, aggregates, image_annotation);
			}
		}

		annotations.addProperty(RDF.type, aggregation_type);

		model.add(resmap, describes, annotations);
		model.add(annotations, described_by, resmap);

		return model;
	}

}
