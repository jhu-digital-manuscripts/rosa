package rosa.scanvas.demo.website.client.dynimg;

import com.google.gwt.http.client.URL;

public final class FsiImageServer extends AbstractImageServer {
	private final String baseurl;

	public FsiImageServer(String baseurl) {
		this.baseurl = baseurl;
	}

	public int maxRenderSize() {
		return 1000;
	}

	public int tileSize() {
		return 500;
	}

	public String renderAsUrl(MasterImage image, int width, int height,
			int... crop) {
		return baseurl + "?type=image&source="
				+ URL.encodeQueryString(image.id()) + "&width="
				+ width
				+ "&height="
				+ height
				// rect is top left x,y and then width, height, all as dimension
				// percentages
				+ (crop.length == 4 ? "&rect="
						+ ((double) crop[0] / image.width()) + ","
						+ ((double) crop[1] / image.height()) + ","
						+ (((double) crop[2] - crop[0]) / image.width()) + ","
						+ (((double) crop[3] - crop[1]) / image.height()) : "");
	}
}
