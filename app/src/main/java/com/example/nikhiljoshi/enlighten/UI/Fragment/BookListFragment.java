package com.example.nikhiljoshi.enlighten.ui.Fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.BookAdapter;
import com.example.nikhiljoshi.enlighten.network.AsyncTask.FetchBooksTask;

/**
 * Created by nikhiljoshi on 5/2/16.
 */
public class BookListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private BookAdapter mBookAdapter;
    private TextView mEmptyViewBookListings;

    /**
     * We inflate the recycler view layout and initialize the book adapter
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_book_listings, container, false);

        mEmptyViewBookListings = (TextView) rootView.findViewById(R.id.empty_view_book_listings);
        mBookAdapter = new BookAdapter(getActivity(), mEmptyViewBookListings);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_nyt_books);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // If we know that the contents arent going to change the size
        // of the recycler view
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mBookAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        // check if there are any internet errors or things like that
        // if so, update the why not available aspect
        if (!dataCollectionIssues()) {
            FetchBooksTask fetchBooksTask = new FetchBooksTask(mBookAdapter);
            fetchBooksTask.execute();
        }

        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Checks if there are any data collection issues -- problems connecting to the internet etc.
     * @return true if problem collecting data, false otherwise
     */
    private boolean dataCollectionIssues() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
            mEmptyViewBookListings.setText(R.string.no_books_available_internet_access);
            return true;
        }
        return false;
    }
}
