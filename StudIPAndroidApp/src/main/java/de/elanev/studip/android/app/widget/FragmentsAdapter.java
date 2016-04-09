package de.elanev.studip.android.app.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class FragmentsAdapter extends FragmentPagerAdapter {

  private ArrayList<Tab> mTabs = new ArrayList<Tab>();

  public FragmentsAdapter(FragmentManager fm) {
    super(fm);
  }

  public FragmentsAdapter(FragmentManager fm, ArrayList<Tab> tabs) {
    super(fm);
    mTabs = tabs;
  }

  public void addTab(Tab tab) {
    mTabs.add(tab);
  }

  @Override public Fragment getItem(int position) {
    Tab tab = mTabs.get(position);

    try {
      Method m = tab.clss.getMethod("newInstance", Bundle.class);

      return (Fragment) m.invoke(null, tab.args);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override public int getCount() {
    return mTabs.size();
  }

  @Override public CharSequence getPageTitle(int position) {
    return mTabs.get(position).title;
  }


  public static final class Tab {
    CharSequence title;
    Class<?> clss;
    Bundle args;

    public Tab(CharSequence title, Class clss, Bundle args) {
      this.title = title;
      this.clss = clss;
      this.args = args;
    }
  }
}