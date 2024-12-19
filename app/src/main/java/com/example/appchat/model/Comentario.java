package com.example.appchat.model;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Comentario")
public class Comentario extends ParseObject {

    public Comentario() {
        // Constructor vacío necesario para Parse
    }

    public String getId() {
        return getObjectId();
    }

    public String getTexto() {
        return getString("texto");
    }

    public void setTexto(String texto) {
        put("texto", texto);
    }


    public Post getPost() {
        return (Post) getParseObject("post");
    }

    public void setPost(Post post) {
        if (post!= null) {
            put("post", post);
        } else {
            Log.w("Comentario", "El post es nulo.");
        }
    }

    public User getUser() {
        return (User) getParseObject("user");
    }

    public void setUser(User user) {
        if (user!= null) {
            put("user", user);
        } else {
            Log.w("Comentario", "El usuario es nulo.");
        }
    }
}
