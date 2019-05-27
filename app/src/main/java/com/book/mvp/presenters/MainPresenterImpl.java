package com.book.mvp.presenters;

import com.book.mvp.contracts.MainContract;
import com.book.retrofit.models.AddingVideoResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainPresenterImpl implements MainContract.Presenter, MainContract.Interactor.OnFinishedListener {

    private MainContract.View view;
    private MainContract.Interactor interactor;

    public MainPresenterImpl(MainContract.View view, MainContract.Interactor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void onDestroy() {
        view = null;
    }

    @Override
    public void onVideoCreateCalled() {
        interactor.createVideo(this);
    }

    @Override
    public void onVideoAddCalled(MultipartBody.Part file, Integer video_general_id) {
        RequestBody videoId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(video_general_id));
        interactor.addVideo(this, file, videoId);
    }

    @Override
    public void onFinished(Integer id) {
        if (view != null) {
            view.onVideoCreateSuccess(id);
        }
    }

    @Override
    public void onFinished(AddingVideoResponse.Object object) {
        if (view != null) {
            view.onVideoAddSuccess(object);
        }
    }

    @Override
    public void onFailure(String message) {
        if (view != null) {
            view.showSnackbar(message);
        }
    }
}
