package com.hababk.userapp.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by a_man on 05-12-2017.
 */

public class ApiUtils {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://jeddahstartupscity.com/cookfu/";

    public static Retrofit getClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder().create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
