package com.github.humongouswin.insidefun.services;

import com.github.humongouswin.insidefun.browser.VideoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

public interface APIInterface {
    @GET("login")
    Call<VideoResponse> getVideoResponse(@Body VideoResponse loginRequest);
}