package com.example.nikhiljoshi.enlighten.ui.Fragment;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.ArticleAdapter;
import com.example.nikhiljoshi.enlighten.adapter.FriendAndPackAdapter;
import com.example.nikhiljoshi.enlighten.network.MyTwitterApi;
import com.example.nikhiljoshi.enlighten.ui.Activity.LoadingActivity;

/**
 * Created by nikhiljoshi on 5/28/16.
 */
public class ArticlesFragment extends Fragment {

    private ArticleAdapter mArticleAdapter;

    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_articles_view, container, false);

        Bundle arguments = getArguments();
        String userName = arguments.getString(FriendAndPackAdapter.USER_NAME);
        long userId = arguments.getLong(FriendAndPackAdapter.USER_ID);

        ((LoadingActivity) getActivity()).startLoadingDialogBox();

        mArticleAdapter = new ArticleAdapter((LoadingActivity)getActivity());
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.articles_recycle_view);
        mRecyclerView.setAdapter(mArticleAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        MyTwitterApi api = new MyTwitterApi(getActivity().getApplicationContext());
        api.getUserTweetsWithLinks(userId, userName, mArticleAdapter);


        return rootView;
    }
}
