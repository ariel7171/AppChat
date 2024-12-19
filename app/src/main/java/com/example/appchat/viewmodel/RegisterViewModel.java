package com.example.appchat.viewmodel;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.example.appchat.model.User;
import com.example.appchat.providers.AuthProvider;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<String> registerResult = new MutableLiveData<>();
    private final AuthProvider authProvider;

    public RegisterViewModel() {
        this.authProvider = new AuthProvider();
    }

    public LiveData<String> getRegisterResult() {
        return registerResult;
    }

    public void register(User user) {

        LiveData<String> result = authProvider.signUp(user);

        result.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String objectId) {
                if (objectId != null) {
                    // Registration successful
                    registerResult.setValue("Registro exitoso"); //TODO: La linea original era: registerResult.setValue(objectId); por eso mostraba el id en el showtoast
                    Log.d("RegisterViewModel", "Usuario registrado con ID: " + objectId);
                } else {
                    // Registration failed
                    registerResult.setValue("Error al registrar"); // TODO: La linea original era: registerResult.setValue(null); entonces si el registro fallaba no se mostraba nada en el toast
                    Log.e("RegisterViewModel", "Error durante el registro.");
                }
                result.removeObserver(this);
            }
        });
    }
}







