package rosa.scanvas.demo.website.client;

public class HistoryState {
	
	private final String VALUE_DELIMITER = ";";
	private final String PANEL_DELIMITER = ":";
	
	private PanelState[] states;

	/**
	 * Create a new HistoryStateImpl object from a History token
	 * @param token
	 */
	public HistoryState(String token) {
		int index = 0;
		String[] panels = token.split(PANEL_DELIMITER);
		states = new PanelState[panels.length];
		
		for (String pan : panels) {
			String[] values = pan.split(VALUE_DELIMITER);
			String view = values[0];
			
			if (view.equals(PanelView.HOME.toString())) {
				
				states[index] = new PanelState();
				
			} else if (view.equals(PanelView.MANIFEST.toString()) ||
						view.equals(PanelView.MANIFEST_COLLECTION.toString())) {
				
				states[index] = new PanelState(PanelView.valueOf(values[0]), values[1]);
				
			} else if (view.equals(PanelView.SEQUENCE.toString()) ||
						view.equals(PanelView.CANVAS.toString())) {
				
				states[index] = new PanelState(PanelView.valueOf(values[0]), values[1], values[2]);
				
			}
			
			index++;
		}
	}
	
	/**
	 * 
	 * @return length of the PanelState array
	 */
	public int size() {
		return states.length;
	}
	
	/**
	 * 
	 * @param index
	 * @return PanelState at specified index
	 */
	public PanelState getPanelState(int index) {
		return states[index];
	}

	/**
	 * Sets the panel state of a specified index to the specified new state
	 * @param newState
	 * @param index
	 */
	public void setPanelState(PanelState newState, int index) {
		states[index] = newState;
	}

	/**
	 * 
	 * @return 
	 */
	public String toToken() {
		String token = "";
		
		for (PanelState state : states) {
			String view = state.getView().toString();
			String object = state.getObjectUri();
			String manifest = state.getManifestUri();
			
			if (object == null) {
				token += view + PANEL_DELIMITER;
			} else if (manifest == null) {
				token += view + VALUE_DELIMITER +
						object + PANEL_DELIMITER;
			} else {
				token += view + VALUE_DELIMITER +
						object + VALUE_DELIMITER + 
						manifest + PANEL_DELIMITER;
			}
			
		}
		
		return token;
	}
	
}
