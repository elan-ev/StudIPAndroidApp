/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumEntry;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.ReactiveFragment;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * @author joern
 */
public class ForumEntryComposeFragment extends ReactiveFragment {
  public static final String ENTRY_TYPE = ForumEntryComposeFragment.class.getName() + ".entry_type";
  private static final String TAG = ForumEntryComposeFragment.class.getSimpleName();
  private static String sEntryId;
  private static EntryType sEntryType;
  private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
  private Bundle mArgs;
  private EditText mSubjectEditText;
  private EditText mContentEditText;
  private boolean mSendButtonVisible = true;
  private MainActivity.OnShowProgressBarListener mCallback;

  public static ForumEntryComposeFragment newInstance(Bundle args) {
    ForumEntryComposeFragment fragment = new ForumEntryComposeFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);

    mArgs = getArguments();

    if (mArgs != null) {
      sEntryType = (EntryType) mArgs.getSerializable(ENTRY_TYPE);
      sEntryId = mArgs.getString(ForumEntry.ID);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_forum_area_create, container, false);
    mSubjectEditText = (EditText) v.findViewById(R.id.forum_area_subject);
    mContentEditText = (EditText) v.findViewById(R.id.forum_area_content);
    if (sEntryType == EntryType.REPLY_ENTRY) {
      mSubjectEditText.setVisibility(View.GONE);
    }

    return v;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    try {
      mCallback = (MainActivity.OnShowProgressBarListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
          + " must implement OnShowProgressBarListener");
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.forum_entry_compose_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public void onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);
    menu.findItem(R.id.create_new_area).setVisible(mSendButtonVisible);

    mCallback.onShowProgressBar(!mSendButtonVisible);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (isAdded()) {
      switch (item.getItemId()) {
        case android.R.id.home:
          getActivity().onBackPressed();
          return true;
        case R.id.create_new_area:
          createNewEntry();
          return true;
        default:
          return super.onOptionsItemSelected(item);
      }
    }

    return true;
  }

  private void createNewEntry() {
    setViewsVisible(false);
    String subject = mSubjectEditText.getText().toString();
    String content = mContentEditText.getText().toString();

    Server server = Prefs.getInstance(getActivity()).getServer();

    StudIpLegacyApiService legacyApiService = new StudIpLegacyApiService(server, getActivity());
    mCompositeSubscription.add(bind(legacyApiService.createForumEntry(sEntryId,
        subject,
        content)).subscribe(new Subscriber<ForumArea>() {
      @Override public void onCompleted() {
        Toast.makeText(getActivity(), R.string.successfully_added, Toast.LENGTH_LONG).show();
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
      }

      @Override public void onError(Throwable e) {
        if (e instanceof TimeoutException) {
          Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT).show();
        } else if (e instanceof HttpException) {
          Toast.makeText(getActivity(), "HTTP exception", Toast.LENGTH_LONG)
              .show();
          Log.e(TAG, e.getLocalizedMessage());
        } else {
          e.printStackTrace();
          throw new RuntimeException("See inner exception");
        }
        setViewsVisible(true);
      }

      @Override public void onNext(ForumArea forumArea) {
      }
    }));
  }

  private void setViewsVisible(boolean state) {
    mSubjectEditText.setEnabled(state);
    mContentEditText.setEnabled(state);
    mSendButtonVisible = state;
    getActivity().supportInvalidateOptionsMenu();
  }

  public enum EntryType {NEW_ENTRY, REPLY_ENTRY}
}
