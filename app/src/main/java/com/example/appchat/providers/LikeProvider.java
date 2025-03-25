package com.example.appchat.providers;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.appchat.model.Like;
import com.example.appchat.model.Comentario;
import com.example.appchat.model.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;

public class LikeProvider {

    // 🔹 Método para agregar, cambiar o eliminar Like/Dislike
    public LiveData<String> toggleReaction(String comentarioId, String userId, String tipo) {
        MutableLiveData<String> result = new MutableLiveData<>();

        if (comentarioId == null || userId == null || (!tipo.equals("like") && !tipo.equals("dislike"))) {
            result.setValue("Error: Datos inválidos");
            return result;
        }

        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo("comentario", ParseObject.createWithoutData(Comentario.class, comentarioId));
        query.whereEqualTo("user", ParseObject.createWithoutData(User.class, userId));

        query.getFirstInBackground((like, e) -> {
            if (like == null) {
                // 1️⃣ No existe reacción → Crear un nuevo Like/Dislike
                Like nuevoLike = new Like();
                nuevoLike.setComentario(ParseObject.createWithoutData(Comentario.class, comentarioId));
                nuevoLike.setUser(ParseObject.createWithoutData(User.class, userId));
                nuevoLike.setTipo(tipo);

                nuevoLike.saveInBackground(error -> {
                    if (error == null) {
                        result.setValue("Reacción guardada: " + tipo);
                    } else {
                        result.setValue("Error al guardar reacción");
                    }
                });
            } else {
                // 2️⃣ Si el usuario ya reaccionó
                if (like.getTipo().equals(tipo)) {
                    // 3️⃣ Si vuelve a presionar el mismo botón, se elimina la reacción
                    like.deleteInBackground(error -> {
                        if (error == null) {
                            result.setValue("Reacción eliminada");
                        } else {
                            result.setValue("Error al eliminar reacción");
                        }
                    });
                } else {
                    // 4️⃣ Si cambia de Like a Dislike (o viceversa), se actualiza el tipo
                    like.setTipo(tipo);
                    like.saveInBackground(error -> {
                        if (error == null) {
                            result.setValue("Reacción cambiada a: " + tipo);
                        } else {
                            result.setValue("Error al actualizar reacción");
                        }
                    });
                }
            }
        });

        return result;
    }

    // 🔹 Método para contar Likes y Dislikes
    public LiveData<int[]> countReactions(String comentarioId) {
        MutableLiveData<int[]> result = new MutableLiveData<>();

        if (comentarioId == null) {
            result.setValue(new int[]{0, 0});
            return result;
        }

        // Consulta para contar Likes
        ParseQuery<Like> likesQuery = ParseQuery.getQuery(Like.class);
        likesQuery.whereEqualTo("comentario", ParseObject.createWithoutData(Comentario.class, comentarioId));
        likesQuery.whereEqualTo("tipo", "like");

        // Consulta para contar Dislikes
        ParseQuery<Like> dislikesQuery = ParseQuery.getQuery(Like.class);
        dislikesQuery.whereEqualTo("comentario", ParseObject.createWithoutData(Comentario.class, comentarioId));
        dislikesQuery.whereEqualTo("tipo", "dislike");

        likesQuery.countInBackground((likesCount, e1) -> {
            if (e1 == null) {
                dislikesQuery.countInBackground((dislikesCount, e2) -> {
                    if (e2 == null) {
                        result.setValue(new int[]{likesCount, dislikesCount});
                    } else {
                        result.setValue(new int[]{likesCount, 0});
                    }
                });
            } else {
                result.setValue(new int[]{0, 0});
            }
        });

        return result;
    }

    // 🔹 Método para verificar si un usuario ya reaccionó a un comentario
    public LiveData<String> getUserReaction(String comentarioId, String userId) {
        MutableLiveData<String> result = new MutableLiveData<>();

        if (comentarioId == null || userId == null) {
            result.setValue(null);
            return result;
        }

        ParseQuery<Like> query = ParseQuery.getQuery(Like.class);
        query.whereEqualTo("comentario", ParseObject.createWithoutData(Comentario.class, comentarioId));
        query.whereEqualTo("user", ParseObject.createWithoutData(User.class, userId));

        query.getFirstInBackground((like, e) -> {
            if (like != null) {
                result.setValue(like.getTipo()); // Retorna "like" o "dislike"
            } else {
                result.setValue(null); // No ha reaccionado aún
            }
        });

        return result;
    }
}
