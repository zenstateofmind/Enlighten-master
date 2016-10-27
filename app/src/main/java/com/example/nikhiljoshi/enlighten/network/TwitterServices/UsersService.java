package com.example.nikhiljoshi.enlighten.network.TwitterServices;

import com.twitter.sdk.android.core.models.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by nikhiljoshi on 5/30/16.
 */
public interface UsersService {

    @POST("/1.1/users/lookup.json")
    void lookup(@Query("user_id") String commaSeparatedUserIds, Callback<List<User>> callback);
}
