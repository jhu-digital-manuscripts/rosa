package pizan.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.TextResource;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("App.css")
	public CssResource css();

	@Source("books.csv")
	public TextResource bookBrowseTable();

	@Source("collection_data.csv")
	public ExternalTextResource collectionDataTable();

	@Source("illustration_titles.csv")
	public ExternalTextResource illustrationTitlesTable();

	@Source("character_names.csv")
	public ExternalTextResource characterNamesTable();

	@Source("home.html")
	public TextResource homeHtml();
	
	@Source("christine_de_pizan.html")
	public ExternalTextResource christineDePizanHtml();

	@Source("works.html")
	public ExternalTextResource worksHtml();
    
	@Source("proper_names.html")
    public ExternalTextResource properNamesHtml();
    
	@Source("partners.html")
	public ExternalTextResource partnersHtml();

	@Source("terms_and_conditions.html")
	public ExternalTextResource termsAndConditionsHtml();

	@Source("contact.html")
	public ExternalTextResource contactHtml();

	@Source("illustration_titles.html")
	public ExternalTextResource illustrationTitlesHtml();

	@Source("character_names.html")
	public ExternalTextResource characterNamesHtml();

	@Source("collection_data.html")
	public ExternalTextResource collectionDataHtml();
	
}
