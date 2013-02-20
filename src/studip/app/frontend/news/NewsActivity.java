/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package studip.app.frontend.news;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import StudIPApp.app.R;
import studip.app.backend.datamodel.News;
import studip.app.backend.datamodel.NewsItem;
import studip.app.backend.datamodel.User;
import studip.app.backend.db.NewsRepository;
import studip.app.backend.db.UsersRepository;
import studip.app.backend.net.services.syncservice.activitys.NewsResponderFragment;
import studip.app.frontend.util.AbstractFragmentActivity;
import studip.app.frontend.util.ArrayAdapterItem;
import studip.app.frontend.util.GeneralListFragment;

/**
 * @author joern
 * 
 */
public class NewsActivity extends
	AbstractFragmentActivity<NewsResponderFragment> {

    public NewsActivity() {
	super(new NewsResponderFragment());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.general_view);
	mItemList = new ArrayList<ArrayAdapterItem>();
	mProgressBar = (ProgressBar) this
		.findViewById(R.id.generalViewProgressBar);
	mRefreshButton = (ImageButton) this
		.findViewById(R.id.general_refresh_button);
	mText = (TextView) this.findViewById(R.id.title);
	mText.setText(getString(R.string.News));

	mRefreshButton.setOnClickListener(new OnClickListener() {

	    public void onClick(View v) {
		refreshArrayList();
	    }

	});

	((ImageButton) this.findViewById(R.id.slide_button))
		.setOnClickListener(new OnClickListener() {
		    public void onClick(View v) {
			slideButtonPressed(v);
		    }
		});

	FragmentManager fragmentManager = getSupportFragmentManager();
	FragmentTransaction fragmentTransaction = fragmentManager
		.beginTransaction();

	// Add List Fragment
	GeneralListFragment glf = new GeneralListFragment();
	glf.itemList = this.mItemList;
	fragmentTransaction.add(R.id.placeholder, glf);

	NewsResponderFragment responderFragment = (NewsResponderFragment) fragmentManager
		.findFragmentByTag(getString(R.string.News));
	if (responderFragment == null) {
	    responderFragment = mResponderFragment;
	    fragmentTransaction.add(responderFragment, getString(R.string.News));
	}
	fragmentTransaction.commit();

	refreshArrayList();
    }

    @Override
    public void refreshArrayList() {
	News news = NewsRepository.getInstance(this).getAllNews();
	UsersRepository userDB = UsersRepository.getInstance(this);
	// mAdapter.clear();
	mItemList.clear();
	if (!news.news.isEmpty()) {

	    for (NewsItem newsItem : news.news) {
		User newsAutor = userDB.getUser(newsItem.user_id);
		mItemList.add(new NewsListItem(newsItem, newsAutor));
	    }
	}

    }
}
