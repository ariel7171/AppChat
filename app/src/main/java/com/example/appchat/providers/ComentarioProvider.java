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
        acl.setPublicWriteAccess(true);
        comentario.setACL(acl);
        comentario.saveInBackground(e -> {
            if (e == null) {
                // Importante: solo establece el valor una vez
                if (result.getValue() == null) {
                    result.setValue("Comentario guardado exitosamente");
                    Log.d("ComentarioProvider", "Comentario guardado exitosamente: " + comentario.getObjectId());
                }
            } else {
                // Importante: solo establece el valor una vez
                if (result.getValue() == null) {
                    result.setValue("Error al guardar el comentario: " + e.getMessage());
                    Log.e("ComentarioProvider", "Error al guardar el comentario: ", e);
                }
            }
        });
        return result;
    }

    public LiveData<String> deleteComment(String commentId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        if (commentId == null) {
            result.setValue("ID del comentario no puede ser nulo");
            return result;
        }

        ParseQuery<Comentario> query = ParseQuery.getQuery(Comentario.class);
        query.getInBackground(commentId, (comentario, e) -> {
            if (e == null) {
                comentario.deleteInBackground(deleteError -> {
                    if (deleteError == null) {
                        result.setValue("Comentario eliminado exitosamente");
                        Log.d("ComentarioProvider", "Comentario eliminado exitosamente: " + commentId);
                    } else {
                        result.setValue("Error al eliminar el comentario: " + deleteError.getMessage());
                        Log.e("ComentarioProvider", "Error al eliminar el comentario: ", deleteError);
                    }
                });
            } else {
                result.setValue("No se encontró el comentario: " + e.getMessage());
                Log.e("ParseError", "Error al recuperar el comentario para eliminar: ", e);
            }
        });

        return result;
    }

}
