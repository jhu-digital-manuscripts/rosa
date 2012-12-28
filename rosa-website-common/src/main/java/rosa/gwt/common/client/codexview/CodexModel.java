package rosa.gwt.common.client.codexview;


public interface CodexModel {
    int numImages();
    CodexImage image(int index);
    
    int numOpenings();
    CodexOpening opening(int index);
    
    // TODO ugly
    int numNonOpeningImages();
    CodexImage nonOpeningImage(int index);
}
