/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.courses;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Recording;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.Transformations.GradientTransformation;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import retrofit.RetrofitError;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Fragment that loads the list of recordings for a specific course and displays it.
 *
 * @author Jörn
 */
public class CourseRecordingsFragment extends ReactiveListFragment implements
    ReactiveListFragment.ListItemClicks {
  public static final String TAG = CourseRecordingsFragment.class.getSimpleName();

  private RecordingsAdapter mAdapter;
  private String mCourseId;
  private RecyclerView.AdapterDataObserver mObserver;

  public CourseRecordingsFragment() {}

  public static CourseRecordingsFragment newInstance(Bundle arguments) {
    CourseRecordingsFragment fragment = new CourseRecordingsFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mAdapter = new RecordingsAdapter(null, this, getActivity());
    mCourseId = getArguments().getString(CoursesContract.Columns.Courses.COURSE_ID);
    mObserver = new RecyclerView.AdapterDataObserver() {

      @Override public void onChanged() {
        super.onChanged();

        toggleEmptyView(mAdapter.isEmpty());
      }
    };

    mAdapter.registerAdapterDataObserver(mObserver);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mRecyclerView.setAdapter(mAdapter);
    removeDividerItemDecoratior();
    mEmptyView.setText(R.string.no_recordings);

    if (!mRecreated) {
      updateItems();
    }
  }

  @Override protected void updateItems() {
    // Return immediately when no course id is set
    if (TextUtils.isEmpty(mCourseId)) {
      return;
    }

    mCompositeSubscription.add(bind(mApiService.getRecordings(mCourseId)).subscribeOn(Schedulers.newThread())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ArrayList<Recording>>() {
          @Override public void onCompleted() {
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.backgroud_grey_light));
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

          @Override public void onNext(ArrayList<Recording> recordings) {
            if (recordings == null) {
              return;
            }
            mAdapter.setData(recordings);
          }
        }));
  }

  @Override public void onListItemClicked(View v, int position) {
    Recording recording = mAdapter.getItem(position);
    if (recording == null) {
      return;
    }

    String url = recording.getPresentationDownload();
    if (!TextUtils.isEmpty(url)) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.parse(url), "video/*");
      startActivity(Intent.createChooser(intent,
          getActivity().getString(R.string.recordings_chooser_title)));
    } else {
      Toast.makeText(getActivity(), R.string.recording_no_available, Toast.LENGTH_LONG).show();
    }
  }

  private static class RecordingsAdapter extends
      RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<Recording> mData;
    private ReactiveListFragment.ListItemClicks mFragmentClickListener;
    private Context mContext;
    private ISO8601DateFormat mDateFormat = new ISO8601DateFormat();
    SimpleDateFormat mDateParser = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'",
        Locale.getDefault());


    public RecordingsAdapter(List<Recording> data,
        ReactiveListFragment.ListItemClicks callback,
        Context context) {
      if (mData == null) {
        mData = new ArrayList<>();
      } else {
        mData = data;
      }
      mFragmentClickListener = callback;
      mContext = context;
    }

    public void setData(ArrayList<Recording> data) {
      mData.clear();
      mData.addAll(data);
      notifyDataSetChanged();
    }

    @Override public RecordingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
      View v = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.list_item_recording, parent, false);

      return new ViewHolder(v, new ViewHolder.ViewHolderClicks() {
        @Override public void onListItemClicked(View caller, int position) {
          mFragmentClickListener.onListItemClicked(caller, position);
        }
      });
    }

    @Override public void onBindViewHolder(RecordingsAdapter.ViewHolder holder, int position) {
      Recording item = getItem(position);

      if (item == null) {
        return;
      }
      Picasso.with(mContext)
          .load(item.getPreview())
          .fit()
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .transform(new GradientTransformation())
          .into(holder.mPreviewImageView);

      holder.mTitleTextView.setText(item.getTitle());

      try {
        Date startDate = mDateFormat.parse(item.getStart());
        String date = TextTools.getLocalizedAuthorAndDateString(item.getAuthor(),
            startDate.getTime(),
            mContext);
        holder.mDateTextView.setText(date);
      } catch (ParseException e) {
        e.printStackTrace();
      }

      String durationString = String.format("%02d:%02d:%02d",
          TimeUnit.MILLISECONDS.toHours(item.getDuration()),
          TimeUnit.MILLISECONDS.toMinutes(item.getDuration())
              - TimeUnit.HOURS.toMinutes((TimeUnit.MILLISECONDS).toHours(item.getDuration())),
          TimeUnit.MILLISECONDS.toSeconds(item.getDuration())
              - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(item.getDuration())));
      holder.mDurationTextView.setText(durationString);
    }

    private Recording getItem(int position) {
      if (position == RecyclerView.NO_POSITION) {
        return null;
      }
      return mData.get(position);
    }

    @Override public int getItemCount() {
      if (mData == null || mData.size() < 0) {
        return 0;
      }
      return mData.size();
    }

    public boolean isEmpty() {
      return mData == null || mData.isEmpty();
    }


    public static final class ViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
      public final View mContainerView;
      public final ImageView mPreviewImageView;
      public final TextView mTitleTextView;
      public final TextView mDateTextView;
      public final TextView mDurationTextView;
      public final ViewHolder.ViewHolderClicks mListener;

      public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
        super(itemView);
        mListener = clickListener;
        mPreviewImageView = (ImageView) itemView.findViewById(R.id.preview_image);
        mTitleTextView = (TextView) itemView.findViewById(R.id.title);
        mDateTextView = (TextView) itemView.findViewById(R.id.date);
        mDurationTextView = (TextView) itemView.findViewById(R.id.duration);

        mContainerView = itemView.findViewById(R.id.card_view);
        mContainerView.setOnClickListener(this);
      }

      @Override public void onClick(View v) {
        mListener.onListItemClicked(v, getLayoutPosition());
      }

      public interface ViewHolderClicks {
        void onListItemClicked(View caller, int position);
      }
    }


  }
}
