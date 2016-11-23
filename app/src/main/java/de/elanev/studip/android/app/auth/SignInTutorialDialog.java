/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class SignInTutorialDialog extends DialogFragment {

  private PagerAdapter mPagerAdapter;
  private ViewPager mViewPager;
  private TextView mCancelTextView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void onCancel(DialogInterface dialog) {
    super.onCancel(dialog);

    dismiss();
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setCancelable(true);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.AppCompatAlertDialogStyle);
    getDialog().setCanceledOnTouchOutside(true);
    getDialog().setTitle(R.string.tutorial);
    mPagerAdapter = new TutorialSlidePagerAdapter(getChildFragmentManager());
    mViewPager.setAdapter(mPagerAdapter);
    mCancelTextView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dismiss();
      }
    });
  }

  @Override public void onStart() {
    super.onStart();

    // Set dialog size on runtime to fit correctly
    // TODO: Find a better solution
    Dialog dialog = getDialog();
    if (dialog != null) {
      dialog.getWindow()
          .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
  }

  private static class TutorialSlidePagerAdapter extends FragmentStatePagerAdapter {
    public TutorialSlidePagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override public Fragment getItem(int position) {
      Bundle args = new Bundle();
      args.putInt(TutorialSlideFragment.POSITION, position);

      return TutorialSlideFragment.newInstance(args);
    }

    @Override public int getCount() {
      return TutorialSlideFragment.NUM_PAGES;
    }

  }

  public static class TutorialSlideFragment extends Fragment {
    public static final String POSITION = "position";
    public static final int NUM_PAGES = 3;
    private static final int FIRST_PAGE = 0;
    private static final int SECOND_PAGE = 1;
    private static final int THIRD_PAGE = 2;
    int mPosition;

    public static TutorialSlideFragment newInstance(Bundle args) {
      TutorialSlideFragment fragment = new TutorialSlideFragment();
      fragment.setArguments(args);

      return fragment;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      Bundle args = getArguments();
      mPosition = args.getInt(POSITION);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater,
        @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      switch (mPosition) {
        case FIRST_PAGE:
          return inflater.inflate(R.layout.fragment_tutorial_page_first, container, false);
        case SECOND_PAGE:
          return inflater.inflate(R.layout.fragment_tutorial_page_second, container, false);
        case THIRD_PAGE:
          return inflater.inflate(R.layout.fragment_tutorial_page_third, container, false);
        default:
          return inflater.inflate(R.layout.fragment_tutorial_page_first, container, false);
      }
    }


  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.dialog_tutorial, container, false);

    mViewPager = (ViewPager) v.findViewById(R.id.tutorialPager);
    mCancelTextView = (TextView) v.findViewById(R.id.cancel_action);

    return v;
  }


}


