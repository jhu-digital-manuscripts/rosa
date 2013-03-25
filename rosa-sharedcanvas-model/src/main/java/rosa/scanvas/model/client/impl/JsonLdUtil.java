package rosa.scanvas.model.client.impl;

public class JsonLdUtil {
    boolean isBlankNodeId(String s) {
        return s.startsWith("_");
    }

    boolean isKeyword(String s) {
        return s.startsWith("@");
    }

    boolean isFullIRI(String s) {
        return s.contains("://");
    }

    boolean isCompactIRI(String s) {
        return !isFullIRI(s) && s.contains(":");
    }
}
