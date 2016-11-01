package com.example.nikhiljoshi.enlighten.ui.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.FriendAndPackAdapter;
import com.example.nikhiljoshi.enlighten.ui.Fragment.ArticlesFragment;

/**
 * Created by nikhiljoshi on 6/8/16.
 */
public class ArticleActivity extends AppCompatActivity implements LoadingActivity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_articles);

        String userName = getIntent().getStringExtra(FriendAndPackAdapter.USER_NAME);
        long userId = getIntent().getLongExtra(FriendAndPackAdapter.USER_ID, -1L);

        Bundle args = new Bundle();
        args.putString(FriendAndPackAdapter.USER_NAME, userName);
        args.putLong(FriendAndPackAdapter.USER_ID, userId);

        ArticlesFragment articlesFragment = new ArticlesFragment();
        articlesFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_articles, articlesFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void startLoadingDialogBox() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading_tweets));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    @Override
    public void stopLoadingDialogBox() {
        progressDialog.dismiss();
    }
}
