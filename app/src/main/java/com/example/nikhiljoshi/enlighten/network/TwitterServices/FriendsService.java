package com.example.nikhiljoshi.enlighten.network.TwitterServices;

import com.example.nikhiljoshi.enlighten.pojo.FriendIds;
import com.example.nikhiljoshi.enlighten.pojo.FriendsInfo;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public interface FriendsService {

    @GET("/1.1/friends/list.json")
    void list(@Query("user_id") long user_id, @Query("cursor") long next_cursor, Callback<FriendsInfo> callback);

    @GET("/1.1/friends/ids.json")
    void ids(@Query("user_id") long user_id, @Query("screen_name") String screen_name, @Query("count") Integer count, Callback<FriendIds> callback);

    @GET("/1.1/friends/ids.json")
    FriendIds idsSynchronous(@Query("user_id") long user_id, @Query("screen_name") String screen_name, @Query("count") Integer count);

}
