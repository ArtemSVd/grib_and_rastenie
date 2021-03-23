package com.example.gribyandrasteniyamap.service.rx;

import android.annotation.SuppressLint;

import com.example.gribyandrasteniyamap.dto.CommentDto;
import com.example.gribyandrasteniyamap.service.http.PlantsAppClient;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RxCommentService {
    @Inject
    PlantsAppClient httpClient;

    @Inject
    RxCommentService() {}

    @SuppressLint("CheckResult")
    public void getComments(Long plantId, Consumer<List<CommentDto>> successCallback) {
        Observable.fromCallable(() -> httpClient.getCommentsFromServer(plantId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successCallback::accept);
    }

    @SuppressLint("CheckResult")
    public void addComment(CommentDto commentDto, Consumer<CommentDto> successCallback) {
        Observable.fromCallable(() -> httpClient.addComment(commentDto))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successCallback::accept);
    }
}
