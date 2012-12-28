package rosa.gwt.common.client.data;

import java.util.ArrayList;
import java.util.List;

import rosa.gwt.common.client.resource.Labels;

public class CharacterNamesTable extends CsvTable {
	public CharacterNamesTable(String csv) {
		super(csv, 1, true);

		displayStringColumn(1, Labels.INSTANCE.name(), true);
		displayStringColumn(2, Labels.INSTANCE.french(), false);
		displayStringColumn(3, Labels.INSTANCE.english(), false);
	}

	public String[][] asSearchVariants() {
		String[][] result = new String[data.length - 1][];

		List<String> variants = new ArrayList<String>(10);

		for (int i = data_offset; i < data.length; i++) {
			String[] row = data[i];

			if (row.length > 1) {
				variants.add(row[1]);

				if (row.length > 2) {
					for (String s : row[2].split(",")) {
						variants.add(s);
					}
				}

				if (row.length > 3) {
					for (String s : row[3].split(",")) {
						variants.add(s);
					}
				}
			}

			result[i - 1] = variants.toArray(new String[] {});
			variants.clear();
		}

		return result;
	}
}
