/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.courses;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Document;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import retrofit.RetrofitError;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

/**
 * Created by joern on 03.02.14
 */
public class CourseDocumentsFragment extends Fragment {

  static final String TAG = CourseDocumentsFragment.class.getSimpleName();
  /**
   * Broadcast receiver listening for completion of a download
   */
  protected final BroadcastReceiver mDownloadManagerReceiver = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {
      DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
      String action = intent.getAction();

      if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

        long receivedID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L);

        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(receivedID);

        Cursor cursor = mgr.query(query);
        int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int reasonIndex = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

        if (cursor.moveToFirst()) {
          queryStatus(cursor.getInt(statusIndex), cursor.getInt(reasonIndex));
        }

        cursor.close();
      }
    }

  };

  public CourseDocumentsFragment() {}

  public static CourseDocumentsFragment newInstance(Bundle arguments) {
    CourseDocumentsFragment fragment = new CourseDocumentsFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    // registering the broadcast receiver for completed downloads
    context.registerReceiver(mDownloadManagerReceiver,
        new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    final FrameLayout wrapperLayout = new FrameLayout(getActivity());
    wrapperLayout.setLayoutParams(
        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT));
    wrapperLayout.setId(R.id.document_list_wrapper);

    return wrapperLayout;
  }

  @Override public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    DocumentsListFragment frag = DocumentsListFragment.newInstance(getArguments());
    getChildFragmentManager().beginTransaction()
        .replace(R.id.document_list_wrapper, frag)
        .commit();

  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putAll(getArguments());
    super.onSaveInstanceState(outState);
  }

  @Override public void onDetach() {
    super.onDetach();
    // unregister the broadcast receiver to save resources
    getActivity().unregisterReceiver(mDownloadManagerReceiver);
  }

  private void queryStatus(int queryStatus, int queryReason) {

    switch (queryStatus) {
      case DownloadManager.STATUS_FAILED:
        showToastMessage(getDownloadFailedReason(queryReason));
        break;

      case DownloadManager.STATUS_SUCCESSFUL:
        showToastMessage(R.string.download_completed);
        try {
          // Show the download activity
          startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } catch (ActivityNotFoundException e) {
          // No download manager installed or active, let the user handle the downloads.
        }

        break;

      default:
        showToastMessage(R.string.unknown_error);
        break;
    }
  }

  private void showToastMessage(int stringRes) {
    if (isAdded()) Toast.makeText(getActivity(), stringRes, Toast.LENGTH_SHORT)
        .show();
  }

  private static int getDownloadFailedReason(int queryReason) {
    switch (queryReason) {
      case DownloadManager.ERROR_CANNOT_RESUME:
        return R.string.error_cannot_resume;

      case DownloadManager.ERROR_DEVICE_NOT_FOUND:
        return R.string.error_device_not_found;

      case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
        return R.string.error_file_already_exists;

      case DownloadManager.ERROR_FILE_ERROR:
        return R.string.error_file_error;

      case DownloadManager.ERROR_HTTP_DATA_ERROR:
        return R.string.error_http_data_error;

      case DownloadManager.ERROR_INSUFFICIENT_SPACE:
        return R.string.error_insufficient_space;

      case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
        return R.string.error_to_many_redirects;

      case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
        return R.string.error_unhandled_error_code;

      default:
        return R.string.unknown_error;
    }

  }

  public static class DocumentsListFragment extends ReactiveListFragment {

    private static final String FOLDER_NAME = "folder_name";
    private DocumentsAdapter mAdapter;
    private String mCourseId;
    private String mFolderId;
    private String mFolderName;
    private DownloadManager mDownloadManager;
    private TextView mFolderTextView;

    public DocumentsListFragment() {}

    @Override public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Bundle args = getArguments();
      if (args == null || args.isEmpty() || !args.containsKey(
          CoursesContract.Columns.Courses.COURSE_ID)) {
        throw new IllegalStateException("Arguments must not be null and must contain a course_id");
      }
      mCourseId = args.getString(CoursesContract.Columns.Courses.COURSE_ID);

      // Get folder ID and name if available
      mFolderId = getArguments().getString(DocumentsContract.Columns.DocumentFolders.FOLDER_ID);
      mFolderName = getArguments().getString(FOLDER_NAME);

      mAdapter = new DocumentsAdapter(new ArrayList<>(), getActivity(), new ListItemClicks() {
        @Override public void onListItemClicked(View caller, int position) {
          Object obj = mAdapter.getItem(position);

          if (obj == null) {
            return;
          }

          if (obj instanceof BackButtonListEntry) {
            getActivity().onBackPressed();
          } else if (obj instanceof DocumentFolder) {
            String folderName = ((DocumentFolder) obj).name;
            String folderId = ((DocumentFolder) obj).folder_id;
            Bundle args = new Bundle();
            args.putString(CoursesContract.Columns.Courses.COURSE_ID, mCourseId);
            args.putString(DocumentsContract.Columns.DocumentFolders.FOLDER_ID, folderId);
            args.putString(FOLDER_NAME, folderName);

            DocumentsListFragment frag = DocumentsListFragment.newInstance(args);
            getParentFragment().getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.document_list_wrapper, frag)
                .addToBackStack(null)
                .commit();
          } else if (obj instanceof Document) {
            downloadDocument((Document) obj);
          }
        }
      });

      mObserver = new RecyclerView.AdapterDataObserver() {
        @Override public void onChanged() {
          super.onChanged();
          mEmptyView.setText(R.string.no_documents);
          setEmptyViewVisible(mAdapter.isEmpty());
        }
      };
      mAdapter.registerAdapterDataObserver(mObserver);

    }

    /**
     * Returns a new instance of DocumentsListFragment and sets its arguments with the passed
     * bundle.
     *
     * @param arguments arguments to set to fragment
     * @return new instance of DocumentsListFragment
     */
    public static DocumentsListFragment newInstance(Bundle arguments) {
      DocumentsListFragment fragment = new DocumentsListFragment();

      fragment.setArguments(arguments);

      return fragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) private void downloadDocument(Document document) {
      String fileId = document.document_id;
      String fileName = document.filename;
      String fileDescription = document.description;
      String apiUrl = Prefs.getInstance(getActivity())
          .getServer()
          .getApiUrl();

      boolean externalDownloadsDir = Environment.getExternalStoragePublicDirectory(
          Environment.DIRECTORY_DOWNLOADS)
          .mkdirs();

      // Query DownloadManager and check of file is already being downloaded
      boolean isDownloading = false;

      if (externalDownloadsDir) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED |
            DownloadManager.STATUS_PENDING |
            DownloadManager.STATUS_RUNNING |
            DownloadManager.STATUS_SUCCESSFUL);
        Cursor cur = mDownloadManager.query(query);
        int col = cur.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
          isDownloading = (TextUtils.equals(Environment.DIRECTORY_DOWNLOADS + fileName,
              cur.getString(col)));
        }
        cur.close();
      }


      if (!isDownloading) {
        try {
          // Create the download URI
          String downloadUrl = String.format(
              getString(R.string.restip_documents_documentid_download), apiUrl, fileId);

          // Sign the download URL with the OAuth credentials and parse the URI
          Server server = Prefs.getInstance(getActivity())
              .getServer();
          String signedDownloadUrl = OAuthConnector.with(server)
              .sign(downloadUrl);
          Uri downloadUri = Uri.parse(signedDownloadUrl);


          // Create the download request
          DownloadManager.Request request = new DownloadManager.Request(
              downloadUri).setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
              | DownloadManager.Request.NETWORK_MOBILE) // Only mobile and wifi allowed
              .setAllowedOverRoaming(false)                   // Disallow roaming downloading
              .setTitle(fileName)                             // Title of this download
              .setDescription(fileDescription)               // Description of this download
              .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
          // download location and file name

          //Allowing the scanning by MediaScanner
          request.allowScanningByMediaScanner();
          request.setNotificationVisibility(
              DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

          try {
            mDownloadManager.enqueue(request);
          } catch (IllegalArgumentException e) {
            if (getActivity() != null) {
              Toast.makeText(getActivity(), R.string.error_downloadmanager_disabled,
                  Toast.LENGTH_LONG)
                  .show();
            }
          }

        } catch (OAuthMessageSignerException e) {
          e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
          e.printStackTrace();
        } catch (OAuthCommunicationException e) {
          e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
          StuffUtil.startSignInActivity(getActivity());
        }
      }
    }

    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.documents_recyclerview_list, container, false);
      mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
      mEmptyView = (TextView) v.findViewById(R.id.empty);
      mFolderTextView = (TextView) v.findViewById(R.id.folder_title);
      mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

      return v;
    }

    @Override public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);

      setTitle(getString(R.string.Documents));
      mRecyclerView.setAdapter(mAdapter);
      mDownloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

      if (!TextUtils.isEmpty(mFolderName)) {
        mFolderTextView.setText(mFolderName);
        mFolderTextView.setVisibility(View.VISIBLE);
      }
    }

    @Override protected void updateItems() {
      downloadDocumentsForFolder(mCourseId, mFolderId, mFolderName);
    }

    private void downloadDocumentsForFolder(String courseId, final String folderId,
        final String folderName) {
      setRefreshing(true);

      Observable<DocumentFolders> observable;
      if (folderId == null) {
        observable = mApiService.getCourseDocuments(courseId);
      } else {
        observable = mApiService.getCourseDocumentsFolders(courseId, folderId);
      }

      if (observable != null) {
        mCompositeSubscription.add(bind(observable).subscribeOn(Schedulers.newThread())
            .subscribe(new Subscriber<DocumentFolders>() {
              @Override public void onCompleted() {
                setRefreshing(false);
              }

              @Override public void onError(Throwable e) {
                if (e instanceof TimeoutException) {
                  Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT)
                      .show();
                } else if (e instanceof RetrofitError || e instanceof HttpException) {
                  Toast.makeText(getActivity(), R.string.sync_error_default, Toast.LENGTH_LONG)
                      .show();
                  Log.e(TAG, e.getLocalizedMessage());
                } else {
                  e.printStackTrace();
                  throw new RuntimeException("See inner exception");
                }

                setRefreshing(false);
              }

              @Override public void onNext(DocumentFolders documentFolders) {
                final List<Object> list = new ArrayList<>();
                list.addAll(documentFolders.folders);
                list.addAll(documentFolders.documents);

                mAdapter.updateData(list, folderName);
              }
            }));
      }
    }

    @Override public void onStart() {
      super.onStart();

      downloadDocumentsForFolder(mCourseId, mFolderId, mFolderName);
    }

    private static class BackButtonListEntry {}

    private static class DocumentsAdapter extends
        RecyclerView.Adapter<DocumentsAdapter.ViewHolder> {

      private final List<Object> mData;
      private final Context mContext;
      private ReactiveListFragment.ListItemClicks mFragmentClickListener;

      public DocumentsAdapter(final List<Object> items, Context context,
          ReactiveListFragment.ListItemClicks fragmentClickListener) {
        if (items == null) {
          throw new IllegalStateException("Data items must not be null");
        }

        mContext = context;
        mData = items;
        mFragmentClickListener = fragmentClickListener;

        setHasStableIds(true);
      }

      @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.list_item_two_text_icon, parent, false);

        return new ViewHolder(view, new ViewHolder.ViewHolderClicks() {

          @Override public void onListItemClicked(View caller, int position) {
            mFragmentClickListener.onListItemClicked(caller, position);
          }

        });
      }

      @Override public void onBindViewHolder(ViewHolder holder, int position) {

        Object entry = mData.get(position);
        if (entry == null) {
          return;
        }

        if (entry instanceof BackButtonListEntry) {
          holder.mTitleTextView.setText(R.string.back);
          holder.mIconImageView.setImageResource(R.drawable.ic_arrow_left_blue);
          holder.mSubtitleTextView.setVisibility(View.GONE);

        } else if (entry instanceof DocumentFolder) {
          DocumentFolder f = (DocumentFolder) entry;

          holder.mIconImageView.setImageResource(R.drawable.ic_folder);
          holder.mTitleTextView.setText(f.name);

          String subText = String.format(mContext.getString(R.string.last_updated),
              DateTools.getLocalizedRelativeTimeString(f.chdate));
          holder.mSubtitleTextView.setText(subText);
          holder.mSubtitleTextView.setVisibility(View.VISIBLE);

        } else {
          Document doc = (Document) entry;

          String docName = TextUtils.isEmpty(doc.name) ? doc.filename : doc.name;
          holder.mIconImageView.setImageResource(R.drawable.ic_file_generic);
          holder.mTitleTextView.setText(docName);

          String subText = String.format(mContext.getString(R.string.downloads), doc.downloads);
          subText = subText + "\t\t\t" + TextTools.readableFileSize(doc.filesize);
          holder.mSubtitleTextView.setText(subText);
          holder.mSubtitleTextView.setVisibility(View.VISIBLE);
        }

        holder.mIconImageView.setColorFilter(mContext.getResources()
            .getColor(R.color.studip_mobile_dark), PorterDuff.Mode.SRC_IN);
      }

      @Override public int getItemCount() {
        return mData.size();
      }

      public Object getItem(int position) {
        if (position == RecyclerView.NO_POSITION) {
          return null;
        }

        return mData.get(position);
      }

      public void updateData(List<Object> list, String folderName) {
        mData.clear();
        mData.addAll(list);
        this.notifyDataSetChanged();
      }

      public boolean isEmpty() {
        return mData.isEmpty();
      }

      public static class ViewHolder extends RecyclerView.ViewHolder implements
          View.OnClickListener {
        TextView mTitleTextView;
        TextView mSubtitleTextView;
        ImageView mIconImageView;
        ViewHolder.ViewHolderClicks mListener;

        public ViewHolder(View itemView, ViewHolderClicks viewHolderClicksListener) {
          super(itemView);

          mListener = viewHolderClicksListener;
          mTitleTextView = (TextView) itemView.findViewById(R.id.text1);
          mSubtitleTextView = (TextView) itemView.findViewById(R.id.text2);
          mIconImageView = (ImageView) itemView.findViewById(R.id.icon);
          itemView.setOnClickListener(this);
        }

        @Override public void onClick(View v) {
          mListener.onListItemClicked(v, getAdapterPosition());
        }

        public interface ViewHolderClicks {
          void onListItemClicked(View caller, int position);
        }
      }
    }
  }
}
