package rosa.core;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rosa.core.util.CSVSpreadSheet;

/**
 * English column has , separated names.
 */
public class CharacterNames {
	public static String NAME = "character_names.csv";

	private enum Column {
		ID, SITE_NAME, OLD_FRENCH_NAMES, MODERN_ENGLISH_NAMES
	}

	private final Map<String, Integer> idmap; // id -> row
	private final CSVSpreadSheet table;

	public CharacterNames(Reader input, List<String> errors) throws IOException {
		this.idmap = new HashMap<String, Integer>();
		this.table = new CSVSpreadSheet(input, Column.values().length, Column.values().length, errors);

		for (int row = 1; row < table.size(); row++) {
			String id = table.get(row, Column.ID.ordinal());

			if (idmap.containsKey(id) && errors != null) {
				errors.add("Id " + id + " already exists");
			}

			idmap.put(id, row);
		}
	}

	public CharacterNames(File file, List<String> errors) throws IOException {
		this(new FileReader(file), errors);
	}

	public Set<String> ids() {
		return idmap.keySet();
	}

	public String siteName(String id) {
		return table.get(idmap.get(id), Column.SITE_NAME.ordinal());
	}

	public String[] oldFrenchNames(String id) {
		return table.get(idmap.get(id), Column.OLD_FRENCH_NAMES.ordinal())
				.split("\\s*,\\s*");
	}

	public String[] modernEnglishNames(String id) {
		return table.get(idmap.get(id), Column.MODERN_ENGLISH_NAMES.ordinal())
				.split("\\s*,\\s*");
	}
	
	public String findIdOfChar(String name) {
        for (String id: idmap.keySet()) {
            String s = siteName(id);
            
            if (s != null && s.equals(name)) {
                return id;
            }
        }
        
        return null;
    }
}
