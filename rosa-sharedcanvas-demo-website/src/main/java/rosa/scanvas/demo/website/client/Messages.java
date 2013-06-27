package rosa.scanvas.demo.website.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Messages extends Constants {
	public static final Messages INSTANCE = GWT.create(Messages.class);
	
	public String appHeader();
	
	public String unhandledView();
	
	public String errorOnView();
	
	public String failedToParseHistory();
	
	public String svgError();

	public String load();
	
	public String collection();
	
	public String homeLabel();
	
	public String homeLabelUser();
	
	public String homeUserInstruction();
	
	public String collectionInstruction();
	
	public String manifestInstruction();
	
	public String pageTurner();
	
	public String thumbnailBrowser();
	
	public String zoomIn();
	
	public String zoomOut();
	
	public String reset();
	
	public String close();
	
	public String swap();
	
	public String duplicate();
	
	public String annotationsHeader();
	
	public String metadataHeader();
	
	public String textHeader();
	
	public String optionsHeader();
	
	public String move();
	
	public String contextSeparator();
	
	public String showAll();
	
	public String hideAll();
	
	public String images();
	
	public String text();
	
	public String collectionHtml();
	
	public String manifestHtml();
	
	public String sequenceHtml();
	
	public String sequencePicker();
	
	public String manifestTitle();
	
	public String manifestAgent();
	
	public String manifestLocation();
	
	public String manifestDate();
	
	public String manifestDescription();
	
	public String manifestRights();
	
	public String sequenceImages();
	
	public String manifestItems();
	
	public String plus();
	
	public String minus();
	
	public String prev();
	
	public String gotoButton();
	
	public String next();
	
	public String errorGettingList();
}
