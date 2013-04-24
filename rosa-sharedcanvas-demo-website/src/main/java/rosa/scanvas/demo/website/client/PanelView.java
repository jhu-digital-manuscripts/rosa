package rosa.scanvas.demo.website.client;

public enum PanelView {
    HOME("home"), MANIFEST_COLLECTION("col"), SEQUENCE("seq"), MANIFEST(
            "manifest"), CANVAS("canvas");

    private PanelView(String history_name) {
        this.history_name = history_name;
    }

    private String history_name;

    public String historyName() {
        return history_name;
    }

    public static PanelView forHistoryName(String name) {
        for (PanelView view : PanelView.values()) {
            if (view.history_name.equals(name)) {
                return view;
            }
        }

        return null;
    }
}