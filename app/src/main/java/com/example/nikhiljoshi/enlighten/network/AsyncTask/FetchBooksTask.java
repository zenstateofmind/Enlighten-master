package com.example.nikhiljoshi.enlighten.network.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;

import com.example.nikhiljoshi.enlighten.adapter.DataSwappableAdapter;
import com.example.nikhiljoshi.enlighten.network.NytApi;
import com.example.nikhiljoshi.enlighten.pojo.Book;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * AsyncTask that fetches the latest books that are listed
 * at the top of New York Times non fiction books list
 */
public class FetchBooksTask extends AsyncTask<Object, Object, Book[]> {

    private static String LOG_TAG = FetchBooksTask.class.getSimpleName();
    private DataSwappableAdapter mAdapter;

    public FetchBooksTask(DataSwappableAdapter adapter) {
        mAdapter = adapter;
    }


    @Override
    protected Book[] doInBackground(Object... params) {
        NytApi nytApi = new NytApi();
        List<Book> topNonFictionBooks = nytApi.getTopNonFictionBooksList(new Date());
        Log.i(LOG_TAG, "Fetched " + topNonFictionBooks.size() + " books from the New York Times" +
                " resources!");

        return topNonFictionBooks.toArray(new Book[0]);
    }

    @Override
    protected void onPostExecute(Book[] books) {
        if (books.length > 0) {
            //swap data in the view holder
            List<Book> newBooks = Arrays.asList(books);
            mAdapter.swapData(newBooks);
        }
    }
}
