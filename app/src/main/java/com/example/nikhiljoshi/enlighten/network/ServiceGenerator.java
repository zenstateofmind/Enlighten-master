package com.example.nikhiljoshi.enlighten.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Retrofit takes in the Interface that contains all the API calls
 * and generates an implementation of the interface
 *
 * @author Nikhil Joshi
 */
public class ServiceGenerator {

    public static String API_BASE_URL = "http://api.nytimes.com";
    public static OkHttpClient client = new OkHttpClient();

    public static <S> S createService(Class<S> serviceClass) {
        final Retrofit retrofit = new Retrofit.Builder().baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit.create(serviceClass);
    }
}
