package rose.m3;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Access data from the Roman de la Rose website data directory.
 * 
 * TODO This code is derived from data loading code in rosa-website-common and
 * is a bit of a mess. There should be an interface for a generic collection of
 * manifests.
 * 
 * TODO caching
 * */

public class RoseCollection {
    // TODO auth issues
    // private static final String ROSE_DATA_URL =
    // "http://rosetest.library.jhu.edu/data/";
    private static final String ROSE_DATA_URL = "http://romandelarose.org/data/";
    private static final String BOOKS_EN_CSV = "books.csv";

    private final CSVSpreadSheet table;
    private final String data_url;

    private enum Column {
        ID, REPOSITORY, SHELFMARK, COMMON_NAME, LOCATION, DATE, ORIGIN, TYPE, NUM_ILLUSTRATIONS, NUM_FOLIOS, TRANSCRIPTION, ILLUSTRATION_TAGGING, NARRATIVE_TAGGING, BIBLIOGRAPHY;
    }

    public class Book {
        private final int row;

        private Book(int row) {
            this.row = row;
        }

        public String id() {
            return table.get(row, Column.ID.ordinal());
        }

        public String commonName() {
            return table.get(row, Column.COMMON_NAME.ordinal());
        }

        public String dataUrl() {
            return data_url + id() + "/";
        }
        
        public String descriptionUrl() {
            return dataUrl() + id() + ".description_en.xml";
        }
        

        // TODO Do cropped

        public ImageList retrieveImageList() throws IOException {
            InputStream is = new URL(imagesUrl()).openStream();
            ImageList result = new ImageList(id(), new CSVSpreadSheet(
                    new InputStreamReader(is, "UTF-8")));
            is.close();

            return result;
        }

        public ImageTagging retrieveIllustrationTagging(ImageList images)
                throws IOException {
            InputStream is = new URL(illustrationTaggingUrl()).openStream();
            ImageTagging result = new ImageTagging(images, new CSVSpreadSheet(
                    new InputStreamReader(is, "UTF-8")));
            is.close();

            return result;
        }

        private String illustrationTaggingUrl() {
            return dataUrl() + id() + ".imagetag.csv";
        }

        private String permissionUrl() {
            return dataUrl() + id() + ".permission_en.html";
        }

        public String permissionStatement() throws IOException {
            InputStream is = new URL(permissionUrl()).openStream();
            ByteArray buf = new ByteArray(4 * 1024);
            buf.append(is);
            is.close();

            return new String(buf.array, 0, buf.length, "UTF-8").trim();
        }

        public String repository() {
            return table.get(row, Column.REPOSITORY.ordinal());
        }

        public String location() {
            return table.get(row, Column.LOCATION.ordinal());
        }

        public boolean hasTranscription() {
            return table.get(row, Column.TRANSCRIPTION.ordinal()).equals("2");
        }

        public boolean hasIllustrationTagging() {
            return table.get(row, Column.ILLUSTRATION_TAGGING.ordinal())
                    .equals("2");
        }

        public String date() {
            String date = table.get(row, Column.DATE.ordinal());

            if (date == null || date.isEmpty()) {
                return "Unknown";
            }

            return date;
        }

        public String imagesUrl() {
            return dataUrl() + id() + ".images.csv";
        }

        public String fullName() {
            return repository() + ", " + commonName();
        }
    }

    /**
     * Return image name with book id and extension stripped.
     */
    public static String shortImageName(String name) {
        int start = name.indexOf('.') + 1;

        // Also strip leading 0
        while (name.charAt(start) == '0') {
            start++;
        }

        int end = name.lastIndexOf('.');

        return name.substring(start, end);
    }

    public class ImageList {
        private final CSVSpreadSheet table;
        private final String bookid;

        private ImageList(String bookid, CSVSpreadSheet table) {
            this.table = table;
            this.bookid = bookid;
        }

        public String image(int index) {
            String s = table.get(index, 0);

            if (s.charAt(0) == '*') {
                s = s.substring(1);
            }

            return s;
        }

        public int size() {
            return table.size();
        }

        public boolean missing(int index) {
            return table.get(index, 0).charAt(0) == '*';
        }

        public String transcriptionUrl(int i) {
            String image = image(i);
            int dot = image.indexOf('.');
            String bookid = image.substring(0, dot);
            String main = image.substring(dot + 1, image.length() - 4);

            return data_url + bookid + "/" + bookid + ".transcription." + main
                    + ".xml";
        }

        // TODO
        public String displayUrl(int i) {
            String image = image(i);
            String bookid = image.substring(0, image.indexOf('.'));

            return "http://fsiserver.library.jhu.edu/server?type=image&source=rose/"
                    + bookid + "/" + image;
        }

        public String iiifServiceUrl(int i) {
            String image = image(i);
            String bookid = image.substring(0, image.indexOf('.'));

            return "http://rosetest.library.jhu.edu/iiif/rose%2F" + bookid
                    + "%2F" + image + "/full/300,/0/native.jpg";
        }

        public int width(int index) {
            String s = table.get(index, 1);

            if (s == null) {
                return -1;
            }

            return Integer.parseInt(s);
        }

        public int height(int index) {
            String s = table.get(index, 2);

            if (s == null) {
                return -1;
            }

            return Integer.parseInt(s);
        }

        public int find(String image) {
            for (int i = 0; i < table.size(); i++) {
                String s = image(i);

                if (image.equalsIgnoreCase(s)
                        || (s.startsWith("*") && image.equalsIgnoreCase(s
                                .substring(1)))) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * Return name of image matching fragment or -1.
         * 
         * @return
         */
        public int guessImage(String frag) {
            frag = frag.trim();

            // try to guess whether or not the book is paginated
            boolean paginated = isPaginatedImage(image(size() / 2));

            if (!paginated) {
                if (frag.matches("\\d+")) {
                    frag += "r";
                } else if (frag.matches("[a-zA-Z]\\d+")) {
                    // Deals with printed books: A1
                    frag = frag.toUpperCase() + "r";
                }
            }

            if (frag.matches("\\d[rRvV]?")) {
                frag = "00" + frag;
            } else if (frag.matches("\\d\\d[rRvV]?")) {
                frag = "0" + frag;
            }

            if (!frag.endsWith(".tif")) {
                frag += ".tif";
            }

            if (!frag.startsWith(bookid)) {
                frag = bookid + "." + frag;
            }

            return find(frag);
        }

        public int guess(String frag) {
            frag = frag.trim();

            // try to guess whether or not the book is paginated
            boolean paginated = isPaginatedImage(image(table.size() / 2));

            if (!paginated) {
                if (frag.matches("\\d+")) {
                    frag += "r";
                } else if (frag.matches("[a-zA-Z]\\d+")) {
                    // Deals with printed books: A1
                    frag = frag.toUpperCase() + "r";
                }
            }

            if (frag.matches("\\d[rRvV]?")) {
                frag = "00" + frag;
            } else if (frag.matches("\\d\\d[rRvV]?")) {
                frag = "0" + frag;
            }

            if (!frag.endsWith(".tif")) {
                frag += ".tif";
            }

            if (!frag.startsWith(bookid)) {
                frag = bookid + "." + frag;
            }

            return find(frag);
        }
    }

    public static class ImageTagging {
        private final CSVSpreadSheet table;
        private final int map[]; // For each illustration index, give image
                                 // index in

        private enum Column {
            IMAGE_ID, FOLIO, TITLES, TEXTUAL_ELEMENTS, INITIALS, CHARACTERS, COSTUME, LANDSCAPE, OTHER;
        }

        private ImageTagging(ImageList images, CSVSpreadSheet table) {
            this.table = table;
            this.map = new int[numIllustrations()];

            for (int illus = 0; illus < map.length; illus++) {
                String folio = value(illus, Column.FOLIO);

                if (folio != null) {
                    map[illus] = images.guess(folio);
                }
            }
        }

        public int numIllustrations() {
            return table.size() - 1;
        }

        private String value(int imageindex, Column d) {
            return table.get(imageindex + 1, d.ordinal());
        }

        /**
         * Return illustration indexes for given image in a book.
         */
        public List<Integer> findIllusIndexes(int image) {
            List<Integer> result = new ArrayList<Integer>();

            for (int i = 0; i < map.length; i++) {
                if (map[i] == image) {
                    result.add(i);
                }
            }

            return result;
        }

        public String folio(int illus) {
            return value(illus, Column.FOLIO);
        }
        
        public String descriptionHtml(int illus) {
            StringBuilder sb = new StringBuilder();

            sb.append("<dl>");
            add(sb, illus, "Titles", Column.TITLES);
            add(sb, illus, "Characters", Column.CHARACTERS);
            add(sb, illus, "Costume", Column.COSTUME);
            add(sb, illus, "Initials", Column.INITIALS);
            add(sb, illus, "Landscape", Column.LANDSCAPE);
            add(sb, illus, "Text", Column.TEXTUAL_ELEMENTS);
            add(sb, illus, "Other", Column.OTHER);
            sb.append("</dl>");

            return sb.toString();
        }
        
        private void add(StringBuilder sb, int illus, String name, Column col) {
            String value = value(illus, col);
            
            if (!value.isEmpty()) {
                sb.append("<dt>" + name + "</dt>");
                
                // TODO Do real escaping!!
                value = value.replaceAll("<", "&lt;");
                value = value.replaceAll(">", "&gt;");

                sb.append("<dd>" + value + "</dd>");
            }
        }

    }
    

    public static boolean isPaginatedImage(String image) {
        String s = image.substring(image.indexOf('.') + 1);
        return s.matches("\\d+\\.tif");
    }

    public RoseCollection(CSVSpreadSheet table, String data_url) {
        this.table = table;
        this.data_url = data_url;
    }

    public RoseCollection() throws IOException {
        InputStream is = new URL(ROSE_DATA_URL + BOOKS_EN_CSV).openStream();
        this.table = new CSVSpreadSheet(new InputStreamReader(is, "UTF-8"));
        this.data_url = ROSE_DATA_URL;
        is.close();
    }

    public Book findBook(String id) {
        for (int i = 0; i < table.size(); i++) {
            Book b = getBook(i);

            if (b.id().equals(id)) {
                return b;
            }
        }

        return null;
    }

    public Book getBook(int index) {
        return new Book(index);
    }

    public int size() {
        return table.size();
    }

    public String dataUrl() {
        return data_url;
    }

    public String name() {
        return "Roman de la Rose Digital Library";
    }
}
