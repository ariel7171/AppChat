package com.example.appchat.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like") // Nombre de la tabla en Parse
public class Like extends ParseObject {

    // Constructor vacío requerido por Parse
    public Like() {}

    // 🔹 Asociar la reacción con un Comentario
    public void setComentario(Comentario comentario) {
        put("comentario", comentario);
    }

    public Comentario getComentario() {
        return (Comentario) getParseObject("comentario");
    }

    // 🔹 Asociar la reacción con un Usuario
    public void setUser(User user) {
        put("user", user);
    }

    public User getUser() {
        return (User) getParseUser("user");
    }

    // 🔹 Tipo de reacción ("like" o "dislike")
    public void setTipo(String tipo) {
        put("tipo", tipo);
    }

    public String getTipo() {
        return getString("tipo");
    }

    // 🔹 Obtener el ID del Like (ObjectId generado por Parse)
    public String getId() {
        return getObjectId();
    }
}
