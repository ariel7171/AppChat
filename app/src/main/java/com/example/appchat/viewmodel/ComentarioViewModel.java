package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchat.model.Comentario;
import com.example.appchat.model.Post;
import com.example.appchat.providers.ComentarioProvider;
import com.parse.ParseObject;

import java.util.List;

public class ComentarioViewModel extends ViewModel {
    private final ComentarioProvider comentarioProvider= new ComentarioProvider();

    private final MutableLiveData<List<Comentario>> commentsLiveData = new MutableLiveData<>();

    public ComentarioViewModel(){};

    public LiveData<List<Comentario>> getCommentsByPost(String postId){

        return comentarioProvider.getCommentsByPost(postId);
    }

    public void saveComment(Comentario comentario){
        comentarioProvider.saveComment(comentario);
    }


}
