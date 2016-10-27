package com.example.nikhiljoshi.enlighten.pojo;

import com.google.gson.annotations.SerializedName;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class FriendsInfo {

    @SerializedName("users")
    public final List<User> users;

    @SerializedName("next_cursor")
    public final long nextCursor;

    public FriendsInfo(List<User> users, long nextCursor) {
        this.users = users;
        this.nextCursor = nextCursor;
    }
}
