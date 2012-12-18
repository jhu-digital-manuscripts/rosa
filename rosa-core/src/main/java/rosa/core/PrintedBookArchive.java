package rosa.core;

import java.io.File;
import java.io.IOException;

// TODO integrate with ManuscriptArchive?

public class PrintedBookArchive extends BookArchive {
	public PrintedBookArchive(File dir) throws IOException {
		super(dir);
	}
}
