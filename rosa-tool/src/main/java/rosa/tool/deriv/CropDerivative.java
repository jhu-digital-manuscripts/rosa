package rosa.tool.deriv;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import rosa.core.BookArchive;
import rosa.tool.Config;
import rosa.core.util.FileUtil;

// TODO figure how how to put script in jar and exec it...
// TODO Write out images file as well...

/**
 * Cropped images are stored in the book archive.
 */
public class CropDerivative extends Derivative {
	public static String NAME = "crop";

	public CropDerivative(Config site, PrintStream report) throws IOException {
		super(site, report);
	}

	public String name() {
		return NAME;
	}

	public boolean convert(File src, File dest, double[] cropdata, boolean force) {
		boolean success = true;

		if (force || !dest.exists() || src.lastModified() > dest.lastModified()) {
			report.println("Updating " + dest);

			try {
				String[] cmd = new String[] { site.cropScript().getPath(),
						src.getPath(), dest.getPath(), "" + cropdata[0],
						"" + cropdata[1], "" + cropdata[2], "" + cropdata[3] };

				if (!FileUtil.exec(cmd, report)) {
					success = false;
				}
			} catch (IOException e) {
				success = false;
				reportError("Creating " + dest, e);
			}
		}

		return success;
	}

	public boolean update(BookArchive archive, boolean force) {
		boolean success = true;

		File cropdir = archive.cropDir();

		Map<String, double[]> cropdata;

		try {
			cropdata = archive.getCroppingData();
		} catch (IOException e) {
			reportError("Loading cropping data", e);
			return false;
		}

		if (cropdata == null) {
			report.println("No cropping data");
			return false;
		}

		cropdir.mkdir();

		for (String image : archive.filenames()) {
			if (!archive.isImage(image)) {
				continue;
			}

			File cropimage = new File(cropdir, image);
			File masterimage = new File(archive.dir(), image);

			double[] data = cropdata.get(image);

			if (data == null) {
				report.println("No cropping data for " + image);
			} else {
				if (!convert(masterimage, cropimage, data, force)) {
					success = false;
				}
			}
		}

		return success;
	}

	public boolean check(BookArchive archive) {
		boolean success = true;

		for (String image : archive.filenames()) {
			if (!archive.isImage(image)) {
				continue;
			}

			File cropped = new File(archive.cropDir(), image);

			if (!cropped.exists()) {
				if (report != null) {
					report.println("Missing cropped image for " + image);
				}

				success = false;
			}
		}

		// TODO check images file
		
		return success;
	}

	public boolean validate(BookArchive archive) {
		return check();
	}
}
