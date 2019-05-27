package com.book.mvp.interactors;

import android.util.Log;

import com.book.mvp.contracts.MainContract;
import com.book.retrofit.ApiClient;
import com.book.retrofit.ApiInterfaces;
import com.book.retrofit.models.AddingVideoResponse;
import com.book.retrofit.models.CreationResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainInteractorImpl implements MainContract.Interactor {

    private ApiInterfaces apiService =
            ApiClient.getInstance().create(ApiInterfaces.class);
    private String TAG = "LOGGERR";

    @Override
    public void createVideo(OnFinishedListener onFinishedListener) {
        Call<CreationResponse> addVideo = apiService.createVideo();

        addVideo.enqueue(new Callback<CreationResponse>() {
            @Override
            public void onResponse(Call<CreationResponse> call, Response<CreationResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    CreationResponse creationResponse = response.body();
                    onFinishedListener.onFinished(creationResponse.getId());
                } else onFinishedListener.onFailure("Произошла ошибка сервера "+ statusCode +". Попытайтесь снова");
            }

            @Override
            public void onFailure(Call<CreationResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                onFinishedListener.onFailure("Произошла ошибка сервера. Попытайтесь снова");
            }
        });
    }

    @Override
    public void addVideo(OnFinishedListener onFinishedListener, MultipartBody.Part file, RequestBody video_general_id) {
        Call<AddingVideoResponse> addResearch = apiService.addVideo(
                file,
                video_general_id
        );

        addResearch.enqueue(new Callback<AddingVideoResponse>() {
            @Override
            public void onResponse(Call<AddingVideoResponse> call, Response<AddingVideoResponse> response) {
                int statusCode = response.code();
                if (statusCode == 200) {
                    AddingVideoResponse addingVideoResponse = response.body();
                    if (addingVideoResponse.getStatus().equals("OK")) {
                        onFinishedListener.onFinished(addingVideoResponse.getObject());
                        Log.d(TAG, "auth: " + response.body().getStatus());
                    } else if (addingVideoResponse.getStatus().toLowerCase().equals("error")) {
                        onFinishedListener.onFailure(addingVideoResponse.getError());
                    }
                } else onFinishedListener.onFailure("Произошла ошибка сервера "+ statusCode +". Попытайтесь снова");
            }

            @Override
            public void onFailure(Call<AddingVideoResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
                onFinishedListener.onFailure("Произошла ошибка сервера. Попытайтесь снова");
            }
        });
    }
}
