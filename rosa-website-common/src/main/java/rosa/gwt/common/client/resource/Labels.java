package rosa.gwt.common.client.resource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface Labels extends Constants {
	public static final Labels INSTANCE = GWT.create(Labels.class);
	
	public String addSearchField();

	public String advancedSearch();

	public String allFields();

	public String book();

	public String bookDescriptionUnavailable();

	public String bookDescription();

	public String browseImages();

	public String catchphrase();

	public String city();

	public String clearTextBox();

	public String close();

	public String contactUs();

	public String criticalNote();

	public String date();

	public String decreaseSize();

	public String dimensions();

	public String error();

	public String filter();

	public String folio();

	public String folios();

	public String help();

	public String hits();

	public String illustration();

	public String illustrationChar();

	public String illustrationDescription();

	public String illustrationKeywords();

	public String illustrations();

	public String illustrationTitle();

	public String imageName();

	public String imagePermission();

	public String increaseSize();

	public String language();

	public String lecoy();

	public String line();

	public String mainPage();

	public String manuscript();

	public String maximize();

	public String narrativeSections();

	public String next();

	public String numFolios();

	public String numIllustrations();

	public String origin();

	public String owner();

	public String pageTurner();

	public String partners();

	public String linesOfVerse();

	public String popup();

	public String previous();

	public String printedbook();

	public String removeSearchField();

	public String repository();

	public String restrictByBook();

	public String rubric();

	public String search();

	public String selectBook();

	public String selectBookBy();

	public String shelfmark();

	public String transcription();

	public String type();

	public String unknownValue();

	public String blog();

	public String usingWebService();

	public String complete();

	public String partial();

	public String none();

	public String transcriptionUnavailable();

	public String currentLocation();

	public String commonName();

	public String roseHistory();

	public String projectHistory();

	public String termsAndConditions();

	public String donation();

	public String show();

	public String other();

	public String costume();

	public String architecture();

	public String landscape();

	public String objects();

	public String roseCorpus();

	public String description();

	public String sectionId();

	public String survey();

	public String project();

	public String collectionData();

	public String updated();

	public String frequency();

	public String identifier();

	public String name();

	public String height();

	public String material();

	public String width();

	public String leavesPerGathering();

	public String linesPerColumn();

	public String english();

	public String french();

	public String illustrationTitles();

	public String characterNames();

	public String startDate();

	public String endDate();

	public String colsPerFolio();

	public String content();

	public String foliosWithOneIllustration();

	public String foliosWithGreaterThanOneIllustration();

	public String rosePlus();

	public String roseExtract();

	public String roseFragment();

	public String roseOnly();

	public String download();

	public String viewInGoogleDocs();

	public String numIllustrationsShort();

	public String bibliography();

	public String bibliographyUnavailable();

	public String text();
	
	public String folioRange();

	public String initials();

	public String textualElements();

	public String texts();

    public String position();

    public String  first();
    
    public String  last();
    
    public String  zoomIn();
    
    public String  zoomOut();
    
    public String  zoomOriginal();
}
