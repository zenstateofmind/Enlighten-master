package com.example.nikhiljoshi.enlighten.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ArticlesFragment;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment;

import static com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment.*;
import static com.example.nikhiljoshi.enlighten.ui.Fragment.ChosenFriendsFragment.*;

/**
 * Created by nikhiljoshi on 6/7/16.
 */
public class MainActivity extends AppCompatActivity implements SwapFragments {

    private static String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static String ARTICLE_FRAGMENT = "ARTICLES_FRAGMENT";
    public static Long NO_PACK = -1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Bundle arguments = new Bundle();
        arguments.putSerializable(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG, ActivityToStartOnFriendSelection.MAIN_ACTIVITY);
        arguments.putSerializable(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG, FriendSource.API);
        arguments.putLong(PACK_ID_TAG, NO_PACK);

        ChosenFriendsFragment fragment = new ChosenFriendsFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container, fragment, FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public void swapFragment(Bundle args) {
        ArticlesFragment articlesFragment = new ArticlesFragment();
        articlesFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_container,
                        articlesFragment,
                        FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }
}
