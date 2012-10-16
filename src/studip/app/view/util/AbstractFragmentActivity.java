package studip.app.view.util;

import java.util.ArrayList;
import studip.app.R;
import studip.app.view.slideout.MenuActivity;
import studip.app.view.slideout.SlideoutActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public abstract class AbstractFragmentActivity extends FragmentActivity {	
	
	private String title;
	
	public AbstractFragmentActivity(String title) {
		this.title = title;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

		this.setContentView(R.layout.general_view);
		
		((FontTextView)this.findViewById(R.id.title)).setText(title);
	    
		((ImageButton)this.findViewById(R.id.slide_button)).setOnClickListener(
				new OnClickListener() {
					// @Override
					public void onClick(View v) {
						slideButtonPressed(v);
					}
				});
		
		GeneralListFragment glf = new GeneralListFragment(this.getItems());
		
	    getSupportFragmentManager().beginTransaction().add(R.id.placeholder, glf, title).commit();
	}
	
	public abstract ArrayList<ArrayAdapterItem> getItems();
	
	public void slideButtonPressed(View view) {
		view.setSelected(false);
		int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, getResources().getDisplayMetrics());
		SlideoutActivity.prepare(this, R.id.inner_content, width);
		startActivity(new Intent(this, MenuActivity.class));
		overridePendingTransition(0, 0);
	}
}
