package com.example.appchat.providers;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PostProvider {

    public LiveData<String> addPost(Post post) {
        MutableLiveData<String> result = new MutableLiveData<>();
        // Configurar campos y ACL...
        ParseACL acl = new ParseACL();
        acl.setPublicReadAccess(true);
        acl.setWriteAccess(post.getUser().getId(), true);
        post.setACL(acl);
        post.saveInBackground(e -> {
            if (e == null) {
                List<String> imagenes = post.getImagenes();
                if (imagenes.isEmpty()) {
                    result.setValue("Post publicado"); // Notificar éxito si no hay imágenes
                    return;
                }

                ParseRelation<ParseObject> relation = post.getRelation("images");
                AtomicInteger counter = new AtomicInteger(imagenes.size());

                for (String url : imagenes) {
                    ParseObject imageObject = new ParseObject("Image");
                    imageObject.put("url", url);
                    imageObject.saveInBackground(imgSaveError -> {
                        if (imgSaveError == null) {
                            relation.add(imageObject);
                            // Verificar si todas las imágenes se guardaron
                            if (counter.decrementAndGet() == 0) {
                                post.saveInBackground(saveError -> {
                                    if (saveError == null) {
                                        result.setValue("Post publicado");
                                    } else {
                                        result.setValue("Error al guardar la relación: " + saveError.getMessage());
                                    }
                                });
                            }
                        } else {
                            result.setValue("Error al subir imagen: " + imgSaveError.getMessage());
                        }
                    });
                }
            } else {
                result.setValue("Error al guardar el post: " + e.getMessage());
            }
        });
        return result;
    }

    public LiveData<List<Post>> getPostsByCurrentUser() {
        MutableLiveData<List<Post>> result = new MutableLiveData<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            result.setValue(new ArrayList<>());
            return result;
        }
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", currentUser);
        query.include("user");
        query.findInBackground((posts, e) -> {
            if (e == null) {
                result.setValue(posts);
            } else {
                result.setValue(new ArrayList<>());
                Log.e("ParseError", "Error al recuperar los posts: ", e);
            }
        });
        return result;
    }

    public LiveData<List<Post>> getAllPosts() {
        MutableLiveData<List<Post>> result = new MutableLiveData<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user"); // Incluye el objeto User completo
        query.findInBackground((posts, e) -> {
            if (e == null) {
                result.setValue(posts);
            } else {
                result.setValue(new ArrayList<>());
            }
        });
        return result;
    }

    public LiveData<String> deletePost(String postId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                post.deleteInBackground(e1 -> {
                    if (e1 == null) {
                        Log.d("PostDelete", "Post eliminado con éxito.");
                        result.postValue("Post eliminado correctamente");
                    } else {
                        Log.e("PostDelete", "Error al eliminar el post: ", e1);
                        result.postValue("Error al eliminar el post: " + e1.getMessage());
                    }
                });
            } else {
                Log.e("PostDelete", "Error al encontrar el post: ", e);
                result.postValue("Error al encontrar el post: " + e.getMessage());
            }
        });

        return result;
    }

    public LiveData<Post> getPostDetail(String postId) {
        MutableLiveData<Post> result = new MutableLiveData<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include("user");
        query.include("images");
        query.getInBackground(postId, (post, e) -> {
            if (e == null) {
                ParseRelation<ParseObject> relation = post.getRelation("images");
                try {
                    List<ParseObject> images = relation.getQuery().find();
                    List<String> imageUrls = new ArrayList<>();
                    for (ParseObject imageObject : images) {
                        imageUrls.add(imageObject.getString("url"));
                    }
                    post.setImagenes(imageUrls);
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                result.setValue(post);
            } else {
                Log.e("ParseError", "Error al obtener el post: ", e);
                result.setValue(null);
            }
        });
        return result;
    }

    public LiveData<Integer> countPostsByCurrentUser() {
        MutableLiveData<Integer> result = new MutableLiveData<>();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser == null) {
            result.setValue(0);
            return result;
        }

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("user", currentUser);
        query.countInBackground((count, e) -> {
            if (e == null) {
                result.setValue(count);
            } else {
                Log.e("ParseError", "Error al contar los posts: ", e);
                result.setValue(0);
            }
        });
        return result;
    }

    /*
    public void getPostById(String postId, GetPostCallback callback) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("objectId", postId);
        query.findInBackground((posts, e) -> {
            if (e == null) {
                if (!posts.isEmpty()) {
                    callback.onSuccess(posts.get(0));
                } else {
                    callback.onError("No post found with that ID");
                }
            } else {
                callback.onError(e.getMessage());
            }
        });
    }

    public interface GetPostCallback {
        void onSuccess(Post post);
        void onError(String errorMessage);
    }
    */

    public LiveData<Post> getPostById(String postId) {
        MutableLiveData<Post> result = new MutableLiveData<>();
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo("objectId", postId);
        query.findInBackground((posts, e) -> {
            if (e == null) {
                if (!posts.isEmpty()) {
                    result.setValue(posts.get(0));
                    Log.d("PostFound", "Post found with ID: " + postId);
                } else {
                    result.setValue(null);
                    Log.d("PostNotFound", "No post found with that ID");
                }
            } else {
                //mensaje log del error
                Log.e("ParseError", "Error al buscar el post: ", e);
                result.setValue(null);
            }
        });
        return result;
    }
}






