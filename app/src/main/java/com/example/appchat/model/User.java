package com.example.appchat.model;
import android.util.Log;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("_User")
public class User extends ParseUser {

    public User() {
        // Constructor vacío necesario para Parse
    }

    public String getRedSocial() {

        return getString("redsocial");
    }

    public void setRedSocial(String redSocial) {
        if (redSocial != null) {
            put("redsocial", redSocial);
        }
    }


    public String getFotoperfil() {
        return getString("foto_perfil");
    }

    public void setFotoperfil(String fotoperfil) {
        if (fotoperfil != null) {
            put("foto_perfil", fotoperfil);
        }
    }

    // Getter y setter para "username"
    public String getUsername() {
        return getString("username");
    }

    public void setUsername(String username) {
        put("username", username);
    }

    // Getter y setter para "email"
    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        if (email != null) {
            put("email", email);
        } else {
            Log.w("User", "El correo electrónico es nulo.");
        }
    }

    // Getter y setter para "password"
    public String getPassword() {
        return getString("password");
    }

    public void setPassword(String password) {
        put("password", password);
    }

    // Getter para "id" (no necesitas un setter para "id" porque Parse lo genera automáticamente)
    public String getId() {
        return getObjectId();
    }


}
