package com.example.appchat.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appchat.providers.LikeProvider;

public class LikeViewModel extends ViewModel {
    private final LikeProvider likeProvider;
    private final MutableLiveData<int[]> reactionsCount = new MutableLiveData<>();
    private final MutableLiveData<String> userReaction = new MutableLiveData<>();

    public LikeViewModel() {
        likeProvider = new LikeProvider();
    }

    // 🔹 Método para agregar, cambiar o eliminar una reacción
    public void toggleReaction(String comentarioId, String userId, String tipo) {
        likeProvider.toggleReaction(comentarioId, userId, tipo).observeForever(result -> {
            if (result.contains("guardada") || result.contains("cambiada") || result.contains("eliminada")) {
                loadReactions(comentarioId); // Refrescar contador
                loadUserReaction(comentarioId, userId); // Refrescar reacción del usuario
            }
        });
    }

    // 🔹 Método para cargar la cantidad de Likes y Dislikes
    public void loadReactions(String comentarioId) {
        likeProvider.countReactions(comentarioId).observeForever(reactionsCount::setValue);
    }

    public LiveData<int[]> getReactionsCount() {
        return reactionsCount;
    }

    // 🔹 Método para cargar la reacción del usuario actual
    public void loadUserReaction(String comentarioId, String userId) {
        likeProvider.getUserReaction(comentarioId, userId).observeForever(userReaction::setValue);
    }

    public LiveData<String> getUserReaction() {
        return userReaction;
    }
}
