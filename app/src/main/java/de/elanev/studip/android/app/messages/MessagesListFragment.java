/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.data.datamodel.Message;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import de.elanev.studip.android.app.widget.SimpleRecyclerViewAdapter;
import de.elanev.studip.android.app.widget.SimpleSectionedRecyclerViewAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessagesListFragment extends ReactiveListFragment {
  public static final String TAG = MessagesListFragment.class.getSimpleName();
  public MessagesAdapter mAdapter;
  public SimpleSectionedRecyclerViewAdapter mSectionedAdapter;
  private int mOffset = 0;
  private boolean mLoading;
  private int mPreviousTotal;
  private int mVisibleThreshold;
  private String mFolderId;
  private String mBoxType;

  public MessagesListFragment() {}

  public static MessagesListFragment newInstance(Bundle args) {
    MessagesListFragment fragment = new MessagesListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    Bundle arguments = getArguments();
    if (arguments == null) {
      return;
    }

    mFolderId = arguments.getString(MessagesActivity.FOLDER_ID);
    mBoxType = arguments.getString(MessagesActivity.BOX_TYPE);
  }


  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    if (savedInstanceState != null) {
      mFolderId = savedInstanceState.getString(MessagesActivity.FOLDER_ID);
    }

    mEmptyView.setText(R.string.no_messages);
    mAdapter = new MessagesAdapter(new ArrayList<Pair<Message, User>>(),
        new SimpleRecyclerViewAdapter.ViewHolder.ViewHolderClicks() {

          @Override public void onListItemClicked(View v, int position) {
            Pair<Message, User> messageUserPair = mAdapter.getItem(position);
            Message message = messageUserPair.first;
            User user = messageUserPair.second;
            startMessageDetailActivity(message, user);
          }
        }, getContext()) {};

    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        mOffset = 0;
        updateItems();
      }
    });
    mSectionedAdapter = new SimpleSectionedRecyclerViewAdapter(mAdapter);

    mRecyclerView.setAdapter(mSectionedAdapter);

    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        int visibleItemCount = mRecyclerView.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();


        if (mLoading) {

          if (totalItemCount > mPreviousTotal) {
            mLoading = false;
            mPreviousTotal = totalItemCount;
          }
        }
        if (!mLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem
            + mVisibleThreshold)) {

          mOffset = totalItemCount;
          setRefreshing(true);
          updateItems();
          mLoading = true;
        }
      }
    });
  }

  @Override protected void updateItems() {
    Observable<Pair<Message, User>> messageObservable;
    if (mBoxType.equals(StudIPConstants.STUDIP_MESSAGES_INBOX_IDENTIFIER)) {
      messageObservable = mApiService.getInboxMessages(mFolderId, mOffset, 10);
    } else {
      messageObservable = mApiService.getOutboxMessages(mFolderId, mOffset, 10);
    }

    final ArrayList<Pair<Message, User>> entries = new ArrayList<>();
    mCompositeSubscription.add(bind(messageObservable).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Pair<Message, User>>() {
          @Override public void onCompleted() {
            if (mOffset == 0) {
              // We are refreshing, so remove all previous entries.
              mAdapter.clear();
            }

            mAdapter.addAll(entries);
            setRefreshing(false);
          }

          @Override public void onError(Throwable e) {
          }

          @Override public void onNext(Pair<Message, User> messageUserPair) {
            entries.add(messageUserPair);
          }
        }));
  }

  private void startMessageDetailActivity(Message message, User sender) {
    if (message.unread != 0) markMessageAsRead(message.messageId);

    Bundle extras = new Bundle();
    extras.putSerializable(MessageDetailActivity.MESSAGE, message);
    extras.putSerializable(MessageDetailActivity.SENDER_INFO, sender);

    Intent intent = new Intent(getContext(), MessageDetailActivity.class);
    intent.putExtras(extras);

    startActivity(intent);
  }

  /**
   * @param messageId
   */
  private void markMessageAsRead(final String messageId) {
    mCompositeSubscription.add(
        bind(mApiService.setMessageRead(messageId)).subscribe(new Subscriber<Void>() {
          @Override public void onCompleted() {
            //SUCCESS
            updateItems();
          }

          @Override public void onError(Throwable e) {
            //ERROR
          }

          @Override public void onNext(Void aVoid) {
            //NOTHING
          }
        }));
  }

  @Override public void onStart() {
    super.onStart();
    updateItems();
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putString(MessagesActivity.FOLDER_ID, mFolderId);
    outState.putString(MessagesActivity.BOX_TYPE, mBoxType);

    super.onSaveInstanceState(outState);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.messages_list_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (isAdded()) {
      switch (item.getItemId()) {
        case R.id.compose_icon:

          Intent intent = new Intent(getContext(), MessageComposeActivity.class);
          startActivity(intent);
          break;

        default:
          return super.onOptionsItemSelected(item);
      }
    }
    return true;

  }

  static class MessagesAdapter extends
      SimpleRecyclerViewAdapter<Pair<Message, User>, MessagesAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    private final Context mContext;

    public MessagesAdapter(ArrayList<Pair<Message, User>> data,
        SimpleRecyclerViewAdapter.ViewHolder.ViewHolderClicks listener, Context context) {

      mData = data;
      mListener = listener;
      mInflater = LayoutInflater.from(context);
      mContext = context;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = mInflater.inflate(R.layout.list_item_message, parent, false);

      return new ViewHolder(v, mListener);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
      Pair<Message, User> messageUserPair = getItem(position);
      Message message = messageUserPair.first;
      User user = messageUserPair.second;

      holder.mSubject.setText(message.subject);
      holder.mUserName.setText(user.getFullName());
      Picasso.with(mContext)
          .load(user.avatarNormal)
          .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(holder.userImage);
    }

    public static class ViewHolder extends SimpleRecyclerViewAdapter.ViewHolder {
      @Bind(R.id.message_subject) TextView mSubject;
      @Bind(R.id.message_sender) TextView mUserName;
      @Bind(R.id.user_image) CircleImageView userImage;

      public ViewHolder(View itemView, ViewHolderClicks listener) {
        super(itemView, listener);

        ButterKnife.bind(this, itemView);
      }
    }
  }
}
