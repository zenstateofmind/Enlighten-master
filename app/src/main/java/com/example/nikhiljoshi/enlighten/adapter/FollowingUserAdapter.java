package com.example.nikhiljoshi.enlighten.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikhiljoshi.enlighten.R;

import java.util.List;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class FollowingUserAdapter extends RecyclerView.Adapter<FollowingUserAdapter.FollowingUserViewHolder> implements DataSwappableAdapter {

    public static class FollowingUserViewHolder extends RecyclerView.ViewHolder {

        public FollowingUserViewHolder(View itemView) {
            super(itemView);
        }

    }

    @Override
    public FollowingUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View followerImageUsernameItemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_image_username_item, parent, false);

        FollowingUserViewHolder viewHolder = new FollowingUserViewHolder(followerImageUsernameItemView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(FollowingUserViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void swapData(List newObjects) {

    }
}
