package com.example.nikhiljoshi.enlighten.ui.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.data.EnlightenContract;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment;
import com.twitter.sdk.android.Twitter;

import static com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment.*;
import static com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment.*;

/**
 * Created by nikhiljoshi on 6/13/16.
 */
public class PackActivity extends AppCompatActivity {

    public static final String PARENT_PACK_ID_TAG = "parent_pack_id";

    private long parentPackId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // if parentPackId = -1 then go to MainActivity, else open PackActivity with parentPackId

        setContentView(R.layout.activity_pack);

        // get the pack_id
        final long currentPackId = getIntent().getLongExtra(PACK_ID_TAG, -1L);
        parentPackId = getIntent().getLongExtra(PARENT_PACK_ID_TAG, -1L);
        final ActivityToStartOnFriendSelection activityToStartOnFriendSelection =
                ActivityToStartOnFriendSelection.PACK_ACTIVITY;
        final FriendSource friendSource = FriendSource.DB;

        Bundle arguments = new Bundle();
        arguments.putSerializable(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG, activityToStartOnFriendSelection);
        arguments.putSerializable(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG, friendSource);
        arguments.putSerializable(PACK_ID_TAG, currentPackId);

        ChosenFriendsFragment fragment = new ChosenFriendsFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_pack_container, fragment)
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
        if (parentPackId == -1L) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PackActivity.class);
            // Since we are going to the "previous" screen, the "current pack id" becomes
            // the parent pack id
            intent.putExtra(PACK_ID_TAG, parentPackId);
            final long parentPackIdOfParentPackId = getParentPackId(parentPackId);
            intent.putExtra(PARENT_PACK_ID_TAG, parentPackIdOfParentPackId);
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
