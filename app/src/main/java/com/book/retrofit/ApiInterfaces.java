package com.book.retrofit;

import com.book.retrofit.models.AddingVideoResponse;
import com.book.retrofit.models.CreationResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterfaces {

    // init new video
    @POST("video/create")
    Call<CreationResponse> createVideo();

    // add video
    @Multipart
    @POST("video/setvideo")
    Call<AddingVideoResponse> addVideo(@Part MultipartBody.Part file,
                                       @Part("video_general_id") RequestBody video_general_id);
}
