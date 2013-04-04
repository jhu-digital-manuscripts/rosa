package rosa.scanvas.viewer.client;

import com.google.gwt.user.client.History;

public class HistoryInfo {
	
	/*
	 * #id ; view ; tab ; collection ; manifest ; sequence ; canvas ;
	 */
	
	public static String newToken(String id, String view, String tab) {
		return 	id + ";" + view + ";" + tab + ";:";
	}
	
	public static String newToken(String id, String view, String tab, String collection) {
		return id + ";" + view + ";" + tab + ";" + collection + ";:";
	}
	
	public static String newToken(String id, String view, String tab, String collection, String manifest) {
		return id + ";" + view + ";" + tab + ";" + collection + ";" + manifest + ";:";
	}
	
	public static String newToken(String id, String view, String tab, String collection, 
			String manifest, String sequence, String canvas) {
		return id+";"+view+";"+tab+";"+collection+";"+manifest+";"+sequence+";"+canvas+";:";
	}
	
	/**
	 * Gets the number of segments in the history tokens that represent separate
	 * panels.
	 * @param token String: full history token
	 * @return int
	 */
	public static int getNumItems(String token) {
		String[] parts = token.split(":");
		
		return parts.length;
	}
	
	/**
	 * Gets the index of the panel with the specified id.
	 * @param token history token
	 * @param id
	 * @return the index of the id. If the id is not found, -1 will be returned.
	 */
	public static int getPosition(String token, String id) {
		String[] parts = token.split(":");
		
		for (int i=0; i<parts.length; i++) {
			String segmentId = parts[i].split(";")[0];
			if (id.equals(segmentId)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public static String getId(String token) {
		return token.split(";")[0];
	}
	
	public static String getId(String token, int position) {
		return token.split(":")[position].split(";")[0];
	}
	
	public static String getView(String token) {
		return token.split(";")[1];
	}	
	
	public static String getView(String token, int position) {
		return token.split(":")[position].split(";")[1];
	}
	
	public static String getTab(String token) {
		return token.split(";")[2];
	}
	
	public static String getTab(String token, int position) {
		return token.split(":")[position].split(";")[2];
	}
	
	/**
	 * Change the view attribute of a specified panel within the current history token.
	 * Ex: change 'home' to 'manifest'
	 * @param token String: the current history token segment
	 * @param newView String: the new view that is being switched into
	 * @return String holding the changed history token segment
	 */
	public static String changeView(String id, String newView) {
		String[] parts = History.getToken().split(":");
		
		String newToken = "";
		for (int i=0; i<parts.length; i++) {
		
			if (id.equals(HistoryInfo.getId(parts[i]))) {
				String[] segment = parts[i].split(";");
				segment[1] = newView;
				
				for (String elem : segment) {
					newToken += elem + ";";
				}
				newToken += ":";
			} else {
				newToken += parts[i] + ":";
			}
		}
		
		return newToken;
	}
	
	public static String setTab(String id, String tab) {
		String newToken = "";
		String[] parts = History.getToken().split(":");
		
		for (int i=0; i<parts.length; i++) {
			
			if (id.equals(getId(parts[i]))) {
				String[] segment = parts[i].split(";");
				segment[2] = tab;
				
				for (String elem : segment) {
					newToken += elem + ";";
				}
				newToken += ":";
			} else {
				newToken += parts[i] + ":";
			}
		}
		
		return newToken;
	}
	
	/**
	 * Set the collection attribute of a specified panel within the current history token.
	 * If the collection attribute does not exist, it will be appended to the history token.
	 * If the collection attribute already exists, its value will be changed.
	 * @param id String: the id of the panel to be changed
	 * @param collection String: the url of the collection
	 * @return the new history token
	 */
	public static String setCollection(String id, String collection) {
		String[] parts = History.getToken().split(":");
		
		String newToken = "";
		for (int i=0; i<parts.length; i++) {
			if (id.equals(HistoryInfo.getId(parts[i]))) {
				String[] segment = parts[i].split(";");
				
				if (segment.length > 3) {
					segment[3] = collection;
					for(int j=0; j<segment.length; j++) {
						newToken += segment[j] + ";";
					}
					
					newToken += ":";
				} else if (segment.length <= 3) {
					newToken = parts[i] + collection + ";:";
				}
				
			} else {
				newToken += parts[i] + ":";
			}
		}
		
		return newToken;
	}
}
