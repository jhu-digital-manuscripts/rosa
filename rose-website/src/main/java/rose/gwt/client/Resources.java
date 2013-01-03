package rose.gwt.client;

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

	@Source("narrative_sections.csv")
	public ExternalTextResource narrativeSectionsTable();

	@Source("character_names.csv")
	public ExternalTextResource characterNamesTable();

	@Source("home.html")
	public TextResource homeHtml();

	@Source("partners.html")
	public ExternalTextResource partnersHtml();

	@Source("project_history.html")
	public ExternalTextResource projectHistoryHtml();

	@Source("rose_history.html")
	public ExternalTextResource roseHistoryHtml();

	@Source("terms_and_conditions.html")
	public ExternalTextResource termsAndConditionsHtml();

	@Source("contact.html")
	public ExternalTextResource contactHtml();

	@Source("donation.html")
	public ExternalTextResource donationHtml();

	@Source("rose_corpus.html")
	public ExternalTextResource roseCorpusHtml();

	@Source("illustration_titles.html")
	public ExternalTextResource illustrationTitlesHtml();

	@Source("character_names.html")
	public ExternalTextResource characterNamesHtml();

	@Source("narrative_sections.html")
	public ExternalTextResource narrativeSectionsHtml();

	@Source("collection_data.html")
	public ExternalTextResource collectionDataHtml();
}
