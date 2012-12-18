package rosa.tool.deriv;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.SAXException;

import rosa.core.BookArchive;
import rosa.core.BookCollection;
import rosa.core.BookDescription;
import rosa.core.CharacterNames;
import rosa.core.IllustrationTitles;
import rosa.core.ImageTagging;
import rosa.core.ManuscriptArchive;
import rosa.core.NarrativeSections;
import rosa.tool.Config;
import rosa.core.util.CSV;
import rosa.core.util.FileUtil;

/**
 * Write out resources needed for GWT.
 */
public class WebsiteResourcesDerivative extends Derivative {
    public final static String NAME = "website-resources";

    public WebsiteResourcesDerivative(Config site, PrintStream report)
            throws IOException {
        super(site, report);
    }

    public boolean check(BookArchive archive) {
        return true;
    }

    public String name() {
        return NAME;
    }

    public boolean update(BookArchive archive, boolean force) {
        return true;
    }

    public boolean validate(BookArchive archive) {
        return false;
    }

    public boolean check() {
        boolean success = true;

        return success;
    }

    private String browseDataSpreadsheetName(String lc) {
        if (lc.equals("en")) {
            return "books.csv";
        } else {
            return "books_" + lc + ".csv";
        }

    }

    private String collectionDataSpreadsheetName(String lc) {
        if (lc.equals("en")) {
            return "collection_data.csv";
        } else {
            return "collection_data_" + lc + ".csv";
        }
    }

    public boolean update(boolean force) {
        boolean success = true;

        try {
            for (String lc : col.languages()) {
                writeBooksFile(new File(site.resourcesPath(),
                        browseDataSpreadsheetName(lc)), lc);

                writeCollectionDataFileAsCSV(new File(site.resourcesPath(),
                        collectionDataSpreadsheetName(lc)), lc);
            }

            FileUtil.copy(new File(col.dir(), CharacterNames.NAME),
                    site.resourcesPath());

            writeIllustrationTitles(col.loadIllustrationTitles(null), new File(
                    site.resourcesPath(), IllustrationTitles.NAME));

            FileUtil.copy(new File(col.dir(), NarrativeSections.NAME),
                    site.resourcesPath());

        } catch (IOException e) {
            reportError("Failed updating metadata", e);
            success = false;
        } catch (SAXException e) {
            reportError("Failed update", e);
        }

        return success;
    }

    private void writeIllustrationTitles(IllustrationTitles titles, File out)
            throws IOException, SAXException {
        PrintWriter w = new PrintWriter(out);

        add(w, "Page");
        w.append(',');
        add(w, "Illustration title");
        w.append(',');
        add(w, "Frequency");
        w.println();

        class Row {
            int loc;
            String title;
            int freq;

            public Row(String title) {
                this.freq = 0;
                this.loc = 0;
                this.title = title;
            }
        }

        Map<String, Row> rows = new HashMap<String, Row>();

        for (String bookid : col.books()) {
            BookArchive archive = col.loadArchive(bookid);
            ImageTagging imgtag = archive.imageTagging(null);

            if (imgtag == null) {
                continue;
            }

            List<BookArchive.Image> images = archive.images();

            int firstfolio = 0;

            for (BookArchive.Image image : images) {
                if (!image.fileName().contains("binding")
                        && !image.fileName().contains("frontmatter")) {
                    break;
                }

                firstfolio++;
            }

            for (int i = 0; i < imgtag.numImages(); i++) {
                for (String id : imgtag.titles(i)) {
                    id = id.trim();
                    
                    if (!ImageTagging.isNumericalId(id)) {
//                        if (id.isEmpty() || Character.isDigit(id.charAt(0))) {
//                            report.println("ILLUS TITLE CHECK ME: " + bookid
//                                    + imgtag.folio(i) + " '" + id + "'");
//                        }
                        
                        continue;
                    }

                    String title = titles.title(id);
                    Row row = rows.get(title);

                    if (row == null) {
                        row = new Row(title);
                        rows.put(title, row);
                    }

                    String folio = imgtag.folio(i);
                    String imagename = archive.guessImageName(folio);

                    int position = 0;
                    for (BookArchive.Image image : images) {
                        if (image.fileName().equals(imagename)) {
                            break;
                        }

                        position++;
                    }

                    archive.firstExistingImage();

                    row.loc += position - firstfolio;
                    row.freq++;
                }
            }
        }

        for (Row row : rows.values()) {
            add(w, "" + Math.round(((float) row.loc / row.freq)));
            w.append(',');

            add(w, "" + row.title);
            w.append(',');

            add(w, "" + row.freq);

            w.println();
        }

        w.close();

        CSV.normalize(out);
    }

    private void add(Appendable ap, String val) throws IOException {
        val = val == null ? "" : val.replaceAll("\\s+", " ").trim();

        ap.append(CSV.escape(val));
    }

    private void add(Appendable ap, int val) throws IOException {
        ap.append(CSV.escape(val == -1 ? "" : "" + val));
    }

    // Return 0 (no trans), 1 (partial trans), 2 (complete trans)
    private int hasTranscriptions(ManuscriptArchive archive) {
        File trdir = new File(site.dataPath(), archive.id());

        boolean foundall = true;
        boolean missedall = true;

        for (String filename : archive.filenames()) {
            if (archive.isFolioImage(filename)) {
                String trans = archive.textTranscriptionName(filename).replace(
                        ".txt", ".xml");

                if (new File(trdir, trans).exists()) {
                    missedall = false;
                } else {
                    foundall = false;
                }
            }
        }

        if (foundall) {
            return 2;
        } else if (missedall) {
            return 0;
        } else {
            return 1;
        }
    }

    private void writeBooksFile(File out, String lc) throws IOException,
            SAXException {
        PrintWriter w = new PrintWriter(out);

        for (String bookid : col.books()) {
            BookArchive archive = col.loadArchive(bookid);

            // System.out.println(archive.descriptionName(lc));

            BookDescription desc = archive.description(lc);

            if (desc == null) {
                desc = archive.description(BookCollection.DEFAULT_LC);
            }
            
            if (desc == null) {
                report.println("Skipping ms with missing description: " + bookid);
                continue;
            }

            add(w, bookid);
            w.append(',');
            add(w, desc.repository());
            w.append(',');
            add(w, desc.shelfmark());
            w.append(',');
            add(w, desc.commonName());
            w.append(',');
            add(w, desc.currentLocation());
            w.append(',');
            add(w, desc.date());
            w.append(',');
            add(w, desc.origin());
            w.append(',');
            add(w, desc.type());
            w.append(',');
            add(w,
                    desc.numIllustrations() == -1 ? null : ""
                            + desc.numIllustrations());
            w.append(',');
            add(w, desc.numFolios() == -1 ? null : "" + desc.numFolios());
            w.append(',');

            if (archive instanceof ManuscriptArchive) {
                add(w, "" + hasTranscriptions((ManuscriptArchive) archive));
            } else {
                add(w, "0");
            }
            w.append(',');

            w.append(archive.imageTagging(null) == null ? "0" : "2");
            w.append(',');

            w.append(archive.narrativeTagging(null) == null ? "0" : "2");
            w.append(',');

            w.append(new File(archive.dir(), archive.bibliographyName())
                    .exists() ? "2" : "0");

            // w.append(',');
            // w.append(archive.getCroppingData() == null ? "0" : "2");

            w.println();
        }

        w.close();

        CSV.normalize(out);
    }

    private void writeCollectionDataFileAsCSV(File out, String lc)
            throws IOException, SAXException {
        PrintWriter w = new PrintWriter(out);

        // TODO Grab correct column labels from Labels.properties somehow

        add(w, "Id");
        w.append(',');
        add(w, "Name");
        w.append(',');
        add(w, "Origin");
        w.append(',');
        add(w, "Material");
        w.append(',');
        add(w, "Number of folios");
        w.append(',');
        add(w, "Height mm");
        w.append(',');
        add(w, "Width mm");
        w.append(',');
        add(w, "Leaves per gathering");
        w.append(',');
        add(w, "Lines per column");
        w.append(',');
        add(w, "Number of illustrations");
        w.append(',');
        add(w, "Date start");
        w.append(',');
        add(w, "Date end");
        w.append(',');
        add(w, "Columns per folio");
        w.append(',');
        add(w, "Texts");
        w.append(',');
        add(w, "Folios with 1 illustration");
        w.append(',');
        add(w, "Folios with >1 illustrations");
        w.println();

        for (String bookid : col.books()) {
            BookArchive archive = col.loadArchive(bookid);

            BookDescription desc = archive.description(lc);

            if (desc == null) {
                desc = archive.description(BookCollection.DEFAULT_LC);
            }

            BookDescription.Text rosetext = null;

            for (BookDescription.Text text : desc.texts()) {
                if (text.id().equals(BookDescription.ROSE_TEXT_ID)) {
                    rosetext = text;
                    break;
                }
            }

            add(w, bookid);
            w.append(',');
            add(w, desc.commonName());
            w.append(',');
            add(w, desc.origin());
            w.append(',');
            add(w, desc.material());
            w.append(',');
            add(w, rosetext == null ? -1 : rosetext.numFolios());
            w.append(',');
            add(w, desc.height());
            w.append(',');
            add(w, desc.width());
            w.append(',');
            add(w, rosetext == null ? -1 : rosetext.leavesPerGathering());
            w.append(',');
            add(w, rosetext == null ? -1 : rosetext.linesPerColumn());
            w.append(',');
            add(w, rosetext == null ? -1 : rosetext.numIllustrations());
            w.append(',');
            add(w, desc.yearStart());
            w.append(',');
            add(w, desc.yearEnd());
            w.append(',');
            add(w, rosetext == null ? -1 : rosetext.columnsPerFolio());
            w.append(',');
            add(w, desc.texts().length);
            w.append(',');

            int[] illuscount = countIllustrations(archive);

            add(w, illuscount[0]);
            w.append(',');
            add(w, illuscount[1]);
            w.println();
        }

        w.close();

        CSV.normalize(out);
    }

    private int[] countIllustrations(BookArchive archive) throws IOException {
        ImageTagging imgtag = archive.imageTagging(null);

        if (imgtag == null) {
            return new int[] { -1, -1, -1 };
        }

        // Folio -> count

        Map<String, Integer> foliocount = new HashMap<String, Integer>();

        for (int i = 0; i < imgtag.numImages(); i++) {
            String folio = imgtag.folio(i);

            if (foliocount.containsKey(folio)) {
                foliocount.put(folio, foliocount.get(folio) + 1);
            } else {
                foliocount.put(folio, 1);
            }
        }

        int[] count = new int[2];

        for (String folio : foliocount.keySet()) {
            int n = foliocount.get(folio);

            if (n == 1) {
                count[0]++;
            } else if (n > 1) {
                count[1]++;
            }
        }

        return count;
    }
}
