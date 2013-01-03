package rosa.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import rosa.core.BookCollection;

/**
 * Root for accessing collection data and site configuration.
 */
public class Config {
	private final File collectionpath;
	private final String fsiservershare;
	private final File datapath;
	private final File searchindexpath;
	private final File resourcespath;
	private final File cropscript;

	private static String getProperty(Properties p, String name)
			throws IOException {
		String value = p.getProperty(name);

		if (value == null) {
			throw new IOException("Required property missing: " + name);
		}

		return value;
	}

	private static Properties findProperties(String propfilename)
			throws IOException {
		URL res = Config.class.getResource(propfilename);
		
		if (res == null) {
			throw new IOException("Cannot find required properties resource: " + propfilename);
		}
		
		InputStream is = res.openStream();
		
		Properties p = new Properties();
		p.load(is);
		is.close();
		
		return p;
	}

    

	public Config(String propfilename) throws IOException {
		this(findProperties(propfilename));
	}

    public Config() throws IOException {
	this(System.getProperties());
    }

	public Config(Properties p) throws IOException {
		this.collectionpath = new File(getProperty(p, "rosa.collection.path"));
		this.fsiservershare = getProperty(p, "rosa.website.fsi.share");
		this.datapath = new File(getProperty(p, "rosa.website.resources.path"));
		this.searchindexpath = new File(getProperty(p, "rosa.website.luceneindex.path"));
		this.resourcespath = new File(getProperty(p,
				"rosa.website.gwt.resources.path"));
		this.cropscript = null; // TODO new File(getProperty(p, "rose.crop.script"));
	}

	public BookCollection loadBookCollection() throws IOException {
		return new BookCollection(collectionpath);
	}

	public String fsiServerShare() {
		return fsiservershare;
	}

	public File dataPath() {
		return datapath;
	}

	public File dataPath(String bookid) {
		return new File(datapath, bookid);
	}

	public File dataPath(String bookid, String filename) {
		return new File(dataPath(bookid), filename);
	}

	public File resourcesPath() {
		return resourcespath;
	}

	public File searchIndexPath() {
		return searchindexpath;
	}

	public File cropScript() {
		return cropscript;
	}
}
