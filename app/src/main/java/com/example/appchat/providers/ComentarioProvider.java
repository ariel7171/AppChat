package com.example.appchat.providers;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appchat.model.Comentario;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ComentarioProvider {

    public LiveData<List<Comentario>> getCommentsByPost(String postId) {
        MutableLiveData<List<Comentario>> result = new MutableLiveData<>();
        if (postId==null) {
            result.setValue(new ArrayList<>());
            return result;
        }
        ParseQuery<Comentario> query = ParseQuery.getQuery(Comentario.class);
        query.whereEqualTo("post", ParseObject.createWithoutData("Post", postId));
        query.include("user");
        query.findInBackground((comentarios, e) -> {
            if (e == null) {
                Log.d("ComentarioProvider", "Comentarios supuestamente obtenidos: " + comentarios);
                result.setValue(comentarios);
            } else {
                result.setValue(new ArrayList<>());
                Log.e("ParseError", "Error al recuperar los comentarios: ", e);
            }
        });
        return result;
    }

    public LiveData<String> saveComment(Comentario comentario){
        MutableLiveData<String> result = new MutableLiveData<>();
        result.setValue(null);

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        comentario.setACL(acl);
        comentario.saveInBackground(e -> {
            if (e == null) {
                result.setValue("Comentario guardado exitosamente");
                Log.d("ComentarioProvider", "Comentario guardado exitosamente: " + comentario.getObjectId());
            } else {
                result.setValue("Error al guardar el comentario: " + e.getMessage());
                Log.e("ComentarioProvider", "Error al guardar el comentario: ", e);
            }
        });
        return result;
    }
}
