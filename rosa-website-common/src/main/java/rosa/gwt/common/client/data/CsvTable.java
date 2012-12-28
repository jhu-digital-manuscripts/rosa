package rosa.gwt.common.client.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rosa.gwt.common.client.Util;

import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.view.client.ListDataProvider;

/**
 * Table view of CSV data such that columns are named and sortable.
 */

public class CsvTable extends ResizeComposite {
	private final DataGrid<String[]> table;
	private final ListDataProvider<String[]> ldp;
	private final ListHandler<String[]> sorthandler;
	private final boolean sortable;

	protected final String[][] data;
	protected final int data_offset;

	public CsvTable(String csv, int data_offset, boolean sortable) {
		this.data = Util.parseCSVTable(csv);
		this.data_offset = data_offset;
		this.sortable = sortable;
		this.table = new DataGrid<String[]>();

		table.setStylePrimaryName("DataTable");
		//table.setTableLayoutFixed(true);

		List<String[]> datalist = Arrays.asList(data);

		// Strip out header
		datalist = datalist.subList(data_offset, datalist.size());

		this.ldp = new ListDataProvider<String[]>(datalist);

		ldp.addDataDisplay(table);

		table.setRowCount(ldp.getList().size(), true);
		table.setVisibleRange(0, ldp.getList().size());

		// TODO: Hack to make sure display is refreshed after sorting

		this.sorthandler = new ListHandler<String[]>(datalist) {
			public void onColumnSort(ColumnSortEvent event) {
				super.onColumnSort(event);
				ldp.refresh();
			}
		};

		table.addColumnSortHandler(sorthandler);

		initWidget(table);
	}

	public void displayStringColumn(int col, String name, boolean start_sorted) {
		StringCsvColumn c = new StringCsvColumn(col);

		table.addColumn(c, name);

		if (sortable) {
			c.setSortable(true);

			Comparator<String[]> cmp = new StringCsvComparator(col);

			sorthandler.setComparator(c, cmp);

			if (start_sorted) {
				table.getColumnSortList().push(c);
				Collections.sort(ldp.getList(), cmp);
				ldp.refresh();
			}
		}
	}

	public void displayColumn(Column<String[], ?> c, int col, String name,
			boolean start_sorted) {
		table.addColumn(c, name);

		if (sortable) {
			c.setSortable(true);

			Comparator<String[]> cmp = new StringCsvComparator(col);

			sorthandler.setComparator(c, cmp);

			if (start_sorted) {
				table.getColumnSortList().push(c);
				Collections.sort(ldp.getList(), cmp);
				ldp.refresh();
			}
		}
	}

	public void displayIntegerColumn(int col, String name, boolean start_sorted) {
		IntegerCsvColumn c = new IntegerCsvColumn(col);

		table.addColumn(c, name);

		if (sortable) {
			c.setSortable(true);

			Comparator<String[]> cmp = new IntegerCsvComparator(col);

			sorthandler.setComparator(c, cmp);

			if (start_sorted) {
				table.getColumnSortList().push(c);
				Collections.sort(ldp.getList(), cmp);
				ldp.refresh();
			}
		}
	}

	private static class StringCsvColumn extends TextColumn<String[]> {
		private final int col;

		public StringCsvColumn(int col) {
			this.col = col;
		}

		public String getValue(String[] row) {
			if (row.length <= col) {
				return "";
			}

			return row[col];
		}
	}

	private static class StringCsvComparator implements Comparator<String[]> {
		private final int col;

		public StringCsvComparator(int col) {
			this.col = col;
		}

		public int compare(String[] row1, String[] row2) {
			String s1 = col < row1.length ? row1[col] : null;
			String s2 = col < row2.length ? row2[col] : null;

			if (s1 != null) {
				return (s2 != null) ? s1.compareTo(s2) : 1;
			}

			return -1;
		}
	}

	private static class IntegerCsvColumn extends Column<String[], Number> {
		private final int col;

		public IntegerCsvColumn(int col) {
			super(new NumberCell());
			this.col = col;

		}

		public Number getValue(String[] row) {
			if (row.length <= col) {
				return -1;
			}

			return row[col].isEmpty() ? null : new Integer(row[col]);
		}
	}

	private static class IntegerCsvComparator implements Comparator<String[]> {
		private final int col;

		public IntegerCsvComparator(int col) {
			this.col = col;
		}

		public int compare(String[] row1, String[] row2) {
			String s1 = col < row1.length ? row1[col] : null;
			String s2 = col < row2.length ? row2[col] : null;

			if (s1 != null && !s1.isEmpty()) {
				return (s2 != null && !s2.isEmpty()) ? new Integer(s1)
						.compareTo(new Integer(s2)) : 1;
			}

			return -1;
		}
	}
}
