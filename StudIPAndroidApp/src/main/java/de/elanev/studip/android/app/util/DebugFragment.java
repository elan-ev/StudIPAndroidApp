/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author joern
 */
public class DebugFragment extends Fragment {
  private static final String TAG = DebugFragment.class.getSimpleName();

  @Override public void onAttach(Context context) {
    super.onAttach(context);

    Log.d(TAG, "onAttach CALLED");
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Log.d(TAG, "onCreate CALLED");
    if (savedInstanceState != null) {
      Log.d(TAG, "savedInstanceState IS NOT NULL");
    } else {
      Log.d(TAG, "savedInstanceState IS NULL");
    }
  }

  @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    Log.d(TAG, "onCreateView CALLED");
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Log.d(TAG, "onViewCreated CALLED");
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    Log.d(TAG, "onActivityCreated CALLED");
  }

  @Override public void onStart() {
    super.onStart();

    Log.d(TAG, "onStart CALLED");
  }

  @Override public void onResume() {
    super.onResume();

    Log.d(TAG, "onResume CALLED");
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    Log.d(TAG, "onSaveInstanceState CALLED");
  }

  @Override public void onPause() {
    super.onPause();

    Log.d(TAG, "onPause CALLED");
  }

  @Override public void onStop() {
    super.onStop();

    Log.d(TAG, "onStop CALLED");
  }

  @Override public void onDestroyView() {
    super.onDestroyView();

    Log.d(TAG, "onDestroyView CALLED");
  }

  @Override public void onDestroy() {
    super.onDestroy();

    Log.d(TAG, "onDestroy CALLED");
  }

  @Override public void onDetach() {
    super.onDetach();

    Log.d(TAG, "onDetach CALLED");
  }
}
