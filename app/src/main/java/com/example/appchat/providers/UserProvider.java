package com.example.appchat.providers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appchat.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.util.List;
import android.util.Log;

public class UserProvider {

    public UserProvider() {
        // Constructor vacío
    }

    // Método para obtener el usuario actual
    public LiveData<User> getCurrentUser() {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        ParseUser parseUser = ParseUser.getCurrentUser();
        if (parseUser != null) {
            User user = (User) parseUser;
            userLiveData.setValue(user); // Establece el usuario en LiveData
            Log.d("UserProvider", "Usuario actual obtenido: " + user.getObjectId());
        } else {
            userLiveData.setValue(null); // No hay usuario autenticado
            Log.e("UserProvider", "No hay usuario autenticado.");
        }
        return userLiveData;
    }

    // Método para actualizar un usuario
    public LiveData<Boolean> updateUser(User user) {
        MutableLiveData<Boolean> updateResult = new MutableLiveData<>();

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    updateResult.setValue(true);
                    Log.d("UserProvider", "Usuario actualizado exitosamente: " + user.getObjectId());
                } else {
                    updateResult.setValue(false);
                    Log.e("UserProvider", "Error actualizando usuario: ", e);
                }
            }
        });

        return updateResult;
    }

    // Método para obtener un usuario por ID
    public LiveData<User> getUserById(String userId) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.getInBackground(userId, (user, e) -> {
            if (e == null) {
                userLiveData.setValue(user);
                Log.d("UserProvider", "Usuario obtenido: " + user.getObjectId());
            } else {
                userLiveData.setValue(null);
                Log.e("UserProvider", "Error al obtener usuario: ", e);
            }
        });

        return userLiveData;
    }

    // Método para obtener todos los usuarios (opcional)
    public LiveData<List<User>> getAllUsers() {
        MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();
        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.findInBackground(new FindCallback<User>() {
            @Override
            public void done(List<User> users, ParseException e) {
                if (e == null) {
                    usersLiveData.setValue(users);
                    Log.d("UserProvider", "Usuarios obtenidos: " + users.size());
                } else {
                    usersLiveData.setValue(null);
                    Log.e("UserProvider", "Error al obtener usuarios: ", e);
                }
            }
        });

        return usersLiveData;
    }
}