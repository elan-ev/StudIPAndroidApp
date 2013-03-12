/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.activities;

import studip.app.frontend.util.AbstractFragmentActivity;
import StudIPApp.app.R;

/**
 * @author joern
 * 
 */
public class ActivitiesActivity extends AbstractFragmentActivity {

    /**
     * @param titleRes
     */
    public ActivitiesActivity(int titleRes) {
	super(R.string.Activities);
	// TODO Auto-generated constructor stub
    }

    // public ActivitiesActivity() {
    // super(new ActivitiesResponderFragment(), R.string.Activities);
    // }
    //
    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    // super.onCreate(savedInstanceState);
    // this.setContentView(R.layout.general_view);
    //
    // mItemList = new ArrayList<ArrayAdapterItem>();
    // mProgressBar = (ProgressBar) this
    // .findViewById(R.id.generalViewProgressBar);
    // mRefreshButton = (ImageButton) this
    // .findViewById(R.id.general_refresh_button);
    // mText = (TextView) this.findViewById(R.id.title);
    // mText.setText(getString(R.string.Activities));
    //
    // mRefreshButton.setOnClickListener(new OnClickListener() {
    //
    // public void onClick(View v) {
    // refreshArrayList();
    // }
    //
    // });
    //
    // FragmentManager fragmentManager = getSupportFragmentManager();
    // FragmentTransaction fragmentTransaction = fragmentManager
    // .beginTransaction();
    //
    // // Add List Fragment
    // GeneralListFragment glf = new GeneralListFragment();
    // glf.itemList = this.mItemList;
    // fragmentTransaction.add(R.id.placeholder, glf);
    //
    // ActivitiesResponderFragment responderFragment =
    // (ActivitiesResponderFragment) fragmentManager
    // .findFragmentByTag(getString(R.string.Activities));
    // if (responderFragment == null) {
    // responderFragment = mResponderFragment;
    // fragmentTransaction.add(responderFragment,
    // getString(R.string.Activities));
    // }
    // fragmentTransaction.commit();
    //
    // refreshArrayList();
    // }
    //
    // @Override
    // public void refreshArrayList() {
    // }

}
