package com.example.appchat.providers;

import static com.parse.Parse.getApplicationContext;

import com.example.appchat.R;
import com.parse.Parse;
import com.parse.ParseUser;
import com.parse.ParseException;
import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.parse.LogInCallback;

public class AuthProvider {
    public AuthProvider(Context context) {
        Parse.initialize(new Parse.Configuration.Builder(context)
                .applicationId(context.getString(R.string.back4app_app_id))
                .clientKey(context.getString(R.string.back4app_client_key))
                .server(context.getString(R.string.back4app_server_url))
                .build()
        );
    }

    public AuthProvider() {}

    public LiveData<String> signIn(String email, String password) {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    // Login exitoso
                    authResult.setValue(user.getObjectId());
                    Log.d("AuthProvider", "Usuario autenticado exitosamente: " + user.getObjectId());
                } else {
                    // Error en el login
                    Log.e("AuthProvider", "Error en inicio de sesión: ", e);
                    authResult.setValue(null);
                }
            }
        });
        return authResult;
    }

    // Registro con Parse
    public LiveData<String> signUp(String email, String password) {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e == null) {
                // Registro exitoso
                authResult.setValue(user.getObjectId());
                Log.d("AuthProvider", "Usuario registrado exitosamente: " + user.getObjectId());
            } else {
                // Error en el registro
                Log.e("AuthProvider", "Error en registro: ", e);
                authResult.setValue(null);
            }
        });
        return authResult;
    }

    public LiveData<String> getCurrentUserID() {
        MutableLiveData<String> currentUserId = new MutableLiveData<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            currentUserId.setValue(currentUser.getObjectId());
        }
        return currentUserId;
    }

    public LiveData<Boolean> logOut() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        ParseUser.logOutInBackground(e -> {
            if (e == null) {
                logoutResult.setValue(true);
                if (getApplicationContext() != null) {
                    getApplicationContext().getCacheDir().delete();
                }
                Log.d("AuthProvider", "Caché eliminada y usuario desconectado.");

            } else {

                logoutResult.setValue(false);
                Log.e("AuthProvider", "Error al desconectar al usuario: ", e);
            }
        });
        return logoutResult;
    }

}