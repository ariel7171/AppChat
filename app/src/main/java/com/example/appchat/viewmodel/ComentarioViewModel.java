package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchat.model.Comentario;
import com.example.appchat.providers.ComentarioProvider;

import java.util.List;

public class ComentarioViewModel extends ViewModel {
    private final ComentarioProvider comentarioProvider = new ComentarioProvider();
    private final MutableLiveData<List<Comentario>> commentsLiveData = new MutableLiveData<>();

    public ComentarioViewModel() {}

    public LiveData<List<Comentario>> getCommentsByPost(String postId) {
        comentarioProvider.getCommentsByPost(postId).observeForever(commentsLiveData::setValue);
        return commentsLiveData;
    }

    public LiveData<String> saveComment(Comentario comentario) {
        return comentarioProvider.saveComment(comentario);
    }

    public LiveData<String> deleteComment(String commentId) {
        return comentarioProvider.deleteComment(commentId);
    }
}