package rosa.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import rosa.core.util.CSVSpreadSheet;

public class ImageTagging {
    public static final String SUFFIX = "imagetag.csv";

    private enum Column {
        IMAGE_ID, FOLIO, TITLES, TEXTUAL_ELEMENTS, INITIALS, CHARACTERS, COSTUME, OBJECTS, LANDSCAPE, ARCHITECTURE, OTHER
    }

    private final CSVSpreadSheet table;

    public ImageTagging(Reader input, List<String> errors) throws IOException {
        this.table = new CSVSpreadSheet(input, 3, Column.values().length,
                errors);
    }

    public ImageTagging(File file, List<String> errors) throws IOException {
        this(new FileReader(file), errors);
    }

    public String folio(int image) {
        return table.get(image + 1, Column.FOLIO.ordinal());
    }

    public String textualElements(int image) {
        return table.get(image + 1, Column.TEXTUAL_ELEMENTS.ordinal());
    }

    public String costume(int image) {
        return table.get(image + 1, Column.COSTUME.ordinal());
    }

    public String objects(int image) {
        return table.get(image + 1, Column.OBJECTS.ordinal());
    }

    public String landscape(int image) {
        return table.get(image + 1, Column.LANDSCAPE.ordinal());
    }

    public String architecture(int image) {
        return table.get(image + 1, Column.ARCHITECTURE.ordinal());
    }

    public String other(int image) {
        return table.get(image + 1, Column.OTHER.ordinal());
    }

    /**
     * Each title is either a numerical id in IllustrationTitles or a non-rose
     * title
     */
    public String[] titles(int image) {
        return table.get(image + 1, Column.TITLES.ordinal()).split("\\s*,\\s*");
    }

    /**
     * Each character is either a numerical id in CharacterNames or the name of
     * some secondary figure.
     */
    public String[] characters(int image) {
        return table.get(image + 1, Column.CHARACTERS.ordinal()).split(
                "\\s*,\\s*");
    }

    public int numImages() {
        return table.size() - 1;
    }

    public static boolean isNumericalId(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void check(BookArchive archive, IllustrationTitles titles,
            CharacterNames chars, List<String> errors) {
        for (int i = 0; i < numImages(); i++) {
            String folio = folio(i);
            String filename = archive.guessImageName(folio);

            if (filename == null || !archive.exists(filename)) {
                errors.add("Cannot not find matching image for folio " + folio);
            }

            for (String id : titles(i)) {
                if (isNumericalId(id)) {
                    if (!titles.ids().contains(id)) {
                        errors.add("No such title id: " + id);
                    }
                }
            }

            for (String id : characters(i)) {
                if (isNumericalId(id)) {
                    if (!chars.ids().contains(id)) {
                        errors.add("No such character name id: " + id);
                    }
                }
            }
        }
    }

    /**
     * Replaces title and char ids with actual strings
     */
    public void replaceIdentifiersWithStrings(IllustrationTitles titles,
            CharacterNames chars) {

        for (int i = 0; i < numImages(); i++) {
            StringBuilder buf = new StringBuilder();
            String[] ids = titles(i);

            for (int j = 0; j < ids.length; j++) {
                if (j > 0) {
                    buf.append(", ");
                }

                if (isNumericalId(ids[j])) {
                    buf.append(titles.title(ids[j]));
                } else {
                    buf.append(ids[j]);
                }
            }

            table.set(i + 1, Column.TITLES.ordinal(), buf.toString());
        }

        for (int i = 0; i < numImages(); i++) {
            StringBuilder buf = new StringBuilder();
            String[] ids = characters(i);

            for (int j = 0; j < ids.length; j++) {
                if (j > 0) {
                    buf.append(", ");
                }

                if (isNumericalId(ids[j])) {
                    buf.append(chars.siteName(ids[j]));
                } else {
                    buf.append(ids[j]);
                }
            }

            table.set(i + 1, Column.CHARACTERS.ordinal(), buf.toString());
        }
    }

    public void replaceStringsWithIdentifiers(IllustrationTitles titles,
            CharacterNames chars) {

        for (int i = 0; i < numImages(); i++) {
            StringBuilder buf = new StringBuilder();
            String[] title_strings = titles(i);

            for (int j = 0; j < title_strings.length; j++) {
                if (j > 0) {
                    buf.append(", ");
                }

                String s = title_strings[j];
                
                if (s == null || s.isEmpty()) {
                    continue;
                }
                
                String id = titles.findIdOfTitle(s);

                if (id == null) {
                    System.err.println("Could not find title: " + s);
                    buf.append(s);
                } else {
                    buf.append(id);
                }
            }

            table.set(i + 1, Column.TITLES.ordinal(), buf.toString());
        }

        for (int i = 0; i < numImages(); i++) {
            StringBuilder buf = new StringBuilder();
            String[] names = characters(i);

            for (int j = 0; j < names.length; j++) {
                if (j > 0) {
                    buf.append(", ");
                }
                
                String s = names[j];
                
                if (s == null || s.isEmpty()) {
                    continue;
                }

                String id = chars.findIdOfChar(s);
                
                if (id == null) {
                    System.err.println("Could not find name: " + s);
                    buf.append(s);
                } else {
                    buf.append(id);
                }                
            }

            table.set(i + 1, Column.CHARACTERS.ordinal(), buf.toString());
        }
    }

    public void serialize(Writer out) throws IOException {
        table.serialize(out);
    }

    /**
     * @param filename
     * @param manuscript
     * @return all image data indexes for images contained in filename
     */
    public List<Integer> findImageIndexes(String filename, BookArchive archive) {
        List<Integer> result = new ArrayList<Integer>();

        for (int i = 0; i < numImages(); i++) {
            String s = archive.guessImageName(folio(i));

            if (s != null && s.equals(filename)) {
                result.add(i);
            }
        }

        return result;
    }
}
