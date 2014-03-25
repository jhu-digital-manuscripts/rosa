package pizan.gwt.client;

import java.util.List;

/**
 * Global Javascript var pageTracker must be set.
 */
public class Analytics {
	/**
	 * @param path
	 *            must start with /
	 */
	public static native void trackPageView(String path) /*-{
	  if ($wnd.pageTracker) {  
	    $wnd.pageTracker._trackPageview(path);
	  }
	}-*/;

	public static native void trackEvent(String category, String action,
			String label) /*-{
	  if ($wnd.pageTracker) {  			
	    $wnd.pageTracker._trackEvent(category, action, label);
	  }
	}-*/;

	public static native void trackEvent(String category, String action,
			String label, int value) /*-{
      if ($wnd.pageTracker) {  
	    $wnd.pageTracker._trackEvent(category, action, label, value);
	  }
	}-*/;

	// TODO move to Action? Defer track commands to try to avoid ui locking?

	public static void track(Action action, String bookid, List<String> args) {
		String actionname = "";
		String category = "";
		String label = "";
		int value = -1;

		switch (action) {
		case BROWSE_BOOK:
			category = "Book";
			actionname = "browse-images";
			label = bookid;
			break;
		case HOME:
			category = "Page";
			actionname = "view";
			label = "home";
			break;
		case READ_BOOK:
			category = "Book";
			actionname = "turn-pages";
			label = bookid;
			break;
		case SEARCH:
			category = "Search";
			actionname = "search";

			StringBuilder sb = new StringBuilder();

			for (int i = 0; i < args.size() - 1;) {
				sb.append(args.get(i++) + ": ");
				sb.append(args.get(i++));
			}

			label = sb.toString();
			value = Integer.parseInt(args.get(args.size() - 1));
			break;
		case SELECT_BOOK:
			category = "Browse";
			actionname = "select";
			label = args.get(0);
			break;
		case VIEW_BOOK:
			category = "Book";
			actionname = "view";
			label = bookid;
			break;
		case VIEW_CONTACT:
			category = "Page";
			actionname = "view";
			label = "contact";
			break;
		case VIEW_WORKS:
			category = "Page";
			actionname = "view";
			label = "works";
			break;
		case VIEW_PARTNERS:
			category = "Page";
			actionname = "view";
			label = "partners";
			break;
		case VIEW_PIZAN:
			category = "Page";
			actionname = "view";
			label = "christine de pizan";
			break;
		case VIEW_TERMS:
			category = "Page";
			actionname = "view";
			label = "terms";
			break;
	     case VIEW_PROPER_NAMES:
	         category = "Page";
	         actionname = "view";
	         label = "proper names";
	         break;
		}

		if (value == -1) {
			trackEvent(category, actionname, label);
		} else {
			trackEvent(category, actionname, label, value);
		}
	}
}
