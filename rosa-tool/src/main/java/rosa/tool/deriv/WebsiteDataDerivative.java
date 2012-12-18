package rosa.tool.deriv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import rosa.core.BookArchive;
import rosa.core.BookCollection;
import rosa.core.ImageTagging;
import rosa.core.ManuscriptArchive;
import rosa.tool.Config;
import rosa.core.util.CSV;
import rosa.core.util.FileUtil;
import rosa.core.util.XMLUtil;

public class WebsiteDataDerivative extends Derivative {
	public static String NAME = "website-data";

	public WebsiteDataDerivative(Config site, PrintStream report)
			throws IOException {
		super(site, report);
	}

	public String name() {
		return NAME;
	}

	public boolean update(boolean force) {
		boolean success = super.update(force);

		return success;
	}

	public boolean update(BookArchive archive, boolean force) {
		boolean success = true;

		try {
			for (String lc : col.languages()) {
				copyFromArchiveToWebsite(archive, archive.descriptionName(lc),
						archive.descriptionName(BookCollection.DEFAULT_LC));

				copyFromArchiveToWebsite(archive, archive.permissionName(lc),
						archive.permissionName(BookCollection.DEFAULT_LC));
			}

			copyFromArchiveToWesite(archive, archive.imagesName());
			copyFromArchiveToWesite(archive, archive.croppedImagesName());

			copyFromArchiveToWesite(archive, archive.bibliographyName());
			copyFromArchiveToWesite(archive, archive.narrativeTaggingName());

			ImageTagging imgtag = archive.imageTagging(null);
			File imgtagfedfile = new File(site.dataPath(), archive.id()
					+ File.separator + archive.imageTaggingName());

			if (imgtag != null) {
				imgtag.replaceIdentifiersWithStrings(col.loadIllustrationTitles(null),
						col.loadCharacterNames(null));
				imgtag.serialize(new FileWriter(imgtagfedfile));
			}
		} catch (IOException e) {
			reportError("Failed copying metadata from archive", e);
			success = false;
		}

		File trans = new File(archive.dir(), archive.transcriptionName());
		// split transcription as needed

		if (archive.exists(archive.transcriptionName())
				&& archive instanceof ManuscriptArchive && trans.exists()) {
			File feddir = new File(site.dataPath(), archive.id());
			File testfrag = new File(feddir, archive.id()
					+ ".transcription.001r.xml");

			if (force
					|| (!testfrag.exists() && trans.lastModified() > testfrag
							.lastModified())) {
				try {
					if (!chopTranscription((ManuscriptArchive) archive)) {
						success = false;
					}
				} catch (IOException e) {
					reportError("Splitting transcription.", e);
					success = false;
				} catch (SAXException e) {
					reportError("Splitting transcription.", e);
					success = false;
				} catch (TransformerException e) {
					reportError("Splitting transcription.", e);
					success = false;
				}
			}
		}

		return success;
	}

	private void copyFromArchiveToWesite(BookArchive archive, String name)
			throws IOException {
		copyFromArchiveToWebsite(archive, name, null);
	}

	private void copyFromArchiveToWebsite(BookArchive archive, String name,
			String fallbacksrcname) throws IOException {
		File src = new File(archive.dir(), name);

		File datadir = new File(site.dataPath(), archive.id());
		datadir.mkdir();

		File dest = new File(datadir, name);

		if (src.exists()) {
			FileUtil.copy(src, dest);
		} else if (fallbacksrcname != null) {
			src = new File(archive.dir(), fallbacksrcname);
			FileUtil.copy(src, dest);
		}

		if (dest.length() > 0 && dest.getName().endsWith(".csv")) {
			CSV.normalize(dest);
		}
	}

	// Chop into files based on pb, pb may either be a child of the
	// root or a child of lg. Assumes that folio correctly match image names.
	// Also makes sure there is a transcription of each folio

	private boolean chopTranscription(ManuscriptArchive archive)
			throws IOException, SAXException, TransformerException {
		File dir = new File(site.dataPath(), archive.id());

		Set<String> folioimages = new HashSet<String>();
		for (BookArchive.Image image : archive.images()) {
			if (archive.isFolioImage(image.fileName())) {
				folioimages.add(image.fileName());
			}
		}

		Document doc = XMLUtil.createDocument(new File(archive.dir(), archive
				.transcriptionName()));

		Document frag = XMLUtil.createDocument("<div type='ms'></div>");
		Node fragdest = frag.getDocumentElement();
		String fragfolio = null;

		// Check top elements and child of top elements for pb

		for (Node n = doc.getDocumentElement().getFirstChild(); n != null; n = n
				.getNextSibling()) {
			if (n.getNodeName().equals("pb")) {
				String attr = n.getAttributes().getNamedItem("n")
						.getNodeValue();
				String folio = ManuscriptArchive.findFolio(attr);

				// System.err.println("Checking " + folio);

				if (folio == null) {
					report.println("Error in transcription for " + archive.id()
							+ ": Could not parse folio " + attr);
					return false;
				}

				if (fragfolio != null && !folio.equals(fragfolio)) {
					if (!writeTranscriptionFrag(archive, folioimages, dir,
							frag, fragfolio)) {
						return false;
					}

					XMLUtil.removeChildren(frag.getDocumentElement());
					fragdest = frag.getDocumentElement();
				}

				fragdest.appendChild(frag.importNode(n, false));
				fragfolio = folio;
			} else if (n.hasChildNodes()) {
				Node fragdestk = fragdest
						.appendChild(frag.importNode(n, false));

				for (Node k = n.getFirstChild(); k != null; k = k
						.getNextSibling()) {
					if (k.getNodeName().equals("pb")) {
						String attr = k.getAttributes().getNamedItem("n")
								.getNodeValue();
						String folio = ManuscriptArchive.findFolio(attr);

						// System.err.println("Checking " + folio);

						if (folio == null) {
							report.println("Error in transcription for "
									+ archive.id() + ": Could not parse folio "
									+ attr);
							return false;
						}

						if (fragfolio != null && !folio.equals(fragfolio)) {
							if (!writeTranscriptionFrag(archive, folioimages,
									dir, frag, fragfolio)) {
								return false;
							}

							XMLUtil.removeChildren(frag.getDocumentElement());
							fragdest = frag.getDocumentElement();
							fragdestk = fragdest.appendChild(frag.importNode(n,
									false));
						}

						fragfolio = folio;
					}

					fragdestk.appendChild(frag.importNode(k, true));
				}
			} else {
				fragdest.appendChild(frag.importNode(n, false));
			}
		}

		// Write trailing fragment

		if (!writeTranscriptionFrag(archive, folioimages, dir, frag, fragfolio)) {
			return false;
		}

		if (folioimages.size() > 0) {
			String[] imgs = new ArrayList<String>(folioimages)
					.toArray(new String[] {});
			Arrays.sort(imgs);

			report.println("Failed to find transcriptions for all images, remaining: "
					+ Arrays.toString(imgs));
			return false;
		}

		return true;
	}

	private boolean writeTranscriptionFrag(ManuscriptArchive archive,
			Set<String> folioimages, File dir, Document frag, String fragfolio)
			throws IOException, TransformerException {
		String imagename = archive.id() + "." + fragfolio
				+ BookArchive.IMAGE_EXT;

		if (!folioimages.contains(imagename)) {
			report.println("Error in transcription for " + archive.id()
					+ ": Could not find image name or duplicate " + imagename);
			return false;
		}

		folioimages.remove(imagename);

		String fragname = archive.textTranscriptionName(imagename).replace(
				".txt", ".xml");
		FileWriter w = new FileWriter(new File(dir, fragname));
		w.write(XMLUtil.toString(frag));
		w.close();

		return true;
	}

	public boolean check(BookArchive archive) {
		boolean success = true;

		File dir = new File(site.dataPath(), archive.id());

		for (String lc : col.languages()) {
			if (!new File(dir, archive.descriptionName(lc)).exists()) {
				report.println("Missing " + archive.descriptionName(lc));
				success = false;
			}

			if (!new File(dir, archive.permissionName(lc)).exists()) {
				report.println("Missing " + archive.permissionName(lc));
				success = false;
			}
		}

		if (!new File(dir, archive.imagesName()).exists()) {
			report.println("Missing " + archive.imagesName());
			success = false;
		}

		if (archive instanceof ManuscriptArchive) {
			ManuscriptArchive msarchive = (ManuscriptArchive) archive;

			for (String filename : archive.filenames()) {
				if (msarchive.isTextTranscription(filename)) {
					String n = filename.replace(".txt", ".xml");

					if (!new File(dir, n).exists()) {
						report.println("Missing " + n);
						success = false;
					}
				}
			}
		}

		return success;
	}

	public boolean validate(BookArchive archive) {
		boolean success = check(archive);

		File dir = new File(site.dataPath(), archive.id());

		for (File file : dir.listFiles()) {
			if (!file.getName().endsWith(".xml")) {
				continue;
			}

			try {
				XMLUtil.createDocument(file);
			} catch (IOException e) {
				report.println("Error loading metadata " + file.getName()
						+ ": " + e);
			} catch (SAXException e) {
				report.println("Error loading metadata " + file.getName()
						+ ": " + e);
			}
		}

		return success;
	}
}
