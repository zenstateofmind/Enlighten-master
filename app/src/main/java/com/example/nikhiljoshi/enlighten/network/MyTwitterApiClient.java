package com.example.nikhiljoshi.enlighten.network;

import com.example.nikhiljoshi.enlighten.network.TwitterServices.FriendsService;
import com.example.nikhiljoshi.enlighten.network.TwitterServices.UsersService;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;

/**
 * Created by nikhiljoshi on 5/8/16.
 */
public class MyTwitterApiClient extends TwitterApiClient {

    public MyTwitterApiClient(Session session) {
        super(session);
    }

    public FriendsService getFriendsService() {
        return (FriendsService) this.getService(FriendsService.class);
    }

    public UsersService getUsersService() {
        return (UsersService) this.getService(UsersService.class);
    }

}
