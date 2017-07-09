package me.alapon.reaz.friendfinder.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Prince on 2/4/2017.
 */

public class RetrofitClient {

   public static final String BASE_URL = "http://alapon.me/reaz/finder/";

    ///public static final String BASE_URL = "http://192.168.0.101/finder/";

    private static Retrofit retrofit = null;


    public static Retrofit getRetrofitClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
