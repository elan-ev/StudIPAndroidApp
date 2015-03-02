package de.elanev.studip.android.app.frontend.forums;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import java.util.List;

import de.elanev.studip.android.app.R;


public class ForumEntriesActivity extends ActionBarActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getIntent().getExtras();
    if (args == null) {
      finish();
      return;
    }

    setContentView(R.layout.content_frame);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (savedInstanceState == null) {

      ForumEntriesListFragment forumEntriesListFragment = ForumEntriesListFragment.newInstance(args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame,
              forumEntriesListFragment,
              ForumEntriesListFragment.class.getName())
          .commit();

    }
  }
}
