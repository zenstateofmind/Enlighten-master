package com.example.nikhiljoshi.enlighten.ui.Fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.FriendSelectionAdapter;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.network.MyTwitterApi;
import com.example.nikhiljoshi.enlighten.pojo.Friend;
import com.example.nikhiljoshi.enlighten.ui.Activity.MainActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.PackActivity;
import com.twitter.sdk.android.Twitter;

import java.util.List;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class SelectFriendsFragment extends Fragment {

    public static final String ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG = "activity_to_start_on_selection";
    public static final String FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG = "chose_from_db_or_api";

    private static final String LOG_TAG = SelectFriendsFragment.class.getSimpleName();

    private FriendSelectionAdapter mFriendSelectionAdapter;
    private RecyclerView mRecyclerView;
    private Class mLaunchClass;
    private FriendSource friendSourceEnum;
    private Long packId;
    private Long parentPackId;

    public enum FriendSource {
        DB, API
    }

    public enum ActivityToStartOnFriendSelection {
        MAIN_ACTIVITY(MainActivity.class),
        PACK_ACTIVITY(PackActivity.class);

        public final Class launchClass;

        ActivityToStartOnFriendSelection(Class startClass) {
            this.launchClass = startClass;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.fragment_select_friends, container, false);

        final Bundle arguments = getArguments();
        mLaunchClass = ((ActivityToStartOnFriendSelection) arguments.getSerializable(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG)).launchClass;
        friendSourceEnum = (FriendSource) arguments.getSerializable(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG);
        packId = arguments.getLong(ChosenFriendsFragment.PACK_ID_TAG);
        parentPackId = arguments.getLong(PackActivity.PARENT_PACK_ID_TAG);

        mFriendSelectionAdapter = new FriendSelectionAdapter(getContext(), friendSourceEnum);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.select_friends_recycler_view);
        mRecyclerView.setAdapter(mFriendSelectionAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        if(friendSourceEnum == FriendSource.DB) {
            mFriendSelectionAdapter.loadAllFriendsFromDb(packId);
        } else if (friendSourceEnum == FriendSource.API) {
            MyTwitterApi api = new MyTwitterApi(getActivity().getApplicationContext());
            api.getFriendsListTake2(mFriendSelectionAdapter);
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.select_friends_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select_friends: {
                List<Friend> selectedFriends = mFriendSelectionAdapter.getSelectedFriends();
                if (selectedFriends.size() < 1) {
                    Toast.makeText(getContext(), R.string.please_pick_friends, Toast.LENGTH_LONG).show();
                } else {
                    saveFriendsInfoInDb(selectedFriends);
                    Intent intent = new Intent(getContext(), mLaunchClass);
                    if (mLaunchClass.getSimpleName().equals(PackActivity.class.getSimpleName())) {
                        intent.putExtra(ChosenFriendsFragment.PACK_ID_TAG, packId);
                        intent.putExtra(PackActivity.PARENT_PACK_ID_TAG, parentPackId);
                    }
                    getActivity().finish();
                    startActivity(intent);
                }

                return true;
            } default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void saveFriendsInfoInDb(List<Friend> users) {
        for (Friend user: users) {
            if (friendSourceEnum == FriendSource.API) {
                insertFriendInDb(user);
            } else if (friendSourceEnum == FriendSource.DB) {
                updateFriendInDb(user);
            }

        }

        ////// Remove the next set of code... just to ensure that info was added to db /////
        long currentSessionUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        Uri uriWithCurrentUserId = EnlightenContract.FriendEntry.buildFriendUriWithCurrentUserId(currentSessionUserId);
        Cursor cursor = getContext().getContentResolver().query(uriWithCurrentUserId, null, null, null, null);

        int numFriendsAdded = 0;
        if (!cursor.moveToFirst()) {
            Log.e(LOG_TAG, "The user hasn't chosen any friends! Weird... he should have chosen some.");
        } else {

            do {
                numFriendsAdded++;
            } while (cursor.moveToNext());
        }

        cursor.close();

        Log.i(LOG_TAG, "Added " + numFriendsAdded + " friends to the db! Good job!");
    }

    private void updateFriendInDb(Friend user) {
        ContentValues contentValues = EnlightenContract.FriendEntry.getContentValues(user, packId);
        final long currentUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        final Uri uri = EnlightenContract.FriendEntry.buildUriWithCurrentUserIdAndFriendUserId(currentUserId, user.userId);
        final int numRowsUpdated = getContext().getContentResolver().update(uri, contentValues, null, null);
        if (numRowsUpdated == 0) {
            Log.e(LOG_TAG, "There were problems updating user: " + user.userName + " to packId: " + packId);
        }

    }

    private void insertFriendInDb(Friend user) {
        ContentValues contentValues = EnlightenContract.FriendEntry.getContentValues(user, packId);
        Uri insertingUri = EnlightenContract.FriendEntry.CONTENT_URI;
        Uri insertedUri = getContext().getContentResolver().insert(insertingUri, contentValues);
        long inserted_row_id = ContentUris.parseId(insertedUri);
        if (inserted_row_id == -1) {
            Log.e(LOG_TAG, "Could not insert info about user: " + user.profileName);
        }
    }
}
