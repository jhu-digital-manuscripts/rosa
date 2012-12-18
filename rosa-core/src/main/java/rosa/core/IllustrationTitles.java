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

public class IllustrationTitles {
	public static String NAME = "illustration_titles.csv";

	private enum Column {
		ID, TITLE
	}

	private final Map<String, String> data;

	public IllustrationTitles(Reader input, List<String> errors)
			throws IOException {
		this.data = new HashMap<String, String>();

		CSVSpreadSheet table = new CSVSpreadSheet(input, 2, 2, errors);

		for (int row = 1; row < table.size(); row++) {
			String id = table.get(row, Column.ID.ordinal());
			String title = table.get(row, Column.TITLE.ordinal());

			if (data.containsKey(id) && errors != null) {
				errors.add("Id " + id + " already exists");
			}

			data.put(id, title);
		}
	}

	public IllustrationTitles(File file, List<String> errors)
			throws IOException {
		this(new FileReader(file), errors);
	}

	public Set<String> ids() {
		return data.keySet();
	}

	public String title(String id) {
		return data.get(id);
	}
	
	public String findIdOfTitle(String title) {
	    for (String id: data.keySet()) {
	        if (data.get(id).equals(title)) {
	            return id;
	        }
	    }
	    
	    return null;
	}
}
