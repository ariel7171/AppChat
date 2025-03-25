package com.example.appchat.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appchat.model.Post;
import com.example.appchat.providers.PostProvider;
import com.parse.ParseObject;

import java.util.List;

public class PostViewModel extends ViewModel {
    private final MutableLiveData<String> postSuccess = new MutableLiveData<>();
    private final PostProvider postProvider = new PostProvider();
    private final MutableLiveData<List<Post>> posts = new MutableLiveData<>();
    private final MutableLiveData<List<ParseObject>> commentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Post> postLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> postCreated = new MutableLiveData<>();

    public PostViewModel() {
        loadPosts();
    }

    // Cargar posts y actualizar el MutableLiveData
    public void loadPosts() {
        postProvider.getAllPosts().observeForever(newPosts -> {
            posts.setValue(newPosts);
        });
    }

    public LiveData<String> getPostSuccess() {
        return postSuccess;
    }

    public void publicar(Post post) {
        postProvider.addPost(post)
                .observeForever(result -> {
                    // Agregamos un log para depuración
                    Log.d("PostViewModel", "Resultado de addPost: " + result);

                    // Usar postValue en lugar de setValue para asegurar que se actualice
                    postSuccess.setValue(result);
                });
    }

    public LiveData<Boolean> getPostCreated() {
        return postCreated;
    }


    public void reloadPosts() {
        loadPosts(); // Forzar recarga
    }

    public LiveData<List<Post>> getPosts() {
        return posts;
    }

    public LiveData<Integer> countPosts() {
        return postProvider.countPostsByCurrentUser();
    }

    // Método para obtener un Post por ID
    public void getPostById(String postId) {
        postProvider.getPostById(postId, new PostProvider.GetPostCallback() {
            @Override
            public void onSuccess(Post post) {
                postLiveData.setValue(post); // Actualiza el LiveData con el post obtenido
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error); // Actualiza el LiveData con el mensaje de error
            }
        });
    }

    // Método para obtener el LiveData del Post
    public LiveData<Post> getPost() {
        return postLiveData;
    }

    // Método para obtener el LiveData del mensaje de error
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Post> getPostById2(String postId) {
        return postProvider.getPostById2(postId);
    }

    public LiveData<List<Post>> getPostsByCurrentUser() {
        return postProvider.getPostsByCurrentUser();
    }

    public void eliminarPost(String postId) {
        postProvider.deletePost(postId).observeForever(mensaje -> {
            if (mensaje.equals("Post eliminado correctamente")) {
                postSuccess.postValue(mensaje);
            } else {
                errorLiveData.postValue(mensaje);
            }
        });
    }

}