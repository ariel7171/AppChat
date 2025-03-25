package com.example.appchat.providers;

import com.example.appchat.model.User;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
public class AuthProvider {

    public AuthProvider() {

    }
    public LiveData<String> signIn(String email, String password) {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        ParseUser.logInInBackground(email, password, (user, e) -> {
            if (e == null) {
                // Login exitoso
                authResult.setValue(user.getObjectId());
                Log.d("AuthProvider", "Usuario autenticado exitosamente: " + user.getObjectId());
            } else {
                // Error en el login
                Log.e("AuthProvider", "Error en inicio de sesión: ", e);
                authResult.setValue(null);
            }
        });
        return authResult;
    }
    // Registro con Parse
    public LiveData<String> signUp(User user) {
        MutableLiveData<String> authResult = new MutableLiveData<>();

        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            Log.e("AuthProvider", "Uno o más valores son nulos: " +
                    "Username=" + user.getUsername() + ", " +
                    "Password=" + user.getPassword() + ", " +
                    "Email=" + user.getEmail());
            authResult.setValue(null);
            return authResult;
        }

        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        user.setACL(acl);

        //Parse.getLocalDatastore().clear(); // Limpia datos locales
        ParseUser.logOut();

        user.signUpInBackground(e -> {
            if (e == null) {
                // Registro exitoso
                authResult.setValue("Registro exitoso"); // TODO: La linea original era: authResult.setValue(parseUser.getObjectId()); por eso mostraba el id en el showtoast
                Log.d("AuthProvider", "Usuario registrado exitosamente: " + user.getId());
            } else {
                // Error en el registro
                Log.e("AuthProvider", "Error en registro: ", e);
                authResult.setValue(null);
            }
        });
        return authResult;
    }


    public LiveData<Boolean> logout() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        ParseUser.logOutInBackground(e -> {
            if (e == null) {
                logoutResult.setValue(true);
                Log.d("AuthProvider", "Caché eliminada y usuario desconectado.");

            } else {

                logoutResult.setValue(false);
                Log.e("AuthProvider", "Error al desconectar al usuario: ", e);
            }
        });
        return logoutResult;
    }
}

