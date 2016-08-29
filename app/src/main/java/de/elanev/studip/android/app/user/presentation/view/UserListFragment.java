/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.view;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Toast;

import java.util.Locale;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroups;
import de.elanev.studip.android.app.data.datamodel.Contacts;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.db.ContactsContract;
import de.elanev.studip.android.app.data.db.UsersContract;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author joern
 */
public abstract class UserListFragment extends ProgressListFragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
  private static final String TAG = UserListFragment.class.getCanonicalName();
  private static ContentResolver mResolver;

  protected UserListFragment() {}
  @Inject StudIpLegacyApiService apiService;
  /*
   * (non-Javadoc)
   *
   * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((AbstractStudIPApplication)getActivity().getApplication()).getAppComponent().inject(this);

    mResolver = mContext.getContentResolver();
  }

  /*
   * (non-Javadoc)
   *
   * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
   */
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setEmptyMessage(R.string.no_users);
    setHasOptionsMenu(true);
    registerForContextMenu(mListView);
  }

  /**
   * Creating floating context menu
   */
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getActivity().getMenuInflater();
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    Cursor listItemCursor = (Cursor) mListView.getAdapter().getItem(info.position);

    final String[] projection = new String[]{
        ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
        ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
        ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID
    };
    final String contactId = listItemCursor.getString(listItemCursor.getColumnIndex(UsersContract.Columns.USER_ID));

    CursorLoader loader = new CursorLoader(getActivity(),
        ContactsContract.CONTENT_URI_CONTACTS.buildUpon().appendPath(contactId).build(),
        projection,
        null,
        null,
        ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");

    final Cursor c = loader.loadInBackground();

    if (c.getCount() <= 0) {
      inflater.inflate(R.menu.user_add_context_menu, menu);
    } else {
      inflater.inflate(R.menu.user_context_menu, menu);
      c.moveToFirst();
      while (!c.isAfterLast()) {
        String currGroupName = c.getString(c.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
        if (TextUtils.equals(currGroupName, getString(R.string.studip_app_contacts_favorites))) {
          menu.removeItem(R.id.add_to_favorites);
          menu.findItem(R.id.remove_from_favorites).setVisible(true);
        }

        c.moveToNext();
      }

    }
    c.close();
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    // Workaround: Check if tab is visible, else pass call to the next tab
    if (getUserVisibleHint()) {
      AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      int itemId = item.getItemId();

      // Get userInfo from cursor
      Cursor c = (Cursor) mListView.getAdapter().getItem(info.position);
      String userId = c.getString(c.getColumnIndex(UsersContract.Columns.USER_ID));

      int userIntId = c.getInt(c.getColumnIndex(UsersContract.Columns._ID));

      switch (itemId) {
        // add to or remove user from favorites
        case R.id.add_to_favorites: {
          String favGroupId = null;
          Cursor favCursor1 = mContext.getContentResolver()
              .query(ContactsContract.CONTENT_URI_CONTACT_GROUPS,
                  new String[]{ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID},
                  ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME + "= ?",
                  new String[]{
                      mContext.getString(R.string.studip_app_contacts_favorites)
                  },
                  ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS
              );
          if (favCursor1.getCount() > 0) {
            favCursor1.moveToFirst();
            favGroupId = favCursor1.getString(favCursor1.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
          }
          favCursor1.close();

          addUserToGroup(userId, favGroupId, mContext, mSyncHelper);
          return true;
        }
        case R.id.remove_from_favorites: {
          String favGroupId = null;
          int favGroupIntId = 0;
          Cursor favCursor2 = mContext.getContentResolver()
              .query(ContactsContract.CONTENT_URI_CONTACT_GROUPS,
                  new String[]{
                      ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
                      ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID
                  },
                  ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME + "= ?",
                  new String[]{
                      mContext.getString(R.string.studip_app_contacts_favorites)
                  },
                  ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS
              );
          if (favCursor2.getCount() > 0) {
            favCursor2.moveToFirst();
            favGroupId = favCursor2.getString(favCursor2.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
            favGroupIntId = favCursor2.getInt(favCursor2.getColumnIndex(ContactsContract.Columns.ContactGroups._ID));
          }
          favCursor2.close();

          deleteUserFromGroup(userId, favGroupId, favGroupIntId, userIntId, mContext, mSyncHelper);
          return true;
        }

        // add to or remove from contacts
        case R.id.add_to_contacts: {
          addUserToContacts(userId, mContext);
          return true;
        }
        case R.id.remove_from_contacts: {
          deleteUserFromContacts(userId, mContext, mSyncHelper);
          return true;
        }
        case R.id.manage_groups: {
//          Bundle args = new Bundle();
//          args.putString(ContactsContract.Columns.Contacts.USER_ID, userId);
//          args.putInt(ContactsContract.Columns.Contacts._ID, userIntId);
//          ContactGroupsDialogFragment frag = (ContactGroupsDialogFragment) ContactGroupsDialogFragment
//              .instantiate(mContext, ContactGroupsDialogFragment.class.getName());
//          frag.setArguments(args);
//          getFragmentManager().beginTransaction()
//              .add(frag, ContactGroupsDialogFragment.class.getName())
//              .commit();
          return true;
        }

        default:
          return super.onContextItemSelected(item);
      }
    } else {
      return false;
    }

  }

  private void addUserToGroup(final String userId, final String groupId,
      final Context context, final SyncHelper syncHelper) {

    // TODO: Replace with injections
    Server server = Prefs.getInstance(context)
        .getServer(getContext());

    apiService.addUserToContactsGroup(groupId, userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<ContactGroups>() {
          @Override public void onCompleted() {
            syncHelper.performContactsSync(null);
          }

          @Override public void onError(Throwable e) {
            if (e != null) {
              Timber.e(e, e.getMessage());
              //              Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }

          @Override public void onNext(ContactGroups contactGroups) {
            //            try {
            //              mResolver.applyBatch(AbstractContract.CONTENT_AUTHORITY,
            //                  new ContactGroupHandler(contactGroups.group).parse());

            //            } catch (RemoteException | OperationApplicationException e) {
            //              e.printStackTrace();
            //            }

            Toast.makeText(context, R.string.successfully_added, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private void deleteUserFromGroup(final String userId, final String groupId,
      final int groupIntId, final int userIntId, final Context context, final SyncHelper syncHelper) {
    apiService.deleteUserFromContactsGroup(groupId, userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Void>() {
          @Override public void onCompleted() {
            syncHelper.performContactsSync(null);
          }

          @Override public void onError(Throwable e) {
            if (e != null) {
              Timber.e(e, e.getMessage());

              //Toast.makeText(context, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
          }

          @Override public void onNext(Void aVoid) {
            context.getContentResolver()
                .delete(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS.buildUpon()
                    .appendPath(String.format(Locale.getDefault(), "%d", groupIntId))
                    .build(), ContactsContract.Columns.Contacts.USER_ID + "= ?", new String[]{
                    String.format(Locale.getDefault(), "%d", userIntId)
                });

            Toast.makeText(context, R.string.successfully_deleted, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private void addUserToContacts(final String userId, final Context context) {
    apiService.addUserToContacts(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Contacts>() {
          @Override public void onCompleted() {
            mSyncHelper.performContactsSync(null);
          }

          @Override public void onError(Throwable e) {
            if (e != null) Timber.e(e, e.getMessage());
            //Toast.makeText(context, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
          }

          @Override public void onNext(Contacts contacts) {
            Toast.makeText(context, R.string.successfully_added, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  private  void deleteUserFromContacts(final String userId, final Context context,
      final SyncHelper syncHelper) {
    apiService.deleteUserFromContacts(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Void>() {
          @Override public void onCompleted() {
            syncHelper.performContactsSync(null);
          }

          @Override public void onError(Throwable e) {
            if (e != null) {
              Timber.e(e, e.getMessage());
//              Toast.makeText(context, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT)
//                  .show();
            }
          }

          @Override public void onNext(Void aVoid) {
            context.getContentResolver()
                .delete(ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS,
                    ContactsContract.Columns.ContactGroupMembers.USER_ID + "= ?",
                    new String[]{"'" + userId + "'"});

            context.getContentResolver()
                .delete(ContactsContract.CONTENT_URI_CONTACTS.buildUpon()
                    .appendPath(userId)
                    .build(), null, null);

            Toast.makeText(context, R.string.successfully_deleted, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

  protected interface UsersQuery {
    String[] projection = {
        UsersContract.Qualified.USERS_ID,
        UsersContract.Qualified.USERS_USER_ID,
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
        ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
        ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID
    };
  }

//  public static class ContactGroupsDialogFragment extends DialogFragment {
//
//    private String mUserId;
//    private int mIntUserId;
//    @Inject SyncHelper mSyncHelper;
//
//    public ContactGroupsDialogFragment() {}
//
//    @Override public void onCreate(Bundle savedInstanceState) {
//      super.onCreate(savedInstanceState);
//
//      ((AbstractStudIPApplication)getActivity().getApplication()).getAppComponent().inject(this);
//
//      mUserId = getArguments().getString(ContactsContract.Columns.Contacts.USER_ID);
//      mIntUserId = getArguments().getInt(ContactsContract.Columns.Contacts._ID);
//    }
//
//    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
//      final String[] projection = new String[]{
//          ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_NAME,
//          ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_GROUP_ID,
//          ContactsContract.Qualified.ContactGroups.CONTACT_GROUPS_ID
//      };
//
//      final HashMap<String, Pair<Pair<String, Integer>, Boolean>> multimap = new HashMap<String, Pair<Pair<String, Integer>, Boolean>>();
//      final ArrayList<Pair<String, Integer>> allGroupIds = new ArrayList<Pair<String, Integer>>();
//      final ArrayList<String> allGroupNames = new ArrayList<String>();
//
//      // load all groups
//      CursorLoader allGroupsloader = new CursorLoader(getActivity(),
//          ContactsContract.CONTENT_URI_CONTACT_GROUPS,
//          projection,
//          ContactsContract.Columns.ContactGroups.GROUP_ID + " != ?",
//          new String[]{getString(R.string.restip_contacts_groups_unassigned_id)},
//          ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");
//
//      final Cursor cursorAllGroups = allGroupsloader.loadInBackground();
//      cursorAllGroups.moveToFirst();
//      while (!cursorAllGroups.isAfterLast()) {
//        String groupName = cursorAllGroups.getString(cursorAllGroups.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_NAME));
//        String groupId = cursorAllGroups.getString(cursorAllGroups.getColumnIndex(ContactsContract.Columns.ContactGroups.GROUP_ID));
//        int groupIntId = cursorAllGroups.getInt(cursorAllGroups.getColumnIndex(ContactsContract.Columns.ContactGroups._ID));
//        multimap.put(groupName,
//            new Pair<Pair<String, Integer>, Boolean>(new Pair<String, Integer>(groupId, groupIntId),
//                false)
//        );
//        cursorAllGroups.moveToNext();
//      }
//      cursorAllGroups.close();
//
//      // load only the users groups
//      CursorLoader selectedGroupsLoader = new CursorLoader(getActivity(),
//          ContactsContract.CONTENT_URI_CONTACTS.buildUpon().appendPath(mUserId).build(),
//          projection,
//          null,
//          null,
//          ContactsContract.Columns.ContactGroups.GROUP_NAME + " ASC");
//      final Cursor userGroupsCursor = selectedGroupsLoader.loadInBackground();
//      userGroupsCursor.moveToFirst();
//
//      // set selected if group is users group
//      while (!userGroupsCursor.isAfterLast()) {
//        String userGroup = userGroupsCursor.getString(cursorAllGroups.getColumnIndex(
//            ContactsContract.Columns.ContactGroups.GROUP_NAME));
//        if (multimap.containsKey(userGroup)) {
//          Pair<String, Integer> groupIdPair = multimap.get(userGroup).first;
//          multimap.put(userGroup, new Pair<Pair<String, Integer>, Boolean>(groupIdPair, true));
//        }
//        userGroupsCursor.moveToNext();
//      }
//      userGroupsCursor.close();
//
//      Iterator<Entry<String, Pair<Pair<String, Integer>, Boolean>>> it = multimap.entrySet()
//          .iterator();
//      int row = 0;
//      boolean[] primitivValuesArr = new boolean[multimap.size()];
//      while (it.hasNext()) {
//        Map.Entry<String, Pair<Pair<String, Integer>, Boolean>> pairs = it
//            .next();
//
//        allGroupNames.add(pairs.getKey());
//        allGroupIds.add(pairs.getValue().first);
//        primitivValuesArr[row] = pairs.getValue().second;
//
//        row++;
//        it.remove(); // avoids a ConcurrentModificationException
//      }
//
//      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//      builder.setTitle(R.string.manage_groups);
//      builder.setMultiChoiceItems(allGroupNames.toArray(new CharSequence[allGroupNames.size()]),
//          primitivValuesArr,
//          new OnMultiChoiceClickListener() {
//
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//              if (isChecked) {
//                addUserToGroup(mUserId, allGroupIds.get(which).first, getActivity(),
//                    getSyncHelper());
//              } else {
//                deleteUserFromGroup(mUserId, allGroupIds.get(which).first,
//                    allGroupIds.get(which).second, mIntUserId, getActivity(), getSyncHelper());
//              }
//
//            }
//          }
//      );
//      builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//            /*
//             * (non-Javadoc)
//             *
//             * @see
//             * android.content.DialogInterface.OnClickListener#onClick
//             * (android.content.DialogInterface, int)
//             */
//            public void onClick(DialogInterface dialog, int which) {
//              getDialog().cancel();
//            }
//          }
//      );
//
//      return builder.create();
//    }
//
//    public SyncHelper getSyncHelper() {
//      return mSyncHelper;
//    }
//  }
}
