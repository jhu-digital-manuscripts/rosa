package rosa.scanvas.demo.website.client.widgets;

import rosa.scanvas.demo.website.client.dynimg.MasterImage;

public class Opening {
    private MasterImage verso;
    private String verso_label;
    private MasterImage recto;
    private String recto_label;
    private int verso_index;
    private int recto_index;

    public Opening(MasterImage verso, String verso_label, int verso_index,
    		MasterImage recto, String recto_label,  int recto_index) {
        this.verso = verso;
        this.verso_label = verso_label;
        this.verso_index = verso_index;
        this.recto = recto;
        this.recto_label = recto_label;
        this.recto_index = recto_index;
    }

    public MasterImage getVerso() {
        return verso;
    }

    public String getVersoLabel() {
        return verso_label;
    }
    
    public int getVersoIndex() {
    	return verso_index;
    }

    public MasterImage getRecto() {
        return recto;
    }

    public String getRectoLabel() {
        return recto_label;
    }
    
    public int getRectoIndex() {
    	return recto_index;
    }
}
