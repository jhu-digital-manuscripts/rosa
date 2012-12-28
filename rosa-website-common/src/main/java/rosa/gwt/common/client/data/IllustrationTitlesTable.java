package rosa.gwt.common.client.data;

import rosa.gwt.common.client.Action;
import rosa.gwt.common.client.Searcher;
import rosa.gwt.common.client.resource.Labels;

import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;

public class IllustrationTitlesTable extends CsvTable {
	public IllustrationTitlesTable(String csv) {
		super(csv, 1, true);

		Column<String[], SafeHtml> titlecol = new Column<String[], SafeHtml>(
				new SafeHtmlCell()) {
			private final int col = 1;

			public SafeHtml getValue(String[] row) {
				if (col < row.length) {
					String title = row[col];
					String query = Searcher.createLiteralQuery(title);

					String url = "#"
							+ Action.SEARCH.toToken(
									Searcher.UserField.ILLUSTRATION_TITLE
											.name(), query, "0");
					String link = "<a href='" + URL.encode(url) + "'>" + title
							+ "</a>";

					return SafeHtmlUtils.fromTrustedString(link);
				}

				return SafeHtmlUtils.EMPTY_SAFE_HTML;
			}
		};

		displayIntegerColumn(0, Labels.INSTANCE.position(), true);
		displayColumn(titlecol, 1, Labels.INSTANCE.illustrationTitle(), false);
		displayIntegerColumn(2, Labels.INSTANCE.frequency(), false);
	}
}
