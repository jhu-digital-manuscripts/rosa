package rosa.scanvas.demo.website.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the current state of the application encoded in the browser URL.
 */
public class HistoryState {
    // Must use characters that cannot appear in URLs.
    private final static String VALUE_DELIMITER_PATTERN = ";";
    private final static String PANEL_DELIMITER_PATTERN = "\\|";
    private final static String VALUE_DELIMITER = ";";
    private final static String PANEL_DELIMITER = "|";

    private List<PanelState> states;

    /**
     * Create a new HistoryState object from a History token. Return null on
     * failure.
     * 
     * @param token
     * @return history state
     */
    public static HistoryState parseHistoryToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        String[] panels = token.split(PANEL_DELIMITER_PATTERN);

        if (panels.length == 0) {
            return null;
        }

        PanelState[] states = new PanelState[panels.length];

        for (int i = 0; i < panels.length; i++) {
            String[] values = panels[i].split(VALUE_DELIMITER_PATTERN);

            if (values.length == 0) {
                return null;
            }

            PanelView view = PanelView.forHistoryName(values[0]);

            if (view == null) {
                return null;
            }

            if (view == PanelView.MANIFEST
                    || view == PanelView.MANIFEST_COLLECTION) {
                if (values.length != 2) {
                    return null;
                }

                states[i] = new PanelState(view, values[1]);
            } else if (view == PanelView.SEQUENCE) {
                if (values.length != 3) {
                    return null;
                }

                states[i] = new PanelState(view, values[1], values[2]);
            } else if (view == PanelView.CANVAS) {
                if (values.length != 4) {
                    return null;
                }

                try {
                    states[i] = new PanelState(view, values[1],
                            values[2], Integer.parseInt(values[3]));
                } catch (NumberFormatException e) {
                    return null;
                }
            } else {
                states[i] = new PanelState();
            }
        }

        return new HistoryState(states);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((states == null) ? 0 : states.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof HistoryState))
            return false;
        HistoryState other = (HistoryState) obj;
        if (states == null) {
            if (other.states != null)
                return false;
        } else if (!states.equals(other.states))
            return false;
        return true;
    }

    public HistoryState(PanelState... states) {
        this.states = new ArrayList<PanelState>(Arrays.asList(states));
    }

    public List<PanelState> panelStates() {
        return states;
    }

    /**
     * Convert the current state to a history token.
     * 
     * @return history token
     */
    public String toToken() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < states.size(); i++) {
            PanelState state = states.get(i);

            String object = state.getObjectUri();

            if (i > 0) {
                sb.append(PANEL_DELIMITER);
            }

            sb.append(state.getView().historyName());

            if (object != null) {
                sb.append(VALUE_DELIMITER + object);

                String manifest = state.getManifestUri();

                if (state.getView() != PanelView.MANIFEST && manifest != null) {
                    sb.append(VALUE_DELIMITER + manifest);

                    int canvas_index = state.getCanvasIndex();

                    if (canvas_index != -1) {
                        sb.append(VALUE_DELIMITER + canvas_index);
                    }
                }
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "HistoryState [states=" + states + "]";
    }
}
