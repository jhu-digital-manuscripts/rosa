package rosa.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rosa.core.BookArchive;
import rosa.core.BookArchive.Image;
import rosa.core.BookCollection;
import rosa.core.BookDescription;
import rosa.core.BookStructure;
import rosa.core.CharacterNames;
import rosa.core.IllustrationTitles;
import rosa.core.ManuscriptArchive;
import rosa.core.NarrativeMapping;
import rosa.core.ReducedTagging;
import rosa.core.SceneMapping;
import rosa.core.util.CSV;
import rosa.core.util.CSVSpreadSheet;
import rosa.core.util.FileUtil;
import rosa.core.util.XMLUtil;
import rosa.tool.deriv.BaseDerivative;
import rosa.tool.deriv.CropDerivative;
import rosa.tool.deriv.Derivative;
import rosa.tool.deriv.MetadataDerivative;
import rosa.tool.deriv.WebsiteDataDerivative;
import rosa.tool.deriv.WebsiteFSIDerivative;
import rosa.tool.deriv.WebsiteResourcesDerivative;
import rosa.tool.deriv.WebsiteSearchIndexDerivative;

public class Tool {
    private final Config site;

    private static String[] DERIVATIVE_NAMES = new String[] {
            BaseDerivative.NAME, WebsiteDataDerivative.NAME,
            WebsiteSearchIndexDerivative.NAME,
            CropDerivative.NAME, WebsiteFSIDerivative.NAME,
            WebsiteResourcesDerivative.NAME };

    static {
        Arrays.sort(DERIVATIVE_NAMES);
    }

    private Tool(String propfilename) throws IOException {
        this.site = new Config(propfilename);
    }

    private Tool() throws IOException {
        this.site = new Config();
    }

    private Derivative getDerivative(BookCollection col, PrintStream report,
            String name) throws IOException {
        if (name.equals(WebsiteFSIDerivative.NAME)) {
            return new WebsiteFSIDerivative(site, report);
        } else if (name.equals(CropDerivative.NAME)) {
            return new CropDerivative(site, report);
        } else if (name.equals(BaseDerivative.NAME)) {
            return new BaseDerivative(site, report);
        } else if (name.equals(WebsiteDataDerivative.NAME)) {
            return new WebsiteDataDerivative(site, report);
        } else if (name.equals(MetadataDerivative.NAME)) {
            return new MetadataDerivative(site, report);
        } else if (name.equals(WebsiteSearchIndexDerivative.NAME)) {
            return new WebsiteSearchIndexDerivative(site, report);
        } else if (name.equals(WebsiteResourcesDerivative.NAME)) {
            return new WebsiteResourcesDerivative(site, report);
        } else {
            return null;
        }
    }

    private enum DerivativeOp {
        UPDATE, VALIDATE, CHECK, FORCE_UPDATE;
    }

    /**
     * Parse bnf xml and return mapping of bnf file names to new file names
     * 
     * @throws SAXException
     * @throws IOException
     */
    private Map<String, String> loadBNFFoliation(File xmlfile, String msid)
            throws IOException, SAXException {
        Map<String, String> result = new HashMap<String, String>();

        Document doc = XMLUtil.createDocument(xmlfile);

        int ordernum = 1;
        NodeList nodes = doc.getElementsByTagName("vueObjet");
        int nextflyleaf = 1;
        boolean donefrontmatter = false;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element obj = (Element) nodes.item(i);
            Element img = (Element) obj.getElementsByTagName("image").item(0);

            if (!obj.getAttribute("ordre").equals("" + ordernum)) {
                throw new SAXException("vueObjet not in order");
            }

            ordernum++;

            String s = obj.getAttribute("numeroPage").toLowerCase();
            String folio = ManuscriptArchive.findFolio(s);
            String oldname = "T/" + img.getAttribute("nomImage") + ".tif";

            if (folio == null) {
                if (s.equals("plat supérieur") || s.equals("plat sup.")
                        || s.equals("plat sup. reliure")) {
                    s = "binding.frontcover";
                } else if (s.equals("plat inférieur") || s.equals("plat inf.")
                        || s.equals("plat inf. reliure")
                        || s.equals("plat inférieur.")) {
                    s = "binding.backcover";
                } else if (s.equals("contreplat sup.")
                        || s.equals("contre plat sup.")
                        || s.equals("contreplat sup")) {
                    s = "frontmatter.pastedown";
                } else if (s.equals("contreplat inf.")
                        || s.equals("contreplat inférieur")
                        || s.equals("contreplat inf,")
                        || s.equals("contre plat inf.")
                        || s.equals("contre plat inf")) {
                    s = "endmatter.pastedown";
                } else if (s.equals("tranche supérieure")) {
                    s = "binding.head";
                } else if (s.equals("gouttière")) {
                    s = "binding.foredge";
                } else if (s.equals("tranche inférieur")
                        || s.equals("tranche inférieure")) {
                    s = "binding.tail";
                } else if (s.equals("dos")) {
                    s = "binding.spine";
                } else if (s.equals("page de garde recto")
                        || s.equals("garde r") || s.equals("page de garde r")) {
                    if (donefrontmatter) {
                        s = "endmatter.flyleaf."
                                + String.format("%02d", nextflyleaf) + "r";
                    } else {
                        s = "frontmatter.flyleaf."
                                + String.format("%02d", nextflyleaf) + "r";
                    }
                } else if (s.equals("page de garde verso")
                        || s.equals("garde v") || s.equals("page de garde v")) {
                    if (donefrontmatter) {
                        s = "endmatter.flyleaf."
                                + String.format("%02d", nextflyleaf) + "v";
                    } else {
                        s = "frontmatter.flyleaf."
                                + String.format("%02d", nextflyleaf) + "v";
                    }

                    nextflyleaf++;
                } else {
                    System.err.println("WARNING! Do not know: " + s + " ["
                            + oldname + "]");
                }
            } else {
                donefrontmatter = true;
                nextflyleaf = 1;
                s = folio;
            }

            String newname = msid + "." + s + ".tif";

            result.put(oldname, newname);
        }

        return result;
    }

    private void createDescriptionsFromSpreadSheet(File templatefile,
            File spreadsheetfile, File outdir) throws IOException {
        List<String> errors = new ArrayList<String>();

        CSVSpreadSheet data = new CSVSpreadSheet(spreadsheetfile, 18, 18,
                errors);

        for (String s : errors) {
            System.err.println("Error loading " + spreadsheetfile + ": " + s);
        }

        String template_base = FileUtil.readFull(new FileReader(templatefile))
                .toString();

        Map<String, String> datamap = new HashMap<String, String>();

        int text_number = 1;
        for (int row = 1; row < data.size(); row++) {
            String title = "Christine de Pizan";

            if (data.get(row, 0).trim().isEmpty()) {
                // Row is another text in current manuscript
                text_number++;
            } else {
                // Row is start of new manuscript

                if (!datamap.isEmpty()) {
                    writeDescriptions(template_base, text_number, datamap,
                            outdir);
                    datamap.clear();
                }

                text_number = 1;

                String repos = data.get(row, 0);
                String shelfmark = data.get(row, 1);
                String common_name = data.get(row, 2);
                String loc = data.get(row, 3);
                String origin = data.get(row, 4);
                String type = data.get(row, 5);
                String date = data.get(row, 6);
                String folios = data.get(row, 7);
                String illus = data.get(row, 8);
                String width = data.get(row, 9);
                String height = data.get(row, 10);
                String material = data.get(row, 11);

                // Data cleanup

                date = date.replace("ca.", "");
                date = date.replaceAll("\\s", "");
                String[] dateparts = date.split("-");

                if (illus.isEmpty()) {
                    illus = "0";
                }

                // awful hack
                String century = dateparts[1].substring(0, 2) + "th century";

                // more hacks
                shelfmark = common_name;
                shelfmark = shelfmark.replace("MS ", "");
                shelfmark = shelfmark.replace("Français", "fr.");

                repos = "Bibliothèque nationale de France";

                datamap.put("MAIN_TITLE", title);
                datamap.put("REPOSITORY", repos);
                datamap.put("TYPE", type);
                datamap.put("COMMON_NAME", common_name);
                datamap.put("NUMBER_FOLIOS", folios);
                datamap.put("HEIGHT", height);
                datamap.put("WIDTH", width);
                datamap.put("LOCATION", loc);
                datamap.put("ORIGIN", origin);
                datamap.put("MAIN_ILLUSTRATIONS", illus);
                datamap.put("MATERIAL", material);

                datamap.put("NOT_AFTER", dateparts[0]);
                datamap.put("NOT_BEFORE", dateparts[1]);
                datamap.put("SHELFMARK", shelfmark);
                datamap.put("CENTURY", century);
            }

            String text_title = data.get(row, 12).trim();
            String text_folio_range = data.get(row, 13).trim();
            String text_folios = data.get(row, 14).trim();
            String text_illustrations = data.get(row, 15).trim();
            String text_columns = data.get(row, 16).trim();
            String text_lines_per_column = data.get(row, 17).trim();

            if (text_illustrations.isEmpty()) {
                text_illustrations = "0";
            }

            if (text_folio_range.isEmpty()) {
                System.out.println("Skipping text without folio range in "
                        + datamap.get("COMMON_NAME"));
                continue;
            }

            String[] text_folio_range_parts = text_folio_range.split("-");

            datamap.put("TEXT_TITLE_" + text_number, text_title);
            datamap.put("TEXT_START_FOLIO_" + text_number,
                    text_folio_range_parts[0]);
            datamap.put("TEXT_END_FOLIO_" + text_number,
                    text_folio_range_parts[1]);
            datamap.put("TEXT_ID_" + text_number, text_title);
            datamap.put("TEXT_ILLUSTRATIONS_"  + text_number, text_illustrations);
            datamap.put("TEXT_FOLIOS_"  + text_number, text_folios);
            datamap.put("TEXT_COLUMNS_PER_FOLIO_"  + text_number, text_columns);
            datamap.put("TEXT_LINES_PER_COLUMN_"  + text_number,
                    text_lines_per_column);

        }

        if (!datamap.isEmpty()) {
            writeDescriptions(template_base, text_number, datamap, outdir);
            datamap.clear();
        }
    }

    private void writeDescriptions(String template_base, int text_number,
            Map<String, String> datamap, File outdir) throws IOException {
        // Pull out a sub template :)
        int text_template_start = template_base.indexOf("<msItem");
        int text_template_end = template_base.indexOf("</msItem>") + 9;

        // System.out.println(text_template_start + " " + text_template_end);

        String text_template = template_base.substring(text_template_start,
                text_template_end);

        // System.out.println(text_template);

        String template = template_base.substring(0, text_template_start);

        for (int i = 1; i <= text_number; i++) {
            template += text_template.replace("TEXT_NUMBER", "" + i);
            // System.out.println(text_template.replace("TEXT_NUMBER", "" + i));
        }

        template += template_base.substring(text_template_end);

        // System.out.println(template);

        String msid = datamap.get("COMMON_NAME");

        msid = msid.replaceAll("\\s", "");
        msid = msid.replace("ç", "c");

        File english_descr_file = new File(outdir, msid + ".description_en.xml");
        writeDescription(template, datamap, english_descr_file);

        // French

        String century = datamap.get("CENTURY");
        String material = datamap.get("MATERIAL");

        century = century.replace("th", "e").replace("century", "siècle");
        material = material.replace("parchment", "parchemin");

        String repos = "Bibliothèque nationale de France";

        datamap.put("CENTURY", century);
        datamap.put("REPOSITORY", repos);
        datamap.put("MATERIAL", material);

        File french_descr_file = new File(outdir, msid + ".description_fr.xml");
        writeDescription(template, datamap, french_descr_file);
    }

    private void writeDescription(String template, Map<String, String> map,
            File file) throws IOException {
        String descr = template;

        for (String key : map.keySet()) {
            descr = descr.replace(key, map.get(key));
        }

        PrintStream ps = new PrintStream(file);
        ps.print(descr);
        ps.close();
    }

    // Split checksums and write to bnf.MD5SUM files in dirs which should be
    // renamed to MSID.bnf.MD5SUM
    private void splitBNFChecksums(File checksums, File bnfdir)
            throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(checksums));

        PrintWriter out = null;
        String lastdir = null;

        for (String line = input.readLine(); line != null; line = input
                .readLine()) {
            line = line.trim();

            if (line.startsWith(";")) {
                continue;
            }

            String[] parts = line.split("\\s+|\\|");
            String cs = parts[0];

            int i = parts[1].indexOf('\\');
            String dir = parts[1].substring(1, i);
            String file = parts[1].substring(i + 1);

            if (out == null || !dir.equals(lastdir)) {
                if (out != null) {
                    out.close();
                }

                File msdir = new File(bnfdir, dir);

                msdir.mkdir();

                out = new PrintWriter(new File(msdir, "bnf.MD5SUM"));
            }

            lastdir = dir;
            out.println(cs + " *" + file);
        }

        if (out != null) {
            out.close();
        }

        input.close();
    }

    private void checkBNFManuscript(File msdir) throws IOException,
            SAXException {
        // Find checksum file to get msid
        File checksumfile = null;
        File xmlfile = null;
        String msid = null;

        for (String name : msdir.list()) {
            if (name.endsWith(".bnf.MD5SUM")) {
                checksumfile = new File(msdir, name);
                msid = name.substring(0, name.indexOf('.'));
            } else if (name.toLowerCase().endsWith(".xml")) {
                xmlfile = new File(msdir, name);
            }
        }

        if (checksumfile == null) {
            System.err.println("Could not find checksum file MSID.bnf.MD5SUM");
            return;
        }

        if (checksumfile.length() == 0) {
            System.out.println("No checksums");
        } else {
            // System.out.println("Checking md5sums");

            // Hack to set cwd and run md5sum
            // FileUtil.exec(new String[] {
            // "sh",
            // "-c",
            // "cd " + msdir.getPath() + " && " + "md5sum -c "
            // + checksumfile.getPath() }, System.out);
        }

        if (xmlfile == null) {
            System.err.println("Could not find xml file");
            return;
        }

        Map<String, String> namemap = loadBNFFoliation(xmlfile, msid);

        ArrayList<String> names = new ArrayList<String>(namemap.keySet());
        Collections.sort(names);

        for (String oldname : names) {
            File oldfile = new File(msdir, oldname);
            File newfile = new File(msdir, namemap.get(oldname));

            if (!oldfile.exists()) {
                System.err.println("No such file: " + oldfile);
            }

            if (newfile.exists()) {
                System.err.println("File already exists: " + newfile);
            }

            System.out.println(oldfile.getName() + " -> " + newfile.getName());
        }

    }

    private void moveBNFManuscript(File coldir, File msdir) throws IOException {
        File checksumfile = null;
        String msid = null;

        for (String name : msdir.list()) {
            if (name.endsWith(".bnf.MD5SUM")) {
                checksumfile = new File(msdir, name);
                msid = name.substring(0, name.indexOf('.'));
            }
        }

        if (checksumfile == null) {
            System.err.println("Could not find checksum file MSID.bnf.MD5SUM");
            return;
        }

        File outputdir = new File(coldir, msid);

        renameFiles(new File(outputdir, msid + ".bnf.filemap.csv"), msdir,
                outputdir);

        System.out.println("Guessing order.");
        BaseDerivative.guessAndWriteImageSequence(site.loadBookCollection()
                .loadArchive(msid), System.err);
    }

    private void prepareBNFManuscript(File out_dir, File bnf_dir)
            throws IOException, SAXException {
        File checksumfile = null;
        File xmlfile = null;
        String msid = null;

        for (String name : bnf_dir.list()) {
            if (name.endsWith(".bnf.MD5SUM")) {
                checksumfile = new File(bnf_dir, name);
                msid = name.substring(0, name.indexOf('.'));
            } else if (name.toLowerCase().endsWith(".xml")) {
                xmlfile = new File(bnf_dir, name);
            }
        }

        if (checksumfile == null) {
            System.err.println("Could not find checksum file MSID.bnf.MD5SUM");
            return;
        }

        if (xmlfile == null) {
            System.err.println("Could not find xml file");
            return;
        }

        Map<String, String> namemap = loadBNFFoliation(xmlfile, msid);

        namemap.put(xmlfile.getName(), msid + ".bnf.foliation.xml");
        namemap.put(checksumfile.getName(), checksumfile.getName());
        namemap.put(xmlfile.getName().replace(".XML", ".TIF"), msid
                + ".misc.colorbar.tif");

        File outputdir = new File(out_dir, msid);
        outputdir.mkdir();

        System.out.println(bnf_dir + " -> " + outputdir);

        FileWriter namewriter = new FileWriter(new File(outputdir, msid
                + ".bnf.filemap.csv"), false);

        ArrayList<String> names = new ArrayList<String>(namemap.keySet());
        Collections.sort(names);

        for (String oldname : names) {
            File newfile = new File(outputdir, namemap.get(oldname));

            if (newfile.exists()) {
                System.err.println("Skipping existing file: " + newfile);
                continue;
            }

            // System.out.println(oldfile + " -> " + newfile);
            namewriter.write(CSV.escape(oldname) + ","
                    + CSV.escape(newfile.getName()) + "\n");
        }

        namewriter.close();

        // hack to get bnf permission files
        FileUtil.copy(
                new File(
                        "/mnt/rosecollection/Arsenal3337/Arsenal3337.permission_fr.html"),
                new File(outputdir, msid + ".permission_fr.html"));
        FileUtil.copy(
                new File(
                        "/mnt/rosecollection/Arsenal3337/Arsenal3337.permission_en.html"),
                new File(outputdir, msid + ".permission_en.html"));
    }

    private void printMSFileNames(String msname, int folios) {
        System.out.println(msname + ".binding.frontcover.tif");
        System.out.println(msname + ".binding.backcover.tif");

        System.out.println(msname + ".frontmatter.pastedown.tif");
        System.out.println(msname + ".frontmatter.flyleaf.01r.tif");
        System.out.println(msname + ".frontmatter.flyleaf.01v.tif");

        System.out.println(msname + ".endmatter.pastedown.tif");
        System.out.println(msname + ".endmatter.flyleaf.01r.tif");
        System.out.println(msname + ".endmatter.flyleaf.01v.tif");

        for (int i = 1; i < folios; i++) {
            String s = ManuscriptArchive.toFolio(i, 'r');
            System.out.println(msname + "." + s + ".tif");

            s = ManuscriptArchive.toFolio(i, 'v');
            System.out.println(msname + "." + s + ".tif");
        }
    }

    // private void printImagesWithDimentions(String msid) throws IOException {
    // BookCollection col = site.loadBookCollection();
    // BookArchive archive = col.loadArchive(msid);
    //
    // List<String> images = archive.getOrderedImages();
    //
    // for (String image : images) {
    // if (image.startsWith("*")) {
    // System.out.println(image);
    // } else {
    // String path = new File(archive.dir(), image).getPath();
    //
    // int[] size = getImageDimensionsHack(path);
    //
    // System.out.println(image + "," + size[0] + "," + size[1]);
    // }
    //
    // System.out.flush();
    // }
    //
    // }
    //
    // private int[] getImageDimensionsHack(String path) throws IOException {
    // String[] cmd = new String[] { "identify", "-ping", "-format", "%w %h ",
    // path + "[0]" };
    //
    // Process p = Runtime.getRuntime().exec(cmd);
    //
    // try {
    // if (p.waitFor() != 0) {
    // ByteArray buf = new ByteArray(1024);
    // buf.append(p.getErrorStream());
    // String err = new String(buf.array, 0, buf.length, "UTF-8");
    //
    // throw new IOException("Failed to run on " + path + ": " + err);
    //
    // }
    //
    // ByteArray buf = new ByteArray(1024);
    // buf.append(p.getInputStream());
    //
    // String result = new String(buf.array, 0, buf.length, "UTF-8");
    // String[] s = result.trim().split("\\s+");
    //
    // if (s.length != 2) {
    // throw new IOException("Invalid result " + result + " on "
    // + path);
    // }
    //
    // return new int[] { Integer.parseInt(s[0]),
    // Integer.parseInt(s[1].trim()) };
    // } catch (NumberFormatException e) {
    // throw new IOException("Invalid result.");
    // } catch (InterruptedException e) {
    // throw new IOException(e);
    // } finally {
    // p.destroy();
    // }
    // }

    // private void writeNewImageFiles() throws IOException {
    // BookCollection col = site.loadBookCollection();
    //
    // for (String bookid : col.books()) {
    // BookArchive archive = col.loadArchive(bookid);
    //
    // {
    // File file = new File(bookid + ".images.csv");
    //
    // if (!file.exists()) {
    // System.out.println("Running on " + bookid);
    // System.out.flush();
    //
    // FileWriter out = new FileWriter(file);
    //
    // for (String image : archive.getOrderedImages()) {
    // if (image.startsWith("*")) {
    // out.write(image + "\n");
    // } else {
    // String path = new File(archive.dir(), image)
    // .getPath();
    //
    // int[] size = getImageDimensionsHack(path);
    //
    // out.write(image + "," + size[0] + "," + size[1]
    // + "\n");
    // out.flush();
    // }
    // }
    //
    // out.close();
    // }
    // }
    //
    // if (archive.isCropped()) {
    // File file = new File("cropped", bookid + ".images.csv");
    //
    // if (!file.exists()) {
    // System.out.println("Running on cropped " + bookid);
    // System.out.flush();
    //
    // FileWriter out = new FileWriter(file);
    //
    // for (String image : archive.getOrderedImages()) {
    // if (image.startsWith("*")) {
    // out.write(image + "\n");
    // } else {
    // String path = new File(archive.cropDir(), image).getPath();
    //
    // int[] size = getImageDimensionsHack(path);
    //
    // out.write(image + "," + size[0] + "," + size[1]
    // + "\n");
    // out.flush();
    // }
    // }
    //
    // out.close();
    // }
    // }
    // }
    // }

    private void printPaginatedFileNames(String msname, int pages) {
        System.out.println(msname + ".binding.frontcover.tif");
        System.out.println(msname + ".binding.backcover.tif");

        System.out.println(msname + ".frontmatter.pastedown.tif");
        System.out.println(msname + ".frontmatter.flyleaf.01r.tif");
        System.out.println(msname + ".frontmatter.flyleaf.01v.tif");

        System.out.println(msname + ".endmatter.pastedown.tif");
        System.out.println(msname + ".endmatter.flyleaf.01r.tif");
        System.out.println(msname + ".endtmatter.flyleaf.01v.tif");

        for (int i = 1; i < pages; i++) {
            System.out
                    .println(msname + "." + String.format("%03d", i) + ".tif");
        }
    }

    private void runop(String derivname, String archivename, DerivativeOp op,
            int threads) throws IOException {

        BookCollection col = site.loadBookCollection();
        BookArchive archive = null;

        if (archivename != null) {
            archive = col.loadArchive(archivename);
        }

        Derivative deriv = getDerivative(col, System.out, derivname);

        if (deriv == null) {
            System.err.println("No such derivative: " + derivname);
            System.exit(1);
        }

        if (threads != -1) {
            deriv.setMaxThreads(threads);
        }

        System.out.println("Running " + deriv.name() + " " + op
                + (archive == null ? "" : ": " + archive.id()));
        runop(deriv, op, archive);

    }

    private void runop(Derivative deriv, DerivativeOp op, BookArchive archive) {
        boolean success;

        if (op == DerivativeOp.UPDATE) {
            success = archive == null ? deriv.update(false) : deriv.update(
                    archive, false);
        } else if (op == DerivativeOp.FORCE_UPDATE) {
            success = archive == null ? deriv.update(true) : deriv.update(
                    archive, true);
        } else if (op == DerivativeOp.VALIDATE) {
            success = archive == null ? deriv.validate() : deriv
                    .validate(archive);
        } else {
            success = archive == null ? deriv.check() : deriv.check(archive);
        }

        if (success) {
            System.out.println("Success: " + deriv.name());
        } else {
            System.out.println("Failure: " + deriv.name());
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: COMMAND ARG...");
            System.exit(1);
        }

        String cmd = args[0];

        //Tool tool = new Tool("/rosa/tool/config.properties");
	Tool tool = new Tool();

        if (cmd.equals("update")) {
            boolean force = false;
            int threads = -1;

            int next = 1;
            for (; next < args.length; next++) {
                if (args[next].equals("-f")) {
                    force = true;
                } else if (args[next].equals("-t")) {
                    threads = Integer.parseInt(args[++next]);
                } else {
                    break;
                }
            }

            if (args.length != next + 1 && args.length != next + 2) {
                System.err
                        .println("Usage: update [-f] [-t threads] DERIV ?ARCHIVE?");
                System.exit(1);
            }

            String derivname = args[next++];
            String archivename = args.length == next ? null : args[next];

            tool.runop(derivname, archivename,
                    force ? DerivativeOp.FORCE_UPDATE : DerivativeOp.UPDATE,
                    threads);
        } else if (cmd.equals("export-balaur-collection")) {
            tool.writeBalaurCollection(new File(args[1]));
        } else if (cmd.equals("check-files")) {
            for (int i = 1; i < args.length; i++) {
                String name = args[i];

                if (name.contains("description_")) {
                    List<String> errors = new ArrayList<String>();
                    new BookDescription(new File(name)).check(errors);

                    if (errors.size() > 0) {
                        System.err.println("Bad ms desc " + name);

                        for (String s : errors) {
                            System.err.println(s);
                        }
                    }

                } else {
                    System.err.println("Unknown file " + name);
                }
            }
        } else if (cmd.equals("validate")) {
            if (args.length != 2 && args.length != 3) {
                System.err.println("Usage: validate DERIV ?ARCHIVE?");
                System.exit(1);
            }

            String derivname = args[1];
            String archivename = args.length == 3 ? args[2] : null;

            tool.runop(derivname, archivename, DerivativeOp.VALIDATE, -1);
        } else if (cmd.equals("check")) {
            if (args.length != 2 && args.length != 3) {
                System.err.println("Usage: check DERIV ?ARCHIVE?");
                System.exit(1);
            }

            String derivname = args[1];
            String archivename = args.length == 3 ? args[2] : null;

            tool.runop(derivname, archivename, DerivativeOp.CHECK, -1);
        } else if (cmd.equals("check-bnf")) {
            for (int i = 1; i < args.length; i++) {
                System.out.println("Checking " + args[i]);
                tool.checkBNFManuscript(new File(args[i]));
            }
        } else if (cmd.equals("split-bnf")) {
            tool.splitBNFChecksums(new File(args[1]), new File(args[2]));
        } else if (cmd.equals("create-descriptions-from-spreadsheet")) {
            tool.createDescriptionsFromSpreadSheet(new File(args[1]), new File(
                    args[2]), new File(args[3]));
        } else if (cmd.equals("prepare-bnf")) {
            for (int i = 2; i < args.length; i++) {
                tool.prepareBNFManuscript(new File(args[1]), new File(args[i]));
            }
        } else if (cmd.equals("move-bnf")) {
            for (int i = 2; i < args.length; i++) {
                System.out.println("Moving " + args[i]);
                tool.moveBNFManuscript(new File(args[1]), new File(args[i]));
            }
        } else if (cmd.equals("replace-imagetag-illus-title-ids")) {
            BookCollection col = tool.site.loadBookCollection();
            IllustrationTitles titles = col.loadIllustrationTitles(null);
            CharacterNames chars = col.loadCharacterNames(null);

            for (int i = 1; i < args.length; i++) {
                String bookid = args[i];
                FileWriter out = new FileWriter(bookid + ".imagetag.csv");
                rosa.core.ImageTagging imgtag = col.loadArchive(bookid)
                        .imageTagging(null);
                imgtag.replaceStringsWithIdentifiers(titles, chars);
                imgtag.serialize(out);
                out.close();
            }
            // } else if (cmd.equals("write-illustration-titles-spreadsheet")) {
            // tool.writeIllustrationTitlesSpreadsheet(System.out);
            // System.out.flush();
            // } else if (cmd.equals("print-images-with-dimensions")) {
            // tool.printImagesWithDimentions(args[1]);
            // System.out.flush();
            // } else if (cmd.equals("write-new-images-files")) {
            // tool.writeNewImageFiles();
            // System.out.flush();
        } else if (cmd.equals("conf-get")) {
            // Must have wiki public key imported into truststore and then
            // -Djavax.net.ssl.trustStore=/path/to/truststore
            // http://confluence.atlassian.com/display/JIRA/Connecting+to+SSL+
            // services

            if (args.length != 4) {
                System.out
                        .println("Usage: sessioncookie pages|desc|perm|trans|nartag|img|bib destdir");
                System.exit(1);
            }

            String cookie = args[1];
            String type = args[2];
            File destdir = new File(args[3]);
            String url = "https://wiki.library.jhu.edu/";

            ConfGet conf = new ConfGet(url, cookie);
            String space = "ROSE";
            String page = null, regex = null;

            if (type.equals("pages")) {
                page = "Pages";
                regex = ".*.html";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("img")) {
                page = "Art+History";
                regex = ".*.csv";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("trans")) {
                page = "Rose_Transcription_Douce332";
                regex = "Douce332.*txt";
                conf.copyAttachments(space, page, regex, destdir);

                page = "Rose_Transcription_LudwigXV7";
                regex = "LudwigXV7.*txt";
                conf.copyAttachments(space, page, regex, destdir);

                page = "Rose_Transcription_SS57";
                regex = "SeldenSupra57.*txt";
                conf.copyAttachments(space, page, regex, destdir);

                page = "Rose_Transcription_CoxMacro";
                regex = "CoxMacro.*txt";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("nartag")) {
                page = "Narrative+Scene+Tagging";
                regex = ".*.txt";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("redtag")) {
                page = "Reduced+Tagging";
                regex = ".*redtag.txt";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("perm")) {
                page = "Image+Permission";
                regex = ".*.html";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("desc")) {
                page = "Manuscript+Description";
                regex = ".*description_.*.xml";
                conf.copyAttachments(space, page, regex, destdir);
            } else if (type.equals("bib")) {
                page = "Bibliographies";
                regex = ".*bibliography.xml";
                conf.copyAttachments(space, page, regex, destdir);
            } else {
                System.err.println("Bad type");
            }
        } else if (cmd.equals("print-ms-file-names")) {
            tool.printMSFileNames(args[1], Integer.parseInt(args[2]));
        } else if (cmd.equals("print-paginated-file-names")) {
            tool.printPaginatedFileNames(args[1], Integer.parseInt(args[2]));
        } else if (cmd.equals("print-reduced-tagging-template")) {
            ReducedTagging.generateTemplate(args[1], Integer.parseInt(args[2]),
                    System.out);
        } else if (cmd.equals("ls") || cmd.equals("list")) {
            tool.list();
        } else if (cmd.equals("rename-files")) {
            if (args.length != 4) {
                System.err
                        .println("Usage: rename-files OLD->NEW_CSV OLD_DIR NEW_DIR");
                System.exit(1);
            }

            tool.renameFiles(new File(args[1]), new File(args[2]), new File(
                    args[3]));
        } else if (cmd.equals("import-files")) {
            if (args.length < 2) {
                System.err.println("Usage: import-files FILE...");
                System.exit(1);
            }

            BookCollection col = tool.site.loadBookCollection();

            for (int i = 1; i < args.length; i++) {
                tool.importFile(col, new File(args[i]));
            }
        } else if (cmd.equals("test-narrative-mapping")) {
            if (args.length != 3) {
                System.err
                        .println("Usage: test-narrative-mapping BOOK_ID USE_SYNC");
                System.exit(1);
            }

            boolean usesync = Boolean.parseBoolean(args[2]);
            BookCollection col = tool.site.loadBookCollection();
            BookArchive archive = col.loadArchive(args[1]);
            List<String> errors = new ArrayList<String>();

            ReducedTagging redtag = archive.reducedTagging(errors);

            if (redtag == null) {
                System.err.println("No reduced tagging ");
                System.exit(1);
            }

            BookStructure struct = redtag.structure();

            if (errors.size() > 0) {
                System.err.println("Errors loading reduced tagging: ");

                for (String s : errors) {
                    System.err.println(s);
                }

                errors.clear();
            }

            NarrativeMapping guess = SceneMapping.guessNarrativeScenes(
                    col.loadNarrativeScenes(null), struct, usesync, null, null);
            NarrativeMapping truth = archive.narrativeTagging(errors);

            if (truth == null) {
                System.err.println("No narrative tagging ");
                System.exit(1);
            }

            if (errors.size() > 0) {
                System.err.println("Errors loading narrative tagging: ");

                for (String s : errors) {
                    System.err.println(s);
                }
            }

            SceneMapping.printComparison(truth, guess);
        } else {
            System.err.println("Unknown command: " + cmd);
            System.exit(1);
        }
    }

    // private void writeIllustrationTitlesSpreadsheet(PrintStream out)
    // throws IOException, SAXException {
    // out.append(CSV.escape("Id"));
    // out.append(',');
    //
    // out.append(CSV.escape("Illustration title"));
    // out.append(',');
    //
    // out.append(CSV.escape("Manuscripts"));
    // out.println();
    //
    // class Row {
    // String id;
    // String title;
    // List<String> locs;
    //
    // public Row(String title) {
    // this.locs = new ArrayList<String>();
    // this.title = title;
    // }
    // }
    //
    // Map<String, Row> rows = new HashMap<String, Row>();
    // BookCollection col = site.loadBookCollection();
    // IllustrationTitles titles = col.loadIllustrationTitles(null);
    //
    // for (String bookid : col.books()) {
    // BookArchive archive = col.loadArchive(bookid);
    // ImageTagging imgtag = archive.imageTagging(null);
    //
    // if (imgtag == null) {
    // continue;
    // }
    //
    // List<String> imagefiles = archive.getOrderedImages();
    //
    // int firstfolio = 0;
    //
    // for (String s : imagefiles) {
    // if (!s.contains("binding") && !s.contains("frontmatter")) {
    // break;
    // }
    //
    // firstfolio++;
    // }
    //
    // for (int i = 0; i < imgtag.numImages(); i++) {
    // for (String id : imgtag.titles(i)) {
    // if (!ImageTagging.isNumericalId(id)) {
    // continue;
    // }
    //
    // String title = titles.title(id);
    // Row row = rows.get(title);
    //
    // if (row == null) {
    // row = new Row(title);
    // row.id = id;
    // rows.put(title, row);
    // }
    //
    // String folio = imgtag.folio(i);
    // String msname = archive.description(
    // BookCollection.DEFAULT_LC).commonName();
    //
    // row.locs.add(msname + "[" + folio + "]");
    // }
    // }
    // }
    //
    // for (Row row : rows.values()) {
    // out.append(CSV.escape(row.id));
    // out.append(',');
    // out.append(CSV.escape(row.title));
    // out.append(',');
    //
    // StringBuilder sb = new StringBuilder();
    //
    // for (int i = 0; i < row.locs.size(); i++) {
    // sb.append(row.locs.get(i));
    //
    // if (i != row.locs.size() - 1) {
    // sb.append(", ");
    // }
    // }
    //
    // out.append(CSV.escape(sb.toString()));
    // out.println();
    // }
    //
    // }

    private void renameFiles(File csvmap, File olddir, File newdir)
            throws IOException {
        String[][] table = CSV.parseTable(new FileReader(csvmap));

        for (String[] entry : table) {
            if (entry.length == 0) {
                continue;
            }

            if (entry.length != 2) {
                throw new IOException("File map must be oldpath, newname: ");
            }

            File oldfile = new File(olddir, entry[0].trim());
            File newfile = new File(newdir, entry[1].trim());

            if (!oldfile.exists()) {
                System.err.println("Old file does not exist: " + oldfile);
                continue;
            }

            if (newfile.exists()) {
                System.err.println("Stopping because new file exists: "
                        + newfile + " for " + oldfile);
                break;
            }

            System.out.println(oldfile + " -> " + newfile);

            oldfile.renameTo(newfile);
        }
    }

    private void importFile(BookCollection col, File file) throws IOException {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                importFile(col, child);
            }
        } else {
            String filename = file.getName();
            String bookid = BookArchive.getID(filename);

            if (bookid == null) {
                System.err.println("Could not find bookid: " + filename);
                return;
            }

            File dir = col.dir(bookid);

            if (!dir.isDirectory()) {
                System.err.println("No such book: " + bookid);
                return;
            }

            File dest = new File(dir, filename);

            // if (dest.lastModified() > file.lastModified()) {
            // System.out.println("Skipping " + file.getName() +
            // " because destination is newer");
            // } else {
            System.out.println(file.getName() + " -> " + dir);
            FileUtil.copy(file, dest);
            // }
        }
    }

    // TODO no openjdk support for tif?
    // private int[] getImageDimensions(String path) throws IOException {
    // int[] result = null;
    //
    // String ext = "";
    // int i = path.lastIndexOf('.');
    //
    // if (i != -1) {
    // ext = path.substring(i + 1);
    // }
    //
    // Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(ext);
    //
    // if (iter.hasNext()) {
    // ImageReader reader = iter.next();
    //
    // try {
    // ImageInputStream stream = new FileImageInputStream(new File(
    // path));
    // reader.setInput(stream);
    //
    // int width = reader.getWidth(reader.getMinIndex());
    // int height = reader.getHeight(reader.getMinIndex());
    //
    // result = new int[] { width, height };
    // stream.close();
    // } finally {
    // reader.dispose();
    // }
    // }
    //
    // return result;
    // }

    private void list() throws IOException {
        System.out.println("Book Archives:");

        BookCollection col = site.loadBookCollection();

        for (String name : col.books()) {
            System.out.println(name);
        }

        System.out.println();
        System.out.println("Derivatives:");
        for (String name : DERIVATIVE_NAMES) {
            System.out.println(name);
        }
        System.out.println();
    }

    private static Element addElement(Document doc, Element parent,
            String name, String value) {
        Element el = doc.createElement(name);

        if (value != null) {
            el.setTextContent(value);
        }

        parent.appendChild(el);
        return el;
    }

    /**
     * Return image name with book id and extension stripped.
     */
    private static String shortImageName(String name) {
        int start = name.indexOf('.') + 1;

        // Also strip leading 0
        while (name.charAt(start) == '0') {
            start++;
        }

        int end = name.lastIndexOf('.');

        return name.substring(start, end);
    }

    private void writeBalaurCollection(File dir) throws Exception {
        BookCollection col = site.loadBookCollection();

        for (String id : col.books()) {
            System.out.println("Doing " + id);

            BookArchive book = col.loadArchive(id);

            Document doc = XMLUtil.createDocument();

            Element root = doc.createElement("collection");
            doc.appendChild(root);

            BookDescription desc = book.description("en");

            addElement(doc, root, "title",
                    desc.repository() + ", " + desc.shelfmark());

            FileReader perm_in = new FileReader(new File(book.dir(),
                    book.permissionName("en")));
            addElement(doc, root, "permission", FileUtil.readFull(perm_in)
                    .toString());
            perm_in.close();

            addElement(doc, root, "url", "http://romandelarose.org/#book;" + id);

            Element images = addElement(doc, root, "images", null);

            for (Image image : book.images()) {
                Element image_el = addElement(doc, images, "image", null);

                image_el.setAttribute("filename", image.fileName());

                addElement(doc, image_el, "title",
                        shortImageName(image.fileName()));
                addElement(doc, image_el, "url",
                        "http://romandelarose.org/#read;" + image.fileName());

            }

            File msdir = new File(dir, id);

            if (msdir.exists()) {
                FileWriter w = new FileWriter(new File(msdir, "collection.xml"));
                w.write(XMLUtil.toString(doc));
                w.close();
            }
        }
    }
}
