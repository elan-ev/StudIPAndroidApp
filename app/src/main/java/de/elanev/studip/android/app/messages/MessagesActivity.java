/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.data.datamodel.Postbox;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.FragmentsAdapter;

/**
 * @author joern
 */
public class MessagesActivity extends MainActivity {
  public static final String FOLDER_ID = "folder-id";
  public static final String BOX_TYPE = "box-type";
  private static final String TAG = MessagesActivity.class.getSimpleName();
  @Bind(R.id.sliding_tabs) TabLayout mTabLayout;
  @Bind(R.id.pager) ViewPager mViewPager;
  @Bind(R.id.fab) FloatingActionButton mFab;

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_messages;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    setTitle(R.string.Messages);

    ButterKnife.bind(this);
    FragmentsAdapter pagerAdapter = new FragmentsAdapter(getSupportFragmentManager(), getTabs());
    mViewPager.setAdapter(pagerAdapter);
    mTabLayout.setupWithViewPager(mViewPager);
    mFab.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startComposeActivity();
      }
    });

    // Workaround for missing title when Toolbar is wrapped in CollapsibleToolbarLayouts
    // FIXME: Look for a better, backward compatible fix for this
    TextView text = new TextView(this);
    text.setText(R.string.Messages);
    text.setTextAppearance(this,
        android.R.style.TextAppearance_Material_Widget_ActionBar_Title_Inverse);
    mToolbar.addView(text);
    // ---

    overridePendingTransition(0, 0);
  }

  private ArrayList<FragmentsAdapter.Tab> getTabs() {
    Postbox postbox = Prefs.getInstance(this)
        .getPostbox();
    ArrayList<FragmentsAdapter.Tab> tabs = new ArrayList<>();

    for (String folderName : postbox.inbox.folders) {
      Bundle extras = new Bundle();
      extras.putString(BOX_TYPE, StudIPConstants.STUDIP_MESSAGES_INBOX_IDENTIFIER);
      extras.putString(FOLDER_ID, folderName);
      tabs.add(new FragmentsAdapter.Tab(folderName, MessagesListFragment.class, extras));
    }
    for (String folderName : postbox.outbox.folders) {
      Bundle extras = new Bundle();
      extras.putString(BOX_TYPE, StudIPConstants.STUDIP_MESSAGES_OUTBOX_IDENTIFIER);
      extras.putString(FOLDER_ID, folderName);
      tabs.add(new FragmentsAdapter.Tab(folderName, MessagesListFragment.class, extras));
    }

    return tabs;
  }

  private void startComposeActivity() {
    Intent intent = new Intent(MessagesActivity.this, MessageComposeActivity.class);
    startActivity(intent);
  }
}
