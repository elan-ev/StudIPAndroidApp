package studip.app.net;

import java.util.ArrayList;
import studip.app.view.util.AbstractFragmentActivity;
import studip.app.view.util.ArrayAdapterItem;
import studip.app.view.util.TextItem;

public class ChooseServerActivity extends AbstractFragmentActivity {
	
	public ChooseServerActivity() {
		super("Select facility");
	}

	@Override
	public ArrayList<ArrayAdapterItem> getItems() {
		
		ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();
		
		//TODO load serverlist...
		
		Server oldenburg = new Server("uni-ol", "4763db64d4776df0fbc47c49ecc74a7104fa7702a", "7b1ee183fb7e22bc3dcee53991b00b6e", "http://devel09.uni-oldenburg.de/trunk/plugins.php/restipplugin");
		
		items.add(new TextItem("Free"));
		items.add(new ServerItem(this, oldenburg));
		
		return items;
	}
}
