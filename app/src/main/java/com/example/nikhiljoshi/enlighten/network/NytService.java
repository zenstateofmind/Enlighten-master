package com.example.nikhiljoshi.enlighten.network;

import com.example.nikhiljoshi.enlighten.pojo.BookList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface that will be implemented by Retrofit in {@link ServiceGenerator}
 * to generate the methods that will be used to access New York Times information
 */
public interface NytService {

    /**
     * Returns {@link BookList} that contains the information on all the books
     * that are present in New York Times 'Combined Print and EBook Non Fiction'
     * list
     */
    @GET("/svc/books/v2/lists/{date}/combined-print-and-e-book-nonfiction")
    Call<BookList> getBookLists(@Path("date") String date, @Query("api-key") String booksApi);

    //Most Popular API


    //Top Stories API
}
