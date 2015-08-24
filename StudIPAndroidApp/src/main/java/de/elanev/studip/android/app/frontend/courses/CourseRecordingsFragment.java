/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.courses;


import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

  private static final int RECORDING_PRESENTATION = 0;
  private static final int RECORDING_PRESENTER = 1;
  private static final int RECORDING_AUDIO = 2;
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
    final Recording recording = mAdapter.getItem(position);
    if (recording == null) {
      return;
    }

    switch (v.getId()) {
      case R.id.download_icon:
        handleDownloadIconClick(recording);
        return;
      default:
        handleStreamingClick(recording);
    }
  }

  private void handleStreamingClick(final Recording recording) {
    final ArrayList<String> dialogOptions = getDialogOptions(recording);
    if (dialogOptions.isEmpty()) {
      showErrorToast(R.string.recording_no_available);
    }
    String[] options = dialogOptions.toArray(new String[dialogOptions.size()]);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.choose_recording_type)
        .setItems(options, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            String option = dialogOptions.get(which);
            if (option.equals("Presentation")) {
              streamUrl(recording.getPresentationDownload());
            }
            if (option.equals("Presenter")) {
              streamUrl(recording.getPresenterDownload());
            }
            if (option.equals("Audio")) {
              streamUrl(recording.getPresenterDownload());
            }
          }
        })
        .setCancelable(true)
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  private void streamUrl(String url) {
    if (!TextUtils.isEmpty(url)) {
      Uri uri = Uri.parse(url);

      if (uri.getScheme().equalsIgnoreCase("http") || uri.getScheme().equalsIgnoreCase("https")) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "video/*");
        startActivity(Intent.createChooser(intent,
            getActivity().getString(R.string.recordings_chooser_title)));
      } else {
        showErrorToast(R.string.recording_no_available);
      }

    } else {
      showErrorToast(R.string.recording_no_available);
    }
  }

  private void handleDownloadIconClick(final Recording recording) {
    final ArrayList<String> dialogOptions = getDialogOptions(recording);
    if (dialogOptions.isEmpty()) {
      showErrorToast(R.string.recording_no_available);
    }

    final ArrayList<Integer> selectedItems = new ArrayList<>();
    String[] options = dialogOptions.toArray(new String[dialogOptions.size()]);

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle(R.string.choose_recording_type)
        .setMultiChoiceItems(options, null, new DialogInterface.OnMultiChoiceClickListener() {
          @Override public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
              selectedItems.add(which);
            } else if (selectedItems.contains(which)) {
              selectedItems.remove(which);
            }
          }
        })
        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            for (int item : selectedItems) {
              if (dialogOptions.get(item).equals("Presentation")) {
                downloadUrl(recording.getPresentationDownload());
              }
              if (dialogOptions.get(item).equals("Presenter")) {
                downloadUrl(recording.getPresenterDownload());
              }
              if (dialogOptions.get(item).equals("Audio")) {
                downloadUrl(recording.getPresenterDownload());
              }
            }
          }
        })
        .setCancelable(true)
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
          }
        })
        .create()
        .show();
  }

  private void showErrorToast(int errorTextRes) {
    Toast.makeText(getActivity(), errorTextRes, Toast.LENGTH_LONG).show();
  }

  private ArrayList<String> getDialogOptions(Recording recording) {
    ArrayList<String> dialogOptions = new ArrayList<>();

    if (!TextUtils.isEmpty(recording.getPresentationDownload())) {
      dialogOptions.add("Presentation");
    }
    if (!TextUtils.isEmpty(recording.getPresenterDownload())) {
      dialogOptions.add("Presenter");
    }
    if (!TextUtils.isEmpty(recording.getAudioDownload())) {
      dialogOptions.add("Audio");
    }

    return dialogOptions;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void downloadUrl(String downloadUrl) {
    if (TextUtils.isEmpty(downloadUrl)) {
      return;
    }

    Uri url = Uri.parse(downloadUrl);
    if (url.getScheme().equalsIgnoreCase("http") || url.getScheme().equalsIgnoreCase("https")) {
      String fileName = url.getLastPathSegment();

      DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
      boolean externalDownloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
          .mkdirs();

      // Query DownloadManager and check of file is already being downloaded
      boolean isDownloading = false;

      if (externalDownloadsDir) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED |
            DownloadManager.STATUS_PENDING |
            DownloadManager.STATUS_RUNNING |
            DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = downloadManager.query(query);
        int col = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
          isDownloading = (TextUtils.equals(Environment.DIRECTORY_DOWNLOADS + fileName,
              cur.getString(col)));
        }
        cur.close();
      }

      if (!isDownloading) {
        // Create the download request
        DownloadManager.Request request = new DownloadManager.Request(url).setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE) // Only mobile and wifi allowed
            .setAllowedOverRoaming(false)                   // Disallow roaming downloading
            .setTitle(fileName);                       // Title of this download
        if (externalDownloadsDir) {
          request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        }

        // Allowing the scanning by MediaScanner
        request.allowScanningByMediaScanner();
        // Set download visibility
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        try {
          downloadManager.enqueue(request);
        } catch (IllegalArgumentException e) {
          if (getActivity() != null) {
            Toast.makeText(getActivity(),
                R.string.error_downloadmanager_disabled,
                Toast.LENGTH_LONG).show();
          }
        }
      }
    }
  }

  private static class RecordingsAdapter extends
      RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

    private List<Recording> mData;
    private ReactiveListFragment.ListItemClicks mFragmentClickListener;
    private Context mContext;
    private ISO8601DateFormat mDateFormat = new ISO8601DateFormat();

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
      private final ImageView mDownloadIconImageView;

      public ViewHolder(View itemView, ViewHolder.ViewHolderClicks clickListener) {
        super(itemView);
        mListener = clickListener;
        mPreviewImageView = (ImageView) itemView.findViewById(R.id.preview_image);
        mTitleTextView = (TextView) itemView.findViewById(R.id.title);
        mDateTextView = (TextView) itemView.findViewById(R.id.date);
        mDurationTextView = (TextView) itemView.findViewById(R.id.duration);
        mDownloadIconImageView = (ImageView) itemView.findViewById(R.id.download_icon);
        mDownloadIconImageView.setOnClickListener(this);

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
