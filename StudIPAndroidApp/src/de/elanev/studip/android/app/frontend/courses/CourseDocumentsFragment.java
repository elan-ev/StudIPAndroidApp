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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.FileUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * @author joern
 */
public class CourseDocumentsFragment extends ProgressSherlockListFragment implements
        LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
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
    private DocumentsAdapter mDocumentsAdapter;
    private long mDownloadReference = -1L;
    private DownloadManager mDownloadManager;
    private Bundle mArgs;
    private String mApiUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        String cid = mArgs.getString(
                CoursesContract.Columns.Courses.COURSE_ID);

        // Get reference to the download manager
        mDownloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        mApiUrl = Prefs.getInstance(mContext).getServer().getApiUrl();

        mDocumentsAdapter = new DocumentsAdapter(mContext);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyMessage(R.string.no_documents);

        mListView.setOnItemClickListener(this);

        mListView.setAdapter(mDocumentsAdapter);
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
        // Do not allow downloading because unfixed bugs in pre ics download manager cause
        // downloads from Stud.IP API to fail
        if (ApiUtils.isOverApi14()) {
            String fileId = fileInfo.getString(FILE_ID);
            String fileName = fileInfo.getString(FILE_NAME);
            String fileDescription = fileInfo.getString(FILE_DESCRIPTION);

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
                            .format(getString(R.string.restip_documents_documentid_download),
                                    mApiUrl,
                                    fileId);


                    downloadUrl = downloadUrl.replace("https://", "http://");

                    // Sign the download URL with the OAuth credentials and parse the URI
                    Server server = Prefs.getInstance(mContext).getServer();
                    String signedDownloadUrl = OAuthConnector.with(server).sign(downloadUrl);
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
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request
                            .VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    mDownloadReference = mDownloadManager.enqueue(request);

                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    StuffUtil.startSignInActivity(mContext);
                }
            }
        } else {

            // Android Version to old, show an warning dialog instead
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment prev = fm.findFragmentByTag("warning_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            new WarningDialog(R.string.not_supported,
                    R.string.version_not_supported_message).show(ft, "warning_dialog");
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
                // Show the download activity
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

        List<SectionedCursorAdapter.Section> sections = new ArrayList<SectionedCursorAdapter.Section>();
        cursor.moveToFirst();
        String prevFolder = null;
        String currFolder = null;
        while (!cursor.isAfterLast()) {
            currFolder = cursor
                    .getString(cursor
                            .getColumnIndex(DocumentsContract.Columns.DocumentFolders.FOLDER_NAME));
            if (!TextUtils.equals(currFolder, prevFolder)) {
                sections.add(new SectionedCursorAdapter.Section(cursor
                        .getPosition(), currFolder));
            }

            prevFolder = currFolder;

            cursor.moveToNext();
        }

        mDocumentsAdapter.setSections(sections);
        mDocumentsAdapter.swapCursor(cursor);

        setLoadingViewVisible(false);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mDocumentsAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
// If a valid row was selected, create a download
        if (position != ListView.INVALID_POSITION) {

            // get the file information from the cursor at the selected position
            Cursor c = (Cursor) mListView.getAdapter().getItem(position);
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

    /**
     * DialogFramgnet for showing a warning to the user
     */
    public static class WarningDialog extends DialogFragment {

        private int title;
        private int message;

        /**
         * Returns a new WarningDialog instance containing the supplied title and message to show.
         *
         * @param title   the resource id for the dialog title
         * @param message the resource id for the dialog message
         */
        public WarningDialog(int title, int message) {
            this.title = title;
            this.message = message;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            }
                    )
                    .create();
        }
    }

    /*
     * ListAdapter for document entries in the database
     */
    private class DocumentsAdapter extends SectionedCursorAdapter {

        /**
         * @param context
         */
        public DocumentsAdapter(Context context) {
            super(context);

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
            int fileResource = FileUtils.getResourceForMimeType(fileMimeType);
            fileIconImageView.setImageResource(fileResource);

            view.setTag(fileId);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_two_text_icon, parent, false);
        }

    }

}
