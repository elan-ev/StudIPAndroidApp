package studip.app.view.util;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextView extends TextView {

	public FontTextView(Context context) {
		super(context);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setTypeface(Typeface tf, int style) {
		
		String fontpath;

		switch (style) {
		default:
			fontpath = "fonts/Gill_Sans.ttf";
			break;
		case Typeface.BOLD:
			fontpath = "fonts/Gill_Sans_Extra_Bold.ttf";
			break;

		case Typeface.ITALIC:
			fontpath = "fonts/Gill_Sans_Italic.ttf";
			break;
		}
	    
	    tf = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), fontpath);
	    super.setTypeface(tf, 0);
	}

}
