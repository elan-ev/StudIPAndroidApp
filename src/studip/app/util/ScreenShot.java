package studip.app.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.view.View;

public class ScreenShot {
	private final View view;

	/** Create snapshots based on the view and its children. */
	public ScreenShot(View root) {
		this.view = root;
	}

	/** Create snapshot handler that captures the root of the whole activity. */
	public ScreenShot(Activity activity) {
		final View contentView = activity.findViewById(android.R.id.content);
		this.view = contentView.getRootView();
	}

	/** Create snapshot handler that captures the view with target id of the activity. */
	public ScreenShot(Activity activity, int id) {
		this.view = activity.findViewById(id);
	}
	
	/** Take a snapshot of the view. */
	public Bitmap snap() {
		Bitmap bitmap = Bitmap.createBitmap(this.view.getWidth(), this.view.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return drawShadow(bitmap);
	}
	
	public Bitmap drawShadow(Bitmap bitmap) {
		int left = 12;
	    int leftMargin = (left + 7)/2;
	    
	    int w = bitmap.getWidth();
	    int h = bitmap.getHeight();

	    Bitmap.Config conf = Bitmap.Config.ARGB_8888;
	    Bitmap bmp = Bitmap.createBitmap(w, h, conf);
	    Bitmap sbmp = Bitmap.createScaledBitmap(bitmap, w, h, false);

	    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	    Canvas c = new Canvas(bmp);


	    Shader lshader = new LinearGradient(0, 0, leftMargin, 0, Color.TRANSPARENT, Color.BLACK, TileMode.CLAMP);
	    paint.setShader(lshader);
	    c.drawRect(0, 0, leftMargin, h, paint); 

	    c.drawBitmap(sbmp, leftMargin, 0, null);

	    return bmp;
	}
}
