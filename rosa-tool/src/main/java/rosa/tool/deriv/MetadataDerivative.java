package rosa.tool.deriv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

import rosa.core.BookArchive;
import rosa.core.BookStructure;
import rosa.core.CharacterNames;
import rosa.core.IllustrationTitles;
import rosa.core.ImageTagging;
import rosa.core.ManuscriptArchive;
import rosa.core.NarrativeMapping;
import rosa.core.NarrativeSections;
import rosa.core.ReducedTagging;
import rosa.core.SceneMapping;
import rosa.core.TranscriptionConverter;
import rosa.tool.Config;
import rosa.core.util.XMLUtil;
import rosa.core.util.XMLWriter;

/**
 * Metadata that is stored in the archive.
 */
public class MetadataDerivative extends Derivative {
	private IllustrationTitles illustitles = null;
	private CharacterNames charnames = null;

	public boolean check() {
		boolean success = super.check();

		if (!checkNarrativeSections()) {
			success = false;
		}

		if (!checkIllustrationTitles()) {
			success = false;
		}

		if (!checkCharacterNames()) {
			success = false;
		}

		return success;
	}

	public static final String NAME = "metadata";

	public MetadataDerivative(Config site, PrintStream report) throws IOException {
		super(site, report);
	}

	public boolean update(BookArchive archive, boolean force) {
		boolean success = true;

		report.println(archive.id());

		if (archive instanceof ManuscriptArchive) {
			try {
				updateTEITranscription((ManuscriptArchive) archive);
			} catch (IOException e) {
				reportError("Updating TEI transcription", e);
				success = false;
			} catch (SAXException e) {
				reportError("Updating TEI transcription", e);
				success = false;
			}
		}

		try {
			updateNarrativeTaggingGuess(archive);
		} catch (IOException e) {
			reportError("Updating narrative tagging guess", e);
			success = false;
		} catch (SAXException e) {
			reportError("Updating narrative tagging guess", e);
			success = false;
		}

		return success;
	}

	public boolean check(BookArchive archive) {
		boolean success = true;

		if (!checkImageTagging(archive)) {
			success = false;
		}

		if (!checkCroppingData(archive)) {
			success = false;
		}

		if (!checkReducedTagging(archive)) {
			success = false;
		}

		// narrative scenes checked elsewhere
		NarrativeSections narscenes = null;
		try {
			narscenes = col.loadNarrativeScenes(null);
		} catch (IOException e) {
		}

		if (!checkNarrativeMapping(new File(archive.dir(), archive
				.narrativeTaggingByHumanName()), archive, true, narscenes)) {
			success = false;
		}

		if (!checkNarrativeMapping(new File(archive.dir(), archive
				.narrativeTaggingName()), archive, false, narscenes)) {
			success = false;
		}

		if (!checkBookDescription(archive)) {
			success = false;
		}

		return success;
	}

	private boolean checkBookDescription(BookArchive archive) {
		boolean success = true;

		for (String lc : col.languages()) {
			List<String> errors = new ArrayList<String>();

			try {
				archive.description(lc).check(errors);

				if (errors.size() > 0) {
					success = false;

					for (String s : errors) {
						report.println("Book description error (" + lc + "): "
								+ s);
					}
				}
			} catch (IOException e) {
				reportError("Loading book description " + lc, e);
				success = false;
				break;
			} catch (SAXException e) {
				reportError("Loading book description " + lc, e);
				success = false;
				break;
			}
		}

		return success;
	}

	private boolean checkCroppingData(BookArchive archive) {
		try {
			Map<String, double[]> crop = archive.getCroppingData();

			if (crop == null) {
				//report.println("No crop data");
				return false;
			}

			boolean success = true;

			for (String image : archive.filenames()) {
				if (archive.isImage(image)) {
					if (!crop.containsKey(image)) {
						report.println("No crop data for: " + image);
						success = false;
					}
				}
			}

			return success;
		} catch (IOException e) {
			reportError("Loading crop data", e);
		}

		return false;
	}

	private boolean checkNarrativeSections() {
		boolean success = true;

		try {
			List<String> errors = new ArrayList<String>();
			col.loadNarrativeScenes(errors).asScenes();

			if (errors.size() > 0) {
				success = false;

				for (String s : errors) {
					report.println("Narrative scene error: " + s);
				}
			}
		} catch (IOException e) {
			reportError("Loading narrative scenes", e);
			success = false;
		}

		return success;
	}

	private boolean checkCharacterNames() {
		try {
			List<String> errors = new ArrayList<String>();
			col.loadCharacterNames(errors);

			if (errors.size() > 0) {
				for (String s : errors) {
					report.println("Character names csv error: " + s);
				}

				return false;
			}

			return true;
		} catch (IOException e) {
			reportError("Loading character names", e);
			return false;
		}
	}

	private boolean checkIllustrationTitles() {
		try {
			List<String> errors = new ArrayList<String>();
			col.loadIllustrationTitles(errors);

			if (errors.size() > 0) {
				for (String s : errors) {
					report.println("Illustration titles error: " + s);
				}

				return false;
			}

			return true;
		} catch (IOException e) {
			reportError("Loading illustration titles", e);
			return false;
		}
	}

	private boolean checkReducedTagging(BookArchive archive) {
		try {
			List<String> errors = new ArrayList<String>();
			ReducedTagging redtag = archive.reducedTagging(errors);

			if (redtag == null) {
				return true;
			}

			boolean success = true;

			redtag.check(archive, errors);

			if (errors.size() > 0) {
				success = false;
				for (String s : errors) {
					report.println("Reduced tagging error: " + s);
				}
			}

			return success;
		} catch (IOException e) {
			reportError("Loading reduced tagging", e);
		}

		return false;
	}

	private boolean checkNarrativeMapping(File file, BookArchive archive,
			boolean textformat, NarrativeSections narscenes) {
		if (!file.exists()) {
			return true;
		}

		try {
			boolean success = true;

			List<String> errors = new ArrayList<String>();
			NarrativeMapping nartag = new NarrativeMapping(file, textformat,
					errors);

			nartag.check(col.loadNarrativeScenes(null), archive, errors);

			if (errors.size() > 0) {
				success = false;

				for (String s : errors) {
					report.println("Narrative tagging error "
							+ (textformat ? "(text): " : "(csv): ") + s);
				}
			}

			return success;
		} catch (IOException e) {
			reportError("Loading narrative tagging", e);
		}

		return false;
	}

	public boolean validate(BookArchive archive) {
		boolean success = check(archive);

		if (!validateXML(archive)) {
			success = false;
		}

		return success;
	}

	// TODO actually validate the xml against a schema

	private boolean validateXML(BookArchive archive) {
		for (String s : archive.filenames()) {
			if (!s.endsWith(".xml")) {
				continue;
			}

			File file = new File(archive.dir(), s);

			try {
				XMLUtil.createDocument(file);
			} catch (IOException e) {
				report
						.println("Error loading xml " + file.getName() + ": "
								+ e);
			} catch (SAXException e) {
				report
						.println("Error loading xml " + file.getName() + ": "
								+ e);
			}
		}

		return false;
	}

	private void updateNarrativeTaggingGuess(BookArchive archive)
			throws IOException, SAXException {

		// Only run narrative tagging algorithm if human narrative tagging does
		// not exist,
		// and our current guess does not exist
		// If we have human narrative tagging, create a csv version.

		List<String> errors = new ArrayList<String>();

		if (new File(archive.dir(), archive.narrativeTaggingByHumanName())
				.exists()) {
			archive.narrativeTaggingByHuman(errors).writeInCSVFormat(
					new FileOutputStream(new File(archive.dir(), archive
							.narrativeTaggingName())));

			if (errors.size() > 0) {
				for (String s : errors) {
					report.println("Human narrative tagging error: " + s);
				}

				return;
			}

			return;
		}

		// TODO for now always regenerate the csv
		// if (new File(archive.dir(), archive.narrativeTaggingName()).exists())
		// {
		// return;
		// }

		errors.clear();

		ReducedTagging redtag = archive.reducedTagging(errors);

		if (errors.size() > 0) {
			for (String s : errors) {
				report.println("Reduced tagging error: " + s);
			}

			return;
		}

		if (redtag == null) {
			return;
		}

		BookStructure struct = redtag.structure();

		File guessfile = new File(archive.dir(), archive.narrativeTaggingName());
		errors.clear();
		NarrativeSections nar = col.loadNarrativeScenes(errors);
		for (String s : errors) {
			report.println("Narrative scene list error: " + s);
		}

		BookArchive trarchive = col.loadArchive("SeldenSupra57");
		File trans = new File(trarchive.dir(), trarchive.transcriptionName());

		NarrativeMapping guess = SceneMapping.guessNarrativeScenes(nar, struct,
				true, trans, "(SS57)");

		FileOutputStream os = new FileOutputStream(guessfile);
		guess.writeInCSVFormat(os);
		os.close();
	}

	private void updateTEITranscription(ManuscriptArchive archive)
			throws IOException, SAXException {

		TranscriptionConverter trans = new TranscriptionConverter();
		XMLWriter transout = null;

		for (String filename : archive.filenames()) {
			if (!archive.isImage(filename)) {
				continue;
			}

			File texttrans = new File(archive.dir(), archive
					.textTranscriptionName(filename));

			if (texttrans.exists()) {
				String folio = ManuscriptArchive.findFolio(filename);
				trans.setFolioOverride(folio);

				if (transout == null) {
					File teitrans = new File(archive.dir(), archive
							.transcriptionName());
					transout = new XMLWriter(new StreamResult(teitrans));

					trans.startConversion(transout);
					report.println("Updating transcription");
				}

				trans.getWarnings().clear();
				trans.getErrors().clear();
				trans.convert(texttrans, "Latin1", transout);

				if (trans.getErrors().size() > 0) {
					for (String s : trans.getErrors()) {
						report.println("Error while parsing "
								+ texttrans.getName() + ": " + s);
					}
				}

				for (String warn : trans.getWarnings()) {
					report.println("Warning while parsing "
							+ texttrans.getName() + ": " + warn);
				}
			}
		}

		if (transout != null) {
			trans.endConversion(transout);
		}
	}

	private boolean checkImageTagging(BookArchive archive) {
		try {
			List<String> errors = new ArrayList<String>();
			ImageTagging imgtag = archive.imageTagging(errors);

			if (imgtag == null) {
				return true;
			}

			if (illustitles == null) {
				illustitles = col.loadIllustrationTitles(null);
			}

			if (charnames == null) {
				charnames = col.loadCharacterNames(null);
			}

			imgtag.check(archive, illustitles, charnames, errors);

			if (errors.size() == 0) {
				return true;
			} else {
				for (String s : errors) {
					report.println("Image tagging error: " + s);
				}
				return false;
			}

		} catch (IOException e) {
			report.println("Failed to load image tagging: " + e);
			return false;
		}
	}

	public String name() {
		return NAME;
	}
}
