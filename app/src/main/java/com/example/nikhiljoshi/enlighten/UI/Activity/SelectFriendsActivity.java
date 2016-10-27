package com.example.nikhiljoshi.enlighten.ui.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment;
import com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment;
import com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsInstructionsFragment;
import com.twitter.sdk.android.Twitter;

import static com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment.PACK_ID_TAG;
import static com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment.*;
import static com.example.nikhiljoshi.enlighten.ui.Activity.MainActivity.*;

/**
 * As soon as the activity loads, it should show the instructions screen.
 * When the user hits 'OK', this activity should replace this instructions screen
 * with a fragment that allows users to choose their friend's whose
 * tweet articles they are interested in
 */
public class SelectFriendsActivity extends AppCompatActivity implements SelectFriendsInstructionsFragment.Callback {

    private long packId;
    private long parentPackId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();

        /**
         * The next two set of extras are used in SelectFriendsFragment.
         * The first extra - used to figure out which activity to launch once you select your friends
         * The second extra - The source from where the UsersAdapter loads its friends - whether
         *      it is through the database or through the API
         */
        final ActivityToStartOnFriendSelection activityToStartEnum =
                (ActivityToStartOnFriendSelection) intent.getSerializableExtra(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG);
        final FriendSource friendSourceEnum = (FriendSource) intent.getSerializableExtra(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG);
        packId = intent.getLongExtra(ChosenFriendsFragment.PACK_ID_TAG, NO_PACK);
        parentPackId = getParentPackId(packId);

        if (activityToStartEnum != null && friendSourceEnum != null) {
            launchSelectFriendsFragment(activityToStartEnum, friendSourceEnum, packId);
        } else {
            /**
             * If this activity is loading for the first time, display the fragment that instructs the
             * user on what to do with this activity
             */
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_friends_container,
                            new SelectFriendsInstructionsFragment())
                    .commit();

        }

    }

    @Override
    public void onSelected() {
        launchSelectFriendsFragment(ActivityToStartOnFriendSelection.MAIN_ACTIVITY,
                                    FriendSource.API, NO_PACK);
    }

    private void launchSelectFriendsFragment(ActivityToStartOnFriendSelection activityToStartOnFriendSelectionEnum,
                                             FriendSource friendsSourceEnum, Long packId) {
        Bundle arguments = new Bundle();

        arguments.putSerializable(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG, activityToStartOnFriendSelectionEnum);
        arguments.putSerializable(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG, friendsSourceEnum);
        arguments.putLong(ChosenFriendsFragment.PACK_ID_TAG, packId);
        arguments.putLong(PackActivity.PARENT_PACK_ID_TAG, parentPackId);

        SelectFriendsFragment fragment = new SelectFriendsFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.select_friends_container, fragment)
                .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                goToPreviousPage();
//                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goToPreviousPage();
//        finish();

        super.onBackPressed();
    }

    private void goToPreviousPage() {
        if (packId == -1L) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PackActivity.class);
            // Since we are going to the "previous" screen, the "current pack id" becomes
            // the parent pack id
            intent.putExtra(PACK_ID_TAG, packId);
            intent.putExtra(PackActivity.PARENT_PACK_ID_TAG, parentPackId);
            startActivity(intent);
        }
    }

    private long getParentPackId(long packId) {
        final long currentUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        final Uri uri = EnlightenContract.PackEntry.buildPackUriWithPackId(currentUserId, packId);
        final Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()) {
            return -1;
        } else {
            final int parentPackidIndex = cursor.getColumnIndex(EnlightenContract.PackEntry.COLUMN_PACK_PARENT_ID);
            // If the parent pack id is null, then we know that the parent pack id must be -1
            if (cursor.isNull(parentPackidIndex)) {
                cursor.close();
                return -1L;
            } else {
                final long parentPackId = cursor.getLong(parentPackidIndex);
                cursor.close();
                return parentPackId;
            }
        }

    }
}
