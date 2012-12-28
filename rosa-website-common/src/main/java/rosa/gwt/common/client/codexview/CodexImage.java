package rosa.gwt.common.client.codexview;

import rosa.gwt.common.client.dynimg.MasterImage;

public interface CodexImage extends MasterImage {
    String label();

    boolean missing();
}
