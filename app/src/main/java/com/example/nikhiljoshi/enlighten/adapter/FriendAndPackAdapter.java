package com.example.nikhiljoshi.enlighten.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.Utility;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.pojo.Friend;
import com.example.nikhiljoshi.enlighten.pojo.Pack;
import com.example.nikhiljoshi.enlighten.ui.Activity.ArticleActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.PackActivity;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.Twitter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nikhiljoshi on 6/7/16.
 */
public class FriendAndPackAdapter extends RecyclerView.Adapter {

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";

    private static final int PACK_VIEW_TYPE  = 1;
    private static final int FRIEND_VIEW_TYPE = 2;
    private static final String LOG_TAG = FriendAndPackAdapter.class.getSimpleName();

    private List<Friend> friends;
    private List<Pack> packs;
    private Context mContext;

    public FriendAndPackAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            /**
             * As of now if the view type doesnt have a pack view,
             * then it has to have a friends view holder
             */
            case PACK_VIEW_TYPE : {
                View packItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.pack_item, parent, false);

                PackViewHolder packViewHolder = new PackViewHolder(packItemView);
                return packViewHolder;

            } default: {
                View friendItemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friend_image_username_item, parent, false);

                FriendViewHolder viewHolder = new FriendViewHolder(friendItemView);

                return viewHolder;
            }
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case PACK_VIEW_TYPE: {
                Pack pack = packs.get(position);
                ((PackViewHolder) holder).bindView(pack);
                return;
            } case FRIEND_VIEW_TYPE: {
                int friendPosition = position - packs.size();
                Friend friend = friends.get(friendPosition);
                ((FriendViewHolder)holder).bindView(friend);
                return;
            }
        }

    }

    @Override
    public int getItemCount() {
        int numFriends = friends == null ? 0 : friends.size();
        int numPacks = packs == null ? 0 : packs.size();
        return numFriends + numPacks;
    }

    public void addFriends(List<Friend> addedFriends) {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        friends.addAll(addedFriends);
        notifyDataSetChanged();
    }

    public void loadPacksFromDb(Long parentPackId) {
        if (packs == null) {
            packs = new ArrayList<>();
        }

        final List<Pack> allPacks = loadPacksFromDbHelper(parentPackId);
        packs.clear();
        packs.addAll(allPacks);


        notifyDataSetChanged();
    }

    private List<Pack> loadPacksFromDbHelper(Long parentPackId) {
        List<Pack> allPacks = new ArrayList<>();
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        final Uri uri = EnlightenContract.PackEntry.buildPackUriWithCurrentUserIdAndParentPackId(userId, parentPackId);
        final Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                allPacks.add(EnlightenContract.PackEntry.convertToPack(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        return allPacks;
    }

    @Override
    public int getItemViewType(int position) {

        // If both are null, then prank
        if (packs == null && friends == null) {
            return super.getItemViewType(position);
        }

        // If there are items in our pack, and our current position
        // is smaller than the number of items in the pack, its time
        // to display the pack!
        if (packs != null && packs.size() > position) {
            return PACK_VIEW_TYPE;
        }

        // If there are items in our pack and our current position is greater
        // than the pack size, we need to start showing the friends views.
        // We only need to show friend view if 'position' is smaller
        // than items in friends
        int friendsPosition = position;
        if (packs != null) {
            friendsPosition = position - packs.size();
        }

        if (friends != null && friends.size() > friendsPosition ) {
            return FRIEND_VIEW_TYPE;
        }

        return super.getItemViewType(position);
    }

    public void loadFriendsFromDb(Long packId) {

        List<Friend> friends = new ArrayList<>();
        long currentSessionUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        Uri uriWithCurrentUserId = EnlightenContract.FriendEntry.buildUriWithCurrentUserIdAndPackId(currentSessionUserId, packId);

        Cursor cursor = mContext.getContentResolver().query(uriWithCurrentUserId, null, null, null, null);

        if (!cursor.moveToFirst()) {
            Log.e(LOG_TAG, "The user hasn't chosen any friends! Weird... he should have chosen some.");
        } else {
            do {
                friends.add(EnlightenContract.FriendEntry.convertToFriend(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();

        addFriends(friends);

    }

    public class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mProfilePicture;
        public TextView mProfileName;
        public Friend friend;

        public FriendViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mProfilePicture = (ImageView) itemView.findViewById(R.id.profile_picture);
            mProfileName = (TextView) itemView.findViewById(R.id.profile_name);
        }

        public void bindView(Friend friend) {
            this.friend = friend;
            Picasso.with(mProfilePicture.getContext()).load(Utility.improveProfileImagePixel(friend.profilePictureUrl)).fit()
                    .into(mProfilePicture);
            mProfileName.setText(friend.profileName);
        }

        @Override
        public void onClick(View itemView) {

            Intent intent = new Intent(mContext, ArticleActivity.class);
            intent.putExtra(USER_NAME, friend.userName);
            intent.putExtra(USER_ID, friend.userId);
            mContext.startActivity(intent);
//
//            Bundle args = new Bundle();
//            args.putString(USER_NAME, friend.userName);
//            args.putLong(USER_ID, friend.userId);
//            activity.swapFragment(args);

        }
    }


    public class PackViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        int numItemsLongClicked = 0;

        public TextView packName;
        public Pack pack;

        public PackViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            packName = (TextView) itemView.findViewById(R.id.pack_item_name);
        }

        public void bindView(Pack pack) {
            packName.setText(pack.name);
            this.pack = pack;
        }


        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PackActivity.class);
            intent.putExtra(ChosenFriendsFragment.PACK_ID_TAG, pack.currentPackId);
            intent.putExtra(PackActivity.PARENT_PACK_ID_TAG, pack.parentPackId);
            mContext.startActivity(intent);
        }

    }
}
