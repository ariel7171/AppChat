package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.appchat.model.User;
import com.example.appchat.providers.UserProvider;

import java.util.List;

public class UserViewModel extends ViewModel {
    private final UserProvider userProvider;

    // LiveData para el usuario actual
    private LiveData<User> currentUser;

    public UserViewModel() {
        userProvider = new UserProvider(); // Inicializa UserProvider
    }

    // Método para obtener el usuario actual
    public LiveData<User> getCurrentUser() {
        if (currentUser == null) {
            currentUser = userProvider.getCurrentUser(); // Obtiene el usuario actual
        }
        return currentUser;
    }

    // Método para actualizar un usuario
    public LiveData<Boolean> updateUser(User user) {
        return userProvider.updateUser(user); // Llama al método de actualización del UserProvider
    }

    // Método para obtener un usuario por ID
    public LiveData<User> getUserById(String userId) {
        return userProvider.getUserById(userId); // Llama al método para obtener usuario por ID en UserProvider
    }

    // Método para obtener todos los usuarios
    public LiveData<List<User>> getAllUsers() {
        return userProvider.getAllUsers(); // Llama al método para obtener todos los usuarios en UserProvider
    }

    public LiveData<String> getEmailUsuarioLiveData() {
        return userProvider.getEmailUsuarioLiveData();
    }

    public LiveData<String> getErrorLiveData() {
        return userProvider.getErrorLiveData();
    }

    public void obtenerEmailUsuario(String targetUserId) {
        userProvider.obtenerEmailUsuario(targetUserId);
    }

}