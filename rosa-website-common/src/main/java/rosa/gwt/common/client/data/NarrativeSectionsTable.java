package rosa.gwt.common.client.data;

import rosa.gwt.common.client.Action;
import rosa.gwt.common.client.Searcher;
import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

public class NarrativeSectionsTable extends CsvTable {
	private static String normalizeSectionId(String id) {
		return id.replaceAll("\\s", "").toLowerCase();
	}

	public NarrativeSectionsTable(String csv) {
		super(csv, 1, false);

		Column<String[], SafeHtml> idcol = new Column<String[], SafeHtml>(
				new SafeHtmlCell()) {
			private final int col = 0;

			public SafeHtml getValue(String[] row) {
				if (col < row.length) {
					String id = normalizeSectionId(row[col]);

					String url = "#"
							+ Action.SEARCH
									.toToken(
											Searcher.UserField.NARRATIVE_SECTION
													.name(), id, "0");
					String link = "<a href='" + URL.encode(url) + "'>" + id
							+ "</a>";

					return SafeHtmlUtils.fromTrustedString(link);
				}

				return SafeHtmlUtils.EMPTY_SAFE_HTML;
			}
		};

		displayColumn(idcol, 0, Labels.INSTANCE.sectionId(), false);
		displayStringColumn(3, Labels.INSTANCE.description(), false);
		displayStringColumn(2, Labels.INSTANCE.lecoy(), false);
	}

	/**
	 * Return index for section id or -1
	 */
	public int findSection(String id) {
		id = normalizeSectionId(id);

		for (int row = data_offset; row < data.length; row++) {
			if (data[row][0].equals(id)) {
				return row;
			}
		}

		return -1;
	}

	public String getDescription(int index) {
		return data[index][3];
	}
}
