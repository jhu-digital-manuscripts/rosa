package rosa.core;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class BookCollection {
	public static String DEFAULT_LC = Locale.ENGLISH.getLanguage();
	public static String MISSING_IMAGE_NAME = "missing_image.tif";

	private final File dir;
	private final String[] archives;

	public BookCollection(File dir) throws IOException {
		this.dir = dir;

		this.archives = dir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File f = new File(dir, name);

				return f.isDirectory() && !f.getName().endsWith(".ignore");
			}
		});
	}

	public String configName() {
		return "config";
	}

	public String[] books() {
		return archives;
	}

	public boolean exists(String name) {
		return new File(dir, name).exists();
	}

	// Guess based on file names
	// TODO
	private boolean isManuscriptArchive(String id) {
		for (int i = 1; i < 10; i++) {
			File test = new File(new File(dir(), id), id + "."
					+ ManuscriptArchive.toFolio(i, 'r') + BookArchive.IMAGE_EXT);

			if (test.exists()) {
				return true;
			}
		}

		return false;
	}

	public BookArchive loadArchive(String id) throws IOException {
		if (!new File(dir, id).isDirectory()) {
			return null;
		}

		if (isManuscriptArchive(id)) {
			return new ManuscriptArchive(new File(dir(), id));
		} else {
			return new PrintedBookArchive(new File(dir(), id));
		}
	}

	public NarrativeSections loadNarrativeScenes(List<String> errors)
			throws IOException {
		File f = new File(dir, NarrativeSections.NAME);

		if (!f.exists()) {
			return null;
		}

		return new NarrativeSections(f, errors);
	}

	public File dir(String name) {
		return new File(dir, name);
	}

	public File dir() {
		return dir;
	}

	// TODO this shouldn't be here
	public String[] languages() {
		return new String[] { "en", "fr" };
	}

	public CharacterNames loadCharacterNames(List<String> errors)
			throws IOException {
	    File file = new File(dir, CharacterNames.NAME);
	    
	    if (!file.exists()) {
	        return null;
	    }
	    
		return new CharacterNames(file , errors);
	}

	public IllustrationTitles loadIllustrationTitles(List<String> errors)
			throws IOException {
		return new IllustrationTitles(new File(dir, IllustrationTitles.NAME),
				errors);
	}
}
