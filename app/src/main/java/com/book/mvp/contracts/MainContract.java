package com.book.mvp.contracts;

import com.book.retrofit.models.AddingVideoResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public interface MainContract {

    interface View {

        void onVideoCreateSuccess(Integer id);

        void onVideoAddSuccess(AddingVideoResponse.Object videoObject);

        void showSnackbar(String message);

    }

    interface Presenter {

        void onDestroy();

        void onVideoCreateCalled();

        void onVideoAddCalled(MultipartBody.Part file, Integer video_general_id);

    }

    interface Interactor {

        interface OnFinishedListener {

            void onFinished(Integer id);

            void onFinished(AddingVideoResponse.Object object);

            void onFailure(String message);

        }

        void createVideo(OnFinishedListener onFinishedListener);

        void addVideo(OnFinishedListener onFinishedListener, MultipartBody.Part file, RequestBody video_general_id);

    }

}
