package rosa.gwt.common.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import rosa.search.SearchResult;

public interface SearchServiceAsync {
	void search(String query, int offset, int maxmatches,
			AsyncCallback<SearchResult> cb);
}
 
