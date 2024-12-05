package com.example.appchat.providers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.appchat.model.User;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

public class UserProvider {

    public LiveData<String> createUser(User user) {

        MutableLiveData<String> result = new MutableLiveData<>();
        ParseObject userObject = new ParseObject("User");
        userObject.put("user_id", user.getId());
        userObject.put("email", user.getEmail());
        userObject.put("password", user.getPassword());

        userObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    result.setValue("Usuario creado correctamente");
                } else {
                    result.setValue("Error al crear usuario");
                }
            }
        });

        return result;
    }

    // Método para obtener un usuario por su correo electrónico en Parse
    public LiveData<User> getUser(String email) {
        MutableLiveData<User> userData = new MutableLiveData<>();

        // Crear una consulta para obtener el usuario por su correo electrónico
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("email", email);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e == null && users.size() > 0) {
                    ParseObject userObject = users.get(0);
                    User user = new User();
                    user.setId(userObject.getString("user_id"));
                    user.setEmail(userObject.getString("email"));
                    user.setPassword(userObject.getString("password"));

                    userData.setValue(user);
                } else {
                    userData.setValue(null);
                }
            }
        });

        return userData;
    }

    // Método para actualizar un usuario en Parse
    public LiveData<String> updateUser(User user) {
        MutableLiveData<String> result = new MutableLiveData<>();

        // Crear una consulta para obtener el usuario por su ID
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("user_id", user.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e == null && users.size() > 0) {
                    ParseObject userObject = users.get(0);
                    userObject.put("email", user.getEmail());
                    userObject.put("password", user.getPassword());

                    // Guardar el usuario actualizado en Parse
                    userObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                result.setValue("Usuario actualizado correctamente");
                            } else {
                                result.setValue("Error al actualizar usuario");
                            }
                        }
                    });
                } else {
                    result.setValue("Usuario no encontrado");
                }
            }
        });
        return result;
    }

    // Método para eliminar un usuario en Parse
    public LiveData<String> deleteUser(String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        // Crear una consulta para obtener el usuario por su ID
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("user_id", userId);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if (e == null && users.size() > 0) {
                    ParseObject userObject = users.get(0);

                    // Eliminar el usuario de Parse
                    userObject.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                result.setValue("Usuario eliminado correctamente");
                            } else {
                                result.setValue("Error al eliminar usuario");
                            }
                        }
                    });
                } else {
                    result.setValue("Usuario no encontrado");
                }
            }
        });
        return result;
    }

}
