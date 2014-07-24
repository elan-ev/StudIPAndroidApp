package de.elanev.studip.android.app.frontend.profile;

import android.content.Intent;
import android.os.Bundle;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import de.elanev.studip.android.app.widget.UserDetailsActivity;



/**
 * Created by aklassen on 07.06.14.
 */
public class ProfileFragment extends ProgressListFragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        String userId = Prefs.getInstance(mContext).getUserId();

        if (userId != null) {
            Intent intent = new Intent(mContext, UserDetailsActivity.class);
            intent.putExtra(UsersContract.Columns.USER_ID, userId);
            mContext.startActivity(intent);
        }

    }
}
