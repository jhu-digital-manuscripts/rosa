package rosa.gwt.common.client.data;

import rosa.gwt.common.client.Action;
import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

public class CollectionDataTable extends CsvTable {
	public CollectionDataTable(String csv) {
		super(csv, 1, true);

		// Set date start column to date start-end

		final int date_start_col = 10;
		final int date_end_col = 11;

		for (int row = 1; row < data.length; row++) {
			String yrstart = data[row][date_start_col];
			String yrend = data[row][date_end_col];

			if (yrstart == null || yrstart.isEmpty() || yrend == null
					|| yrend.isEmpty()) {
				data[row][date_start_col] = "";
			} else {
				data[row][date_start_col] = yrstart + "-" + yrend;
			}
		}

		// Set width column to heightXwidth

		final int height_col = 5;
		final int width_col = 6;

		for (int row = 0; row < data.length; row++) {
			String w = data[row][width_col];
			String h = data[row][height_col];

			if (w != null && !w.isEmpty() && h != null && !h.isEmpty()) {
				data[row][width_col] = h + "x" + w;
			} else {
				data[row][width_col] = "";
			}
		}

		// Click on book name to view

		Column<String[], SafeHtml> name_col = new Column<String[], SafeHtml>(
				new SafeHtmlCell()) {
			private final int col = 1;

			public SafeHtml getValue(String[] row) {
				if (col < row.length) {
					String id = row[0];
					String name = row[1];

					String url = "#" + Action.VIEW_BOOK.toToken(id);
					String link = "<a href='" + URL.encode(url) + "'>" + name
							+ "</a>";

					return SafeHtmlUtils.fromTrustedString(link);
				}

				return SafeHtmlUtils.EMPTY_SAFE_HTML;
			}
		};

		displayColumn(name_col, 1, Labels.INSTANCE.name(), true);
		displayStringColumn(date_start_col, Labels.INSTANCE.date(), false);
		displayIntegerColumn(4, Labels.INSTANCE.folios(), false);
		displayIntegerColumn(9, Labels.INSTANCE.numIllustrationsShort(), false);
		displayIntegerColumn(12, Labels.INSTANCE.colsPerFolio(), false);
		displayIntegerColumn(8, Labels.INSTANCE.linesPerColumn(), false);
		displayStringColumn(width_col, Labels.INSTANCE.dimensions(), false);
		displayIntegerColumn(7, Labels.INSTANCE.leavesPerGathering(), false);
		displayIntegerColumn(15,
				Labels.INSTANCE.foliosWithGreaterThanOneIllustration(), false);
	}
}
