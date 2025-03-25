package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchat.providers.PostProvider;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

public class PostDetailViewModel extends ViewModel {

    private final MutableLiveData<List<ParseObject>> commentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final PostProvider postProvider;

    public PostDetailViewModel() {
        postProvider = new PostProvider();
    }

    public LiveData<List<ParseObject>> getCommentsLiveData() {
        return commentsLiveData;
    }


    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

}


