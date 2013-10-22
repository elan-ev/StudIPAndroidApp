/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * @author joern
 */
public class CourseDocumentsFragment extends ProgressSherlockListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = CourseDocumentsFragment.class
            .getSimpleName();
    private static final String FILE_ID = CourseDocumentsFragment.class.getName() + ".fileId";
    private static final String FILE_NAME = CourseDocumentsFragment.class.getName() + ".fileName";
    private static final String FILE_DESCRIPTION = CourseDocumentsFragment.class.getName() +
            ".fileDescription";
    /**
     * Content Observer listening for changes in the database
     */
    protected final ContentObserver mObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (getActivity() == null) {
                return;
            }

            Loader<Cursor> loader = getLoaderManager().getLoader(0);
            if (loader != null) {
                loader.forceLoad();
            }
        }
    };
    /**
     * Broadcast receiver listening for completion of a download
     */
    protected final BroadcastReceiver mDownloadManagerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            DownloadManager mgr = (DownloadManager) context.getSystemService(Context
                    .DOWNLOAD_SERVICE);
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
    private SimpleSectionedListAdapter mAdapter;
    private DocumentsAdapter mDocumentsAdapter;
    private long mDownloadReference = -1L;
    private DownloadManager mDownloadManager;
    private Bundle mArgs;
    private VolleyOAuthConsumer mConsumer;
    private String mApiUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        String cid = mArgs.getString(
                CoursesContract.Columns.Courses.COURSE_ID);

        Prefs prefs = Prefs.getInstance(mContext);
        if (prefs.isAppAuthorized()) {
            Server server = prefs.getServer();
            mConsumer = new VolleyOAuthConsumer(server.getConsumerKey(),
                    server.getConsumerSecret());
            mConsumer.setTokenWithSecret(prefs.getAccessToken(),
                    prefs.getAccessTokenSecret());
        }
        // Get reference to the download manager
        mDownloadManager = (DownloadManager) getActivity()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        mApiUrl = prefs.getServer().getApiUrl();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyMessage(R.string.no_documents);
        mDocumentsAdapter = new DocumentsAdapter(mContext);
        mAdapter = new SimpleSectionedListAdapter(mContext, R.layout.list_item_header,
                mDocumentsAdapter);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // registering the content observer
        activity.getContentResolver().registerContentObserver(
                DocumentsContract.CONTENT_URI, true, mObserver);
        // registering the broadcast receiver for completed downloads
        activity.registerReceiver(mDownloadManagerReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // unregister the content observer to save resources
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
        // unregister the broadcast receiver to save resources
        getActivity().unregisterReceiver(mDownloadManagerReceiver);
    }

    @SuppressLint("NewApi")
    private void startDocumentDownload(Bundle fileInfo) {
        String fileId = fileInfo.getString(FILE_ID);
        String fileName = fileInfo.getString(FILE_NAME);
        String fileDescription = fileInfo.getString(FILE_DESCRIPTION);

//        if (!ApiUtils.isOverApi14())
//            fileName = fileName.replace(" ", "_");

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
                isDownloading = (Environment.DIRECTORY_DOWNLOADS + fileName == cur.getString(col));
            }
            cur.close();
        }

        if (!isDownloading) {
            try {
                // Create the download URI
                String downloadUrl = String
                        .format(getString(R.string.restip_documents_documentid_download), mApiUrl,
                                fileId);


                // Since HTTPS is only supported on ICS and higher, we replace HTTPS with HTTP
                if (!ApiUtils.isOverApi14())
                    downloadUrl = downloadUrl.replace("https://", "http://");

                // Sign the download URL with the OAuth credentials and parse the URI
                String signedDownloadUrl = downloadUrl;
                Uri downloadUri = Uri.parse(signedDownloadUrl);

                // Create the download request
                Request request = new Request(Uri.parse(signedDownloadUrl))
                        .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                                DownloadManager.Request.NETWORK_MOBILE) // Only mobile and wifi allowed
                        .setAllowedOverRoaming(false)                   // Disallow roaming downloading
                        .setTitle(fileName)                             // Title of this download
                        .setDescription(fileDescription);               // Description of this download
                if (externalDownloadsDir)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                            fileName);
                // download location and file name


                //Allowing the scanning by MediaScanner
                if (ApiUtils.isOverApi11()) {
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request
                            .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                }

                mConsumer.sign(request);
                // Enqueue the download request and save download reference
                mDownloadReference = mDownloadManager.enqueue(request);

            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }
        }

    }

    private void queryStatus(int queryStatus, int queryReason) {

        switch (queryStatus) {
            case DownloadManager.STATUS_FAILED:
                showToastMessage(getDownloadFailedReason(queryReason));
                break;

//            case DownloadManager.STATUS_PAUSED:
//                showToastMessage("Download paused!");
//                break;
//
//            case DownloadManager.STATUS_PENDING:
//                showToastMessage("Download pending!");
//                break;
//
//            case DownloadManager.STATUS_RUNNING:
//                showToastMessage("Download in progress!");
//                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                showToastMessage(R.string.download_completed);
                //Show the download activity if the API is below Honeycomb
                if (!ApiUtils.isOverApi11())
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                break;

            default:
                showToastMessage(R.string.unknown_error);
                break;
        }

    }

    private int getDownloadFailedReason(int queryReason) {
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

    private void showToastMessage(int stringRes) {
        if (isAdded())
            Toast.makeText(mContext, stringRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // If a valid row was selected, create a download
        if (position != ListView.INVALID_POSITION) {

            // get the file information from the cursor at the selected position
            Cursor c = (Cursor) getListAdapter().getItem(position);
            String fileId = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_ID));
            String fileName = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_FILENAME));
            String name = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_NAME));
            String fileDesc = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_DESCRIPTION));

            Bundle fileInfo = new Bundle();
            fileInfo.putString(FILE_ID, fileId);
            fileInfo.putString(FILE_NAME, fileName);
            fileInfo.putString(FILE_DESCRIPTION, fileDesc);

            startDocumentDownload(fileInfo);
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        setLoadingViewVisible(true);
        return new CursorLoader(
                mContext,
                DocumentsContract.CONTENT_URI
                        .buildUpon()
                        .appendPath(
                                mArgs.getString(CoursesContract.Columns.Courses.COURSE_ID))
                        .build(),
                DocumentsQuery.projection,
                null,
                null,
                DocumentsContract.Qualified.DocumentFolders.DOCUMENTS_FOLDERS_FOLDER_NAME
                        + " ASC, "
                        + DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_CHDATE
                        + " DESC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (getActivity() == null) {
            return;
        }

        List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
        cursor.moveToFirst();
        String prevFolder = null;
        String currFolder = null;
        while (!cursor.isAfterLast()) {
            currFolder = cursor
                    .getString(cursor
                            .getColumnIndex(DocumentsContract.Columns.DocumentFolders.FOLDER_NAME));
            if (!TextUtils.equals(currFolder, prevFolder)) {
                sections.add(new SimpleSectionedListAdapter.Section(cursor
                        .getPosition(), currFolder));
            }

            prevFolder = currFolder;

            cursor.moveToNext();
        }

        mDocumentsAdapter.changeCursor(cursor);

        SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
                .size()];
        mAdapter.setSections(sections.toArray(dummy));

        setLoadingViewVisible(false);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mDocumentsAdapter.swapCursor(null);
    }

    /*
     * Fields to be selected from the database
     */
    private interface DocumentsQuery {
        public String[] projection = {
                DocumentsContract.Qualified.Documents.DOCUMENTS_ID,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_NAME,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_FILESIZE,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_FILENAME,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_DESCRIPTION,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_MIME_TYPE,
                DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_ID,
                DocumentsContract.Qualified.DocumentFolders.DOCUMENTS_FOLDERS_FOLDER_NAME};
    }

    /*
     * ListAdapter for document entries in the database
     */

    private class DocumentsAdapter extends CursorAdapter {

        /**
         * @param context
         */
        public DocumentsAdapter(Context context) {
            super(context, null, false);

        }

        @Override
        public void bindView(View view, Context context, final Cursor c) {
            String fileName = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_NAME));
            String fileMimeType = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_MIME_TYPE));
            String fileSize = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_FILESIZE));
            String fileId = c
                    .getString(c
                            .getColumnIndex(DocumentsContract.Columns.Documents.DOCUMENT_ID));

            TextView fileNameTextView = (TextView) view
                    .findViewById(R.id.text1);
            TextView fileSizeTextView = (TextView) view
                    .findViewById(R.id.text2);
            ImageView fileIconImageView = (ImageView) view
                    .findViewById(R.id.icon);

            fileNameTextView.setText(fileName);
            fileSizeTextView.setText(fileSize);

            // Set correct icon for specific MIMEType
            if (TextUtils.equals(fileMimeType, MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension("zip"))
                    || TextUtils.equals(fileMimeType, MimeTypeMap
                    .getSingleton().getMimeTypeFromExtension("rar"))
                    || TextUtils.equals(fileMimeType, MimeTypeMap
                    .getSingleton().getMimeTypeFromExtension("7z"))) {
                fileIconImageView.setImageResource(R.drawable.ic_file_archive);
            } else {
                fileIconImageView.setImageResource(R.drawable.ic_file_text);
            }

            view.setTag(fileId);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_two_text_icon, parent, false);
        }

    }

}
