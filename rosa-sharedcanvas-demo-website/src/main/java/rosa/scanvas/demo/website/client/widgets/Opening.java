package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.demo.website.client.dynimg.MasterImage;

public class Opening {
    private MasterImage verso;
    private String verso_label;
    private MasterImage recto;
    private String recto_label;

    public Opening(MasterImage verso, String verso_label, MasterImage recto,
            String recto_label) {
        this.verso = verso;
        this.verso_label = verso_label;
        this.recto = recto;
        this.recto_label = recto_label;
    }

    public MasterImage getVerso() {
        return verso;
    }

    public String getVersoLabel() {
        return verso_label;
    }

    public MasterImage getRecto() {
        return recto;
    }

    public String getRectoLabel() {
        return recto_label;
    }
}
