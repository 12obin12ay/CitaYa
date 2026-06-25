package com.codelabs.citaya.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(){

        if(retrofit == null){

            HttpLoggingInterceptor log =
                    new HttpLoggingInterceptor();

            log.setLevel(
                    HttpLoggingInterceptor.Level.BODY
            );

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(log)
                            .build();

            retrofit =
                    new Retrofit.Builder()

                            .baseUrl(
                                    "https://generativelanguage.googleapis.com/"
                            )

                            .client(client)

                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )

                            .build();
        }

        return retrofit;
    }
}
