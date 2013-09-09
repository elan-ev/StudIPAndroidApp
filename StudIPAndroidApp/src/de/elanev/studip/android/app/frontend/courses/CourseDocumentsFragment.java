/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.DocumentsResponderFragment;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * @author joern
 * 
 */
public class CourseDocumentsFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = CourseDocumentsFragment.class
			.getSimpleName();
	private Bundle mArgs;
	// private SimpleCursorAdapter mAdapter;
	private DocumentsAdapter mAdapter;
	private Context mContext;

	// TEST
	private long mDownloadReference;
	private DownloadManager mDownloadManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mArgs = getArguments();
		mContext = getActivity();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		DocumentsResponderFragment responderFragment = (DocumentsResponderFragment) fm
				.findFragmentByTag("documentsResponder");
		if (responderFragment == null) {
			responderFragment = new DocumentsResponderFragment();
			responderFragment.setFragment(this);
			responderFragment.setArguments(mArgs);
			ft.add(responderFragment, "documentsResponder");
		}
		ft.commit();

		mAdapter = new DocumentsAdapter(mContext);
		setListAdapter(mAdapter);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getLoaderManager().initLoader(0, null, this);

		// Get reference to the download manager
		mDownloadManager = (DownloadManager) getActivity().getSystemService(
				Context.DOWNLOAD_SERVICE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.list, container, false);
		((TextView) v.findViewById(R.id.empty_message))
				.setText(R.string.no_documents);
		return v;
	}

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
			String action = intent.getAction();
			// Check of the download was completed
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				Query query = new Query();
				query.setFilterById(mDownloadReference);
				// Get reference to the download manager
				DownloadManager downloadManager = (DownloadManager) getActivity()
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Cursor c = downloadManager.query(query);
				// Check if the download was successful
				if (c.moveToFirst()) {
					int columnIndex = c
							.getColumnIndex(DownloadManager.COLUMN_STATUS);
					if (DownloadManager.STATUS_SUCCESSFUL == c
							.getInt(columnIndex)) {
						Toast.makeText(mContext, "Download fertiggestellt",
								Toast.LENGTH_SHORT).show();

						// Show the download activity
						startActivity(new Intent(
								DownloadManager.ACTION_VIEW_DOWNLOADS));
					} else {
						Toast.makeText(mContext,
								getString(R.string.something_went_wrong),
								Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#onAttach
	 * (android.app.Activity)
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockListFragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		// unregister the content observer to save resources
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
		// unregister the broadcast receiver to save resources
		getActivity().unregisterReceiver(mDownloadManagerReceiver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// If a valid row was selected, create a download
		if (position != ListView.INVALID_POSITION) {

			// get the file information from the cursor at the selected position
			Cursor c = (Cursor) getListAdapter().getItem(position);
			String fileId = c.getString(c
					.getColumnIndex(DocumentsContract.Columns.DOCUMENT_ID));
			// String fileName = c
			// .getString(c
			// .getColumnIndex(DocumentsContract.Columns.DOCUMENT_FILENAME));
			String name = c.getString(c
					.getColumnIndex(DocumentsContract.Columns.DOCUMENT_NAME));
			String fileDesc = c
					.getString(c
							.getColumnIndex(DocumentsContract.Columns.DOCUMENT_DESCRIPTION));

			try {
				// Create the download URI
				String apiUrl = Prefs.getInstance(mContext).getServer().API_URL;
				String downloadUrl = String
						.format(getString(R.string.restip_documents_documentid_download),
								apiUrl, fileId);

				Prefs prefs = Prefs.getInstance(mContext);
				VolleyOAuthConsumer consumer = null;
				if (prefs.isAppAuthorized()) {
					Server server = prefs.getServer();
					consumer = new VolleyOAuthConsumer(server.CONSUMER_KEY,
							server.CONSUMER_SECRET);
					consumer.setTokenWithSecret(prefs.getAccessToken(),
							prefs.getAccessTokenSecret());
				}
				// Sign the download URI with the OAuth credentials
				String signedDownloadUrl = consumer.sign(downloadUrl);

				// Create the download request
				Request request = new Request(Uri.parse(signedDownloadUrl));
				// Restrict the types of networks
				request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
						| DownloadManager.Request.NETWORK_MOBILE);
				// Disallow downloading over roaming
				request.setAllowedOverRoaming(false);
				// Set the title of this download
				request.setTitle(name);
				// Set a description of this download
				request.setDescription(fileDesc);
				// TODO: Check which destinations are available and set it
				// Set the local destination for the download
				// request.setDestinationInExternalPublicDir(
				// Environment.DIRECTORY_DOWNLOADS, fileName);
				// Allowing the scanning by MediaScanner
				if (ApiUtils.isOverApi11())
					request.allowScanningByMediaScanner();

				// Enqueue the download request and save download reference
				mDownloadReference = mDownloadManager.enqueue(request);

				Toast.makeText(mContext, "Download gestartet",
						Toast.LENGTH_SHORT).show();

			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(
				mContext,
				DocumentsContract.CONTENT_URI,
				DocumentsQuery.projection,
				DocumentsContract.Columns.DOCUMENT_COURSE_ID + "= ? ",
				new String[] { mArgs
						.getString(CoursesContract.Columns.Courses.COURSE_ID) },
				DocumentsContract.Columns.DOCUMENT_CHDATE + " DESC");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.swapCursor(cursor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}

	/*
	 * Fields to be selected from the database
	 */
	private interface DocumentsQuery {
		public String[] projection = { DocumentsContract.Columns._ID,
				DocumentsContract.Columns.DOCUMENT_NAME,
				DocumentsContract.Columns.DOCUMENT_FILESIZE,
				DocumentsContract.Columns.DOCUMENT_FILENAME,
				DocumentsContract.Columns.DOCUMENT_DESCRIPTION,
				DocumentsContract.Columns.DOCUMENT_MIME_TYPE,
				DocumentsContract.Columns.DOCUMENT_ID };
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#bindView(android.view.View,
		 * android.content.Context, android.database.Cursor)
		 */
		@Override
		public void bindView(View view, Context context, final Cursor c) {
			String fileName = c.getString(c
					.getColumnIndex(DocumentsContract.Columns.DOCUMENT_NAME));
			String fileMimeType = c
					.getString(c
							.getColumnIndex(DocumentsContract.Columns.DOCUMENT_MIME_TYPE));
			String fileSize = c
					.getString(c
							.getColumnIndex(DocumentsContract.Columns.DOCUMENT_FILESIZE));
			String fileId = c.getString(c
					.getColumnIndex(DocumentsContract.Columns.DOCUMENT_ID));

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#newView(android.content.Context
		 * , android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.list_item_two_text_icon, parent, false);
		}

	}

}
