package rosa.tool.deriv;

import java.io.IOException;
import java.io.PrintStream;

import rosa.core.BookArchive;
import rosa.core.BookCollection;
import rosa.tool.Config;

/**
 * Manage deriving data from a BookCollection
 */
public abstract class Derivative {
	protected final Config site;
	protected final BookCollection col;
	protected final PrintStream report;
	protected int maxthreads;

	protected Derivative(Config site, PrintStream report) throws IOException {
		this.site = site;
		this.col = site.loadBookCollection();
		this.report = report;
		this.maxthreads = 1;
	}

	protected void reportError(String msg, Exception e) {
		report.println("Error: " + msg + ": " + e);
		e.printStackTrace(report);
	}

	/**
	 * Attempt update of collection derivative. Progress and errors reported.
	 * 
	 * @return success or failure.
	 */

	public boolean update(boolean force) {
		boolean success = true;

		for (String id : col.books()) {
			report.println("Updating " + id);
			report.flush();
			
			try {
				if (!update(col.loadArchive(id), force)) {
					success = false;
				}
			} catch (IOException e) {
				report.println("Failed to load archive " + id + ": " + e);
			}
		}

		return success;
	}

	public abstract boolean update(BookArchive archive, boolean force);

	/**
	 * Do a quick check of derivative against the collection. Results reported.
	 * 
	 * @return success or failure
	 * @param report
	 */

	public boolean check() {
		boolean success = true;

		for (String id : col.books()) {
			report.println("Checking " + id);

			try {
				if (!check(col.loadArchive(id))) {
					success = false;
				}
			} catch (IOException e) {
				report.println("Failed to load archive " + id + ": " + e);
			}
		}

		return success;
	}

	public abstract boolean check(BookArchive archive);

	/**
	 * Do a thorough validation of the derived data. Print results.
	 * 
	 * @return success or failure
	 * @param report
	 */
	public boolean validate() {
		boolean success = true;

		for (String id : col.books()) {
			try {
				if (!validate(col.loadArchive(id))) {
					success = false;
				}
			} catch (IOException e) {
				report.println("Failed to load archive " + id + ": " + e);
			}
		}

		return success;
	}

	public abstract boolean validate(BookArchive archive);

	/**
	 * @return Short human readable name.
	 */
	public abstract String name();

	/**
	 * Set the maximum threads used to process jobs.
	 * 
	 * @param max
	 */
	public void setMaxThreads(int max) {
		this.maxthreads = max;
	}
}
