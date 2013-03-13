/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.db.DocumentsRepository;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.DocumentsResponderFragment;

/**
 * @author joern
 * 
 */
public class CourseDocumentsFragment extends SherlockListFragment {
	public static final String TAG = CourseDocumentsFragment.class
			.getSimpleName();
	private String mCid;
	public SimpleCursorAdapter mAdapter;
	public Cursor mCursor;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCid = getArguments().getString("cid");

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		DocumentsResponderFragment responderFragment = (DocumentsResponderFragment) fm
				.findFragmentByTag("documentsResponder");
		if (responderFragment == null) {
			responderFragment = new DocumentsResponderFragment();
			responderFragment.setFragment(this);
			Bundle args = new Bundle();
			args.putString("cid", mCid);
			responderFragment.setArguments(args);
			ft.add(responderFragment, "documentsResponder");
		}
		ft.commit();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		View detailView = inflater.inflate(R.layout.general_list_fragment,
				container, false);

		return detailView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();
		getNewCursor();
		setListAdapter(getNewListAdapter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
		mCursor.close();
	}

	public SimpleCursorAdapter getNewListAdapter() {
		return new SimpleCursorAdapter(getSherlockActivity(),
				R.layout.file_item, mCursor, new String[] {
						DocumentsContract.Columns.DOCUMENT_NAME,
						DocumentsContract.Columns.DOCUMENT_FILESIZE },
				new int[] { R.id.file_name, R.id.file_size });
	}

	public void getNewCursor() {
		mCursor = DocumentsRepository.getInstance(getSherlockActivity())
				.getDocumentsCursorForCourse(mCid);
	}

}
