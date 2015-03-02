package de.elanev.studip.android.app.frontend.forums;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpException;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumAreas;
import de.elanev.studip.android.app.backend.datamodel.ForumCategory;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import retrofit.RetrofitError;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author joern
 */
public class ForumAreasListFragment extends ReactiveListFragment {
  private static final String TAG = ForumAreasListFragment.class.getSimpleName();
  private final int visibleThreshold = 5;
  private String mCategoryTitle;
  private String mCategoryId;
  private ForumAreasAdapter mAdapter;
  private RecyclerView.AdapterDataObserver mObserver;
  private int previousTotal = 0;
  private boolean loading = true;
  private int firstVisibleItem, visibleItemCount, totalItemCount;
  private int mOffset = 0;
  private String mCourseId;

  public ForumAreasListFragment() {}

  /**
   * Returns a new instance of ForumsListFragment and sets its arguments with the passed
   * bundle.
   *
   * @param arguments arguments to set to fragment
   * @return new instance of ForumsListFragment
   */
  public static ForumAreasListFragment newInstance(Bundle arguments) {
    ForumAreasListFragment fragment = new ForumAreasListFragment();
    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    if (args != null) {
      mCategoryId = args.getString(ForumCategory.ID);
      mCategoryTitle = args.getString(ForumCategory.TITLE);
      mCourseId = args.getString(Course.ID);
    }

    mAdapter = new ForumAreasAdapter(new ArrayList<ForumArea>(), new ListItemClicks() {
      @Override public void onListItemClicked(View v, int position) {
        ForumArea item = mAdapter.getItem(position);
        Bundle args = new Bundle();
        args.putString(ForumArea.ID, item.topicId);
        args.putString(ForumArea.TITLE, item.subject);
        args.putString(Course.ID, mCourseId);
        startActivity(args);
      }
    });

    mObserver = new RecyclerView.AdapterDataObserver() {

      @Override public void onChanged() {
        super.onChanged();

        mEmptyView.setText(R.string.no_entries);
        toggleEmptyView(mAdapter.isEmpty());
      }
    };

    mAdapter.registerAdapterDataObserver(mObserver);
  }

  private void startActivity(Bundle args) {
    Intent intent = new Intent();
    intent.setClass(getActivity(), ForumEntriesActivity.class);
    intent.putExtras(args);

    startActivity(intent);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setTitle(mCategoryTitle);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        visibleItemCount = mRecyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = mRecyclerView.getFirstVisiblePosition();

        if (loading) {
          if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
          }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem
            + visibleThreshold)) {

          mOffset = totalItemCount;
          setRefreshing(true);
          updateItems();
          loading = true;
        }
      }
    });

    if (!mRecreated) {
      updateItems();
    }
  }

  @Override protected void updateItems() {
    // Return immediately when no course id is set
    if (TextUtils.isEmpty(mCategoryId)) {
      return;
    }

    mCompositeSubscription.add(bind(mApiService.getForumAreas(mCategoryId, mOffset)).subscribeOn(
        Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ForumAreas>() {
          @Override public void onCompleted() {
            setRefreshing(false);
          }

          @Override public void onError(Throwable e) {
            if (e instanceof TimeoutException) {
              Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT).show();
            } else if (e instanceof RetrofitError || e instanceof HttpException) {
              Toast.makeText(getActivity(), "Retrofit error or http exception", Toast.LENGTH_LONG)
                  .show();
              Log.e(TAG, e.getLocalizedMessage());
            } else {
              e.printStackTrace();
              throw new RuntimeException("See inner exception");
            }

            setRefreshing(false);
          }

          @Override
          public void onNext(ForumAreas forumAreas) {
            if (mOffset <= 0) {
              mAdapter.clear();
            }
            mAdapter.addAll(forumAreas.forumAreas);
          }
        }));
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (isAdded()) {
      switch (item.getItemId()) {
        case android.R.id.home:
          getActivity().onBackPressed();

          return true;
        default:
          return super.onOptionsItemSelected(item);
      }
    }
    return true;
  }

  @Override public void onDetach() {
    super.onDetach();
    mAdapter.unregisterAdapterDataObserver(mObserver);
  }

}
