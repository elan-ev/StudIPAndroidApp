package studip.app.view.util;

import java.util.ArrayList;
import studip.app.R;
import studip.app.net.ServerItem;
import studip.app.net.SignInActivity;
import studip.app.view.activities.ActivitiesItem;
import studip.app.view.news.NewsItem;
import studip.app.view.slideout.MenuActivity;
import studip.app.view.slideout.MenuItem;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class GeneralListFragment extends ListFragment {

	ArrayList<ArrayAdapterItem> itemList;
	
	public GeneralListFragment(ArrayList<ArrayAdapterItem> itemList) {
		this.itemList = itemList;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		this.setListAdapter(new GeneralArrayAdapter(this.getActivity(), R.layout.general_item, itemList));
		getListView().setCacheColorHint(0);
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		//MenuItem
		if (l.getItemAtPosition(position).getClass().equals(MenuItem.class)) {
			MenuItem mi = (MenuItem)l.getItemAtPosition(position);
			((MenuActivity)getActivity()).getSlideoutHelper().close(mi.id);
		} else if (l.getItemAtPosition(position) instanceof NewsItem) {
			
		} else if (l.getItemAtPosition(position) instanceof ActivitiesItem) {
			String str = ((ActivitiesItem)l.getItemAtPosition(position)).activity.title;
			Toast toast = Toast.makeText(this.getActivity().getApplicationContext(), str, Toast.LENGTH_LONG);
			toast.show();
		} else if (l.getItemAtPosition(position) instanceof ServerItem) {
			ServerItem si = (ServerItem)l.getItemAtPosition(position);
			SignInActivity.selectedServer = si.server;
			si.activity.finish();
		}
	}
}
