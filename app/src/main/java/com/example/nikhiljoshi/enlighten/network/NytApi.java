package com.example.nikhiljoshi.enlighten.network;

import android.util.Log;

import com.example.nikhiljoshi.enlighten.BuildConfig;
import com.example.nikhiljoshi.enlighten.pojo.Book;
import com.example.nikhiljoshi.enlighten.pojo.BookList;
import com.example.nikhiljoshi.enlighten.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by nikhiljoshi on 4/25/16.
 */
public class NytApi {

    private NytService service;

    public static final String NYT_API_LOG_TAG = NytApi.class.getSimpleName();

    public NytApi() {
        this.service = ServiceGenerator.createService(NytService.class);
    }

    /**
     * Returns a list of the top non fiction books that are listed on the New York Times list
     * given a specific date. The service will search forward (into the future) for the closest
     * publication date to the date you specify
     */
    public List<Book> getTopNonFictionBooksList(Date date){
        final Call<BookList> call = service.getBookLists(Utility.formatDate(date), BuildConfig.BOOKS_API_KEY);
        final List<Book> books = new ArrayList<>();

        try {
            final Response<BookList> response = call.execute();
            if (response.isSuccessful()) {
                final BookList bookList = response.body();
                books.addAll(bookList.results);
            } else {
                Log.e(NYT_API_LOG_TAG, "There were problems accessing " +
                            " the NYT Book Reviews API: Error code: " + response.code() +
                            " and the error message: " + response.errorBody());
            }
        } catch (IOException e) {
            Log.e(NYT_API_LOG_TAG, "Failures in accessing the server: " + e.getMessage());
        }

       return books;
    }

}
