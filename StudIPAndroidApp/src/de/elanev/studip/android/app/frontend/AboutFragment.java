/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;

/**
 * Created by joern on 07.10.13.
 */
public class AboutFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null)
            return null;
        View v = inflater.inflate(R.layout.fragment_about_app, null);

        TextView versionTextView = (TextView) v.findViewById(R.id.version_text);
        versionTextView.setText(String.format(getString(R.string.version_and_copyright),
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.about_studip_mobile);
    }
}
