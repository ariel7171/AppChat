package com.example.appchat.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchat.model.Post;
import com.example.appchat.providers.PostProvider;

import java.util.List;

public class PostViewModel extends ViewModel {
    private final MutableLiveData<Boolean> postSuccess = new MutableLiveData<>();
    private final PostProvider postProvider;
    private LiveData<List<Post>> posts;

    public LiveData<Boolean> getPostSuccess() {
        return postSuccess;
    }

    public PostViewModel() {
        posts = new MutableLiveData<>();
        postProvider = new PostProvider();
    }

    public void publicar(Post post) {
        postProvider.addPost(post).observeForever(result -> {
                    if ("Post publicado".equals(result)) {
                        postSuccess.setValue(true);
                    } else {
                        postSuccess.setValue(false);
                    }
        });
    }

    public LiveData<List<Post>> getPosts() {
        posts = postProvider.getPostsByCurrentUser();
        Log.d("PostViewModel", "posts:");
        return posts;
    }
}
