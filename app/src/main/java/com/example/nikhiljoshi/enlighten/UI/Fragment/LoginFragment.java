package com.example.nikhiljoshi.enlighten.ui.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.nikhiljoshi.enlighten.MyApplication;
import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.data.Contract.EnlightenContract;
import com.example.nikhiljoshi.enlighten.ui.Activity.MainActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.SelectFriendsActivity;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.SessionManager;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by nikhiljoshi on 5/5/16.
 */
public class LoginFragment extends Fragment {

    @Inject
    SessionManager<TwitterSession> sessionManager;
    private TwitterLoginButton loginButton;
    private View rootView;

    private static final String[] FRIENDS_COLUMNS = {
            EnlightenContract.FriendEntry.COLUMN_PROFILE_NAME
    };

    private static final int COL_PROFILE_NAME = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication)getActivity().getApplication()).baseTwitterComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final TwitterSession activeSession = sessionManager.getActiveSession();
        if (activeSession != null) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            Toast.makeText(getContext(), "Non null", Toast.LENGTH_LONG).show();
            startActivity(intent);
        } else {
            loginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button2);
            login();
        }

    }

    private void login() {
        Callback<TwitterSession> callback = new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                if (containsFriends()) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), SelectFriendsActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        };
        loginButton.setCallback(callback);
    }

    private boolean containsFriends() {

        List<String> chosenFriendsProfileNames = new ArrayList<>();

        final Uri uriWithCurrentUserSessionId =
                EnlightenContract.FriendEntry.buildFriendUriWithCurrentUserSessionId(Twitter.getSessionManager().getActiveSession().getUserId());
        final Cursor cursor = getContext().getContentResolver().query(uriWithCurrentUserSessionId, FRIENDS_COLUMNS, null, null, null);

        if (cursor.moveToFirst()) {
            cursor.close();
            return true;


        }

        cursor.close();
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
}
