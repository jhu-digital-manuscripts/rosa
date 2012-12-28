package rosa.gwt.common.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import rosa.search.SearchResult;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {
	SearchResult search(String query, int offset, int maxmatches)
			throws RPCException;
}
