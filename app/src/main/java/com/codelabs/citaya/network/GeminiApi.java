package com.codelabs.citaya.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApi {

    @POST(
            "v1beta/models/gemini-3.5-flash:generateContent"
    )

    Call<GeminiResponse> generar(

            @Query("key")
            String apiKey,

            @Body
            GeminiRequest request
    );
}
