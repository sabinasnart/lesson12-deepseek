package com.amicus.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by sizik on 27.09.2025.
 */
public interface DeepSeekAPI {
    @Headers({"Content-Type: application/json","Authorization: Bearer sk-eb3954f44d2744d6ab2b3fa06f801e62"})
    @POST("v1/chat/completions")
    Call<ChatResponse> sendMessage(@Body ChatRequest request);
}
