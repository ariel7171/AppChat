package com.example.appchat.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appchat.providers.AuthProvider;

public class MainViewModel extends ViewModel {
    public final AuthProvider authProvider;

    public MainViewModel(Context context){
        authProvider=new AuthProvider(context);
    }

    public LiveData<String> login(String email, String password) {
        MutableLiveData<String> loginResult = new MutableLiveData<>();
        authProvider.signIn(email, password).observeForever(userId -> {
            loginResult.setValue(userId);
        });
        return loginResult;
    }

}
