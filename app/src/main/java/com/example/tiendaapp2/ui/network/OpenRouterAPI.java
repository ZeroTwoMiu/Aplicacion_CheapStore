package com.example.tiendaapp2.ui.network;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenRouterAPI {
    @POST("api/v1/chat/completions")
    Call<RespuestaIA> enviarPrompt(@Header("Authorization") String authHeader, @Body RequestBody body);
}
