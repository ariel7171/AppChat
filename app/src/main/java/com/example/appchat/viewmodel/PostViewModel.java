package com.example.appchat.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.appchat.model.Post;
import com.example.appchat.providers.PostProvider;
import com.parse.ParseObject;
import java.util.List;

public class PostViewModel extends ViewModel {
    private final MutableLiveData<String> postSuccess = new MutableLiveData<>();
    private final PostProvider postProvider;
    private LiveData<List<Post>> posts;
    private final MutableLiveData<List<ParseObject>> commentsLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Post> postLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public PostViewModel() {
        posts = new MutableLiveData<>();
        postProvider = new PostProvider();
    }

    public LiveData<String> getPostSuccess() {
        return postSuccess;
    }

    public void publicar(Post post) {
        postProvider.addPost(post)
                .observeForever(result -> {
                    postSuccess.setValue(result);
                });
    }

    public LiveData<List<Post>> getPosts() {
        posts = postProvider.getAllPosts();
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

}