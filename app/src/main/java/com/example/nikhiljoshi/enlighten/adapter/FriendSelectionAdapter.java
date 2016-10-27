package com.example.nikhiljoshi.enlighten.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.Utility;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.pojo.Friend;
import com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;
import java.util.List;

import static com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract.*;

/**
 * Adapter that holds information for friends that need to be selected -- either through DB
 * or through Twitter API
 */
public class FriendSelectionAdapter extends RecyclerView.Adapter<FriendSelectionAdapter.UsersViewHolder> {

    private static final String LOG_TAG = FriendSelectionAdapter.class.getSimpleName();
    private final Context context;
    private final SelectFriendsFragment.FriendSource friendSourceEnum;

    private List<Friend> friends;
    private List<Friend> selectedFriends = new ArrayList<>();

    private static final String[] FRIENDS_COLUMNS = {
            FriendEntry.COLUMN_PROFILE_NAME
    };

    private static final int COL_PROFILE_NAME = 0;

    public FriendSelectionAdapter(Context context, SelectFriendsFragment.FriendSource friendSourceEnum) {
        this.context = context;
        this.friendSourceEnum = friendSourceEnum;
    }

    /**
     * This is the view holder for the FriendsAdapter
     */
    public class UsersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView profilePicture;
        public TextView profileName;
        public Friend user;

        public UsersViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            View imageNameFriendItemView = itemView;
            profilePicture = (ImageView) imageNameFriendItemView.findViewById(R.id.profile_picture);
            profileName = (TextView) imageNameFriendItemView.findViewById(R.id.profile_name);
        }

        /**
         * We bind the user to the view. In here, we also handle the view settings to that
         * of a selected user or a non selected user. We first check if the user present in this view
         * has been selected by the AppUser to be a part of friends list that they want to get tweets from
         */
        public void bindView(Friend user) {
            View rootView = profilePicture.getRootView();
            if (FriendSelectionAdapter.this.isSelectedUser(user)) {
                setViewSettingsToSelectedUser(rootView);
            } else {
                setViewSettingsToNonSelectedUser(rootView);
            }
            Picasso.with(profilePicture.getContext()).load(Utility.improveProfileImagePixel(user.profilePictureUrl)).fit()
                    .into(profilePicture);


            Log.i(LOG_TAG, " Username: " + user.userName + " Profile URL:" + user.profilePictureUrl);
            profileName.setText(user.profileName);
            this.user = user;
        }


        /**
         * When the AppUser clicks on a twitter user that she wants to include in their
         * reading list, we do that... and when they click on the user again, they get deselected
         */
        @Override
        public void onClick(View itemView) {
            if (!FriendSelectionAdapter.this.isSelectedUser(user)) {
                setViewSettingsToSelectedUser(itemView);
                FriendSelectionAdapter.this.addToSelectedFriends(user);
            } else {
                setViewSettingsToNonSelectedUser(itemView);
                FriendSelectionAdapter.this.removeFromSelectedFriends(user);
            }

        }

        private void setViewSettingsToSelectedUser(View itemView) {
            itemView.setBackgroundResource(R.color.cardview_dark_background);
        }

        private void setViewSettingsToNonSelectedUser(View itemView) {
            itemView.setBackgroundResource(R.color.default_background);
        }
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View friendImageNameItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.friend_image_username_item, parent, false
        );

        UsersViewHolder viewHolder = new UsersViewHolder(friendImageNameItemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(UsersViewHolder holder, int position) {
        if (friends.size() > position) {
            Friend user = friends.get(position);
            holder.bindView(user);
        }
    }

    @Override
    public int getItemCount() {
        return friends == null ? 0 : friends.size();
    }

    public void addUsersFromApi(List<User> extraUsers) {
        if (friends == null) {
            friends = new ArrayList<Friend>();
        }
        // Since we get all our friends from API route, we need to filter out
        // friends who have alread been added into our database
        extraUsers = filterOutSavedFriends(extraUsers);
        final List<Friend> extraFriends = convertToFriendObjects(extraUsers);
        this.friends.addAll(0, extraFriends);
        notifyDataSetChanged();
    }

    public void loadAllFriendsFromDb(Long packId) {

        if (friends == null) {
            friends = new ArrayList<Friend>();
        }

        List<Friend> friendsFromDb = new ArrayList<>();
        final ContentResolver contentResolver = context.getContentResolver();

        long currentSessionUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        Uri uriWithCurrentUserId = EnlightenContract.FriendEntry.buildFriendUriWithCurrentUserId(currentSessionUserId);

        // Get all the friends from DB who are in other packs
        String selectionQuery = FriendEntry.COLUMN_PACK_KEY + " != ? AND " + FriendEntry.COLUMN_PACK_KEY + " IS NOT NULL";
        String[] selectionArgs = new String[]{packId + ""};

        Cursor cursor = contentResolver.query(uriWithCurrentUserId, null, selectionQuery, selectionArgs, null);

        if (!cursor.moveToFirst()) {
            Log.e(LOG_TAG, "The user hasn't chosen any friends! Weird... he should have chosen some.");
        } else {

            do {
                friendsFromDb.add(EnlightenContract.FriendEntry.convertToFriend(cursor));
            } while (cursor.moveToNext());

        }

        this.friends.addAll(0, friendsFromDb);

        // Now get all the friends who are in base folder -- these are the ones that
        // the user now wants to probably select into their pack. Hence show it up
        // all the way at the top

        friendsFromDb.clear();
        selectionQuery = FriendEntry.COLUMN_PACK_KEY + " IS NULL";
        cursor = contentResolver.query(uriWithCurrentUserId, null, selectionQuery, null, null);

        if (!cursor.moveToFirst()) {
            Log.e(LOG_TAG, "The user hasn't chosen any friends! Weird... he should have chosen some.");
        } else {

            do {
                friendsFromDb.add(EnlightenContract.FriendEntry.convertToFriend(cursor));
            } while (cursor.moveToNext());

        }

        this.friends.addAll(0, friendsFromDb);

        cursor.close();
        notifyDataSetChanged();

    }

    private List<Friend> convertToFriendObjects(List<User> extraUsers) {
        List<Friend> extraFriends = new ArrayList<>();

        for (User user : extraUsers) {
            //(String userName, String profileName, String profilePictureUrl, long userId)
            extraFriends.add(new Friend(user.screenName, user.name, user.profileImageUrl, user.id));
        }

        return extraFriends;
    }

    private List<User> filterOutSavedFriends(List<User> extraUsers) {
        // get all the users that have already been added
        final List<String> savedFriends = getChosenFriends();
        List<User> unsavedFriends = new ArrayList<>();
        for (User user : extraUsers) {
            if (!savedFriends.contains(user.name)) {
                unsavedFriends.add(user);
            }
        }
        return unsavedFriends;
    }

    public void swapUsers(List<Friend> newUsers) {
        if (friends == null) {
            friends = new ArrayList<Friend>();

        }
        friends.clear();
        friends.addAll(newUsers);
        notifyDataSetChanged();
    }

    /**
     * Get the list of friends that have already been selected
     */
    private List<String> getChosenFriends() {

        List<String> chosenFriendsProfileNames = new ArrayList<>();

        final Uri uriWithCurrentUserId =
                EnlightenContract.FriendEntry.buildFriendUriWithCurrentUserId(Twitter.getSessionManager().getActiveSession().getUserId());
        final Cursor cursor = context.getContentResolver().query(uriWithCurrentUserId, FRIENDS_COLUMNS, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                chosenFriendsProfileNames.add(cursor.getString(COL_PROFILE_NAME));
            } while (cursor.moveToNext());

        }

        cursor.close();
        return chosenFriendsProfileNames;
    }

    public List<Friend> getSelectedFriends() {
        return selectedFriends;
    }

    private void addToSelectedFriends(Friend user) {
        selectedFriends.add(user);
    }

    private void removeFromSelectedFriends(Friend user) {
        selectedFriends.remove(user);
    }

    private boolean isSelectedUser(Friend friend) {
        return selectedFriends.contains(friend) ? true : false;
    }
}
