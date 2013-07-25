package rosa.scanvas.demo.website.client.disparea;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.CanvasElement;

import com.google.gwt.user.client.Window;

public class ZoomAnimation extends Animation {
	
	private final Context2d context;
	private final CanvasElement canvas_img;
	
	private final int width_from;
	private final int height_from;
	
	private final AnimationCallback cb;
	
	private final int x_from;
	private final int y_from;
	private final int x_to;
	private final int y_to;
	
	private final double scale;
	
	/**
	 * Animate zoom in on the HTML5 canvas, after the DisplayArea has zoomed in.
	 * 
	 * @param context
	 * @param canvas_img
	 * 			the CanvasElement of the canvas to be animated
	 * @param cb
	 * 			callback for when the animation completes
	 * @param old_width
	 * 			the width of the viewport before zoom
	 * @param new_width
	 * 			the width of the viewport after zoom, in the same coordinates as old_width
	 * @param new_x
	 * 			x coordinate of the new viewport center, relative to the HTML5 canvas 
	 * 			in the browser
	 * @param new_y
	 * 			y coordinate of the new viewport center, relative to the HTML5 canvas
	 * 			in the browser
	 */
	public ZoomAnimation(Context2d context, CanvasElement canvas_img,
			AnimationCallback cb, int old_width, int new_width, int new_x, int new_y) {
		this.context = context;
		
		Canvas buffer = Canvas.createIfSupported();
		Context2d buff_context = buffer.getContext2d();
		
		buffer.setPixelSize(canvas_img.getWidth(), canvas_img.getHeight());
		buffer.setCoordinateSpaceWidth(canvas_img.getWidth());
		buffer.setCoordinateSpaceHeight(canvas_img.getHeight());
		
		buff_context.drawImage(canvas_img, 0, 0);
		
		this.canvas_img = buff_context.getCanvas();
		
		scale = (double) old_width / new_width;
		
		this.width_from = canvas_img.getWidth();
		this.height_from = canvas_img.getHeight();
		
		this.x_from = width_from / 2;
		this.y_from = height_from / 2;
		
		this.x_to = new_x;
		this.y_to = new_y;
		
		this.cb = cb;
	}
	
	@Override
	protected void onComplete() {
		cb.onAnimationComplete();
	}
	
	@Override
	protected void onStart() {
		
	}

	@Override
	protected void onUpdate(double progress) {
		double zoom = (scale - 1) * progress + 1;
		
		int x = (int) ((x_to - x_from) * progress) + x_from;
		int y = (int) ((y_to - y_from) * progress) + y_from;
		
		context.clearRect(0, 0, width_from, height_from);
		
		context.save();
		
		context.translate(width_from / 2, height_from / 2);
		context.scale(zoom, zoom);
		context.translate(-(x + width_from / 2), -(y + height_from / 2));

		context.drawImage(canvas_img, width_from / 2, height_from / 2);
		
		context.restore();
	}

}
