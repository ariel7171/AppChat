package com.example.appchat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.R;
import com.example.appchat.model.Comentario;
import com.example.appchat.model.User;
import com.example.appchat.providers.LikeProvider;
import com.example.appchat.viewmodel.ComentarioViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {

    private List<Comentario> comentarios;
    private LikeProvider likeProvider;
    private User currentUser;
    private Context context;
    // Mapas para almacenar el estado de cada comentario individualmente
    private Map<String, String> userReactionMap = new HashMap<>();
    private Map<String, int[]> reactionCountsMap = new HashMap<>();

    public ComentarioAdapter(List<Comentario> comentarios, User currentUser, Context context) {
        this.comentarios = comentarios;
        this.currentUser = currentUser;
        this.context = context;
        this.comentarios = comentarios;
        this.likeProvider = new LikeProvider();

        // No llamamos a preloadReactions aquí porque es mejor hacerlo
        // cuando los ViewHolders se crean/vinculan
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        String comentarioId = comentario.getObjectId();

        holder.tvComentario.setText(comentario.getTexto());

        if (comentario.getUser() != null) {
            holder.tvUsuario.setText(comentario.getUser().getUsername());

            // Comprueba si el usuario actual es el creador del comentario
            if (currentUser != null && currentUser.getObjectId().equals(comentario.getUser().getObjectId())) {
                // Mostrar el botón de eliminar
                holder.tvDelete.setVisibility(View.VISIBLE);

                // Configurar el onClickListener para eliminar el comentario
                holder.tvDelete.setOnClickListener(v -> {
                    // Aquí implementar la lógica para eliminar el comentario
                    eliminarComentario(comentario, position);
                });
            } else {
                // Ocultar el botón de eliminar para otros usuarios
                holder.tvDelete.setVisibility(View.GONE);
            }
        }

        // Cargar datos inmediatamente si no están en el mapa
        if (!reactionCountsMap.containsKey(comentarioId)) {
            // Mostrar valores por defecto mientras se carga
            holder.tvLikesCount.setText("...");
            holder.tvDislikesCount.setText("...");

            // Cargar contadores para este comentario específico
            loadReactionCounts(comentarioId, holder);
        } else {
            // Usar datos existentes en el mapa
            updateCountUI(holder, comentarioId);
        }

        // Cargar reacción del usuario actual si no está en el mapa
        if (!userReactionMap.containsKey(comentarioId)) {
            // Restablecer colores por defecto
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.btnDislike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));

            // Cargar reacción
            loadUserReaction(comentarioId, holder);
        } else {
            // Usar datos existentes en el mapa
            updateLikeUI(holder, comentarioId);
        }

        // Configurar click listeners para los botones de este comentario específico
        holder.btnLike.setOnClickListener(v -> {
            likeProvider.toggleReaction(comentarioId, currentUser.getObjectId(), "like").observeForever(result -> {
                // Actualizar datos locales después de la reacción
                refreshReactionData(comentarioId, holder);
            });
        });

        holder.btnDislike.setOnClickListener(v -> {
            likeProvider.toggleReaction(comentarioId, currentUser.getObjectId(), "dislike").observeForever(result -> {
                // Actualizar datos locales después de la reacción
                refreshReactionData(comentarioId, holder);
            });
        });
    }

    private void loadReactionCounts(String comentarioId, ComentarioViewHolder holder) {
        likeProvider.countReactions(comentarioId).observeForever(counts -> {
            reactionCountsMap.put(comentarioId, counts);
            // Actualizar UI directamente para este ViewHolder específico
            updateCountUI(holder, comentarioId);
        });
    }

    private void loadUserReaction(String comentarioId, ComentarioViewHolder holder) {
        likeProvider.getUserReaction(comentarioId, currentUser.getObjectId()).observeForever(reaction -> {
            userReactionMap.put(comentarioId, reaction);
            // Actualizar UI directamente para este ViewHolder específico
            updateLikeUI(holder, comentarioId);
        });
    }

    private void refreshReactionData(String comentarioId, ComentarioViewHolder holder) {
        // Recargar la reacción del usuario para este comentario específico
        likeProvider.getUserReaction(comentarioId, currentUser.getObjectId()).observeForever(reaction -> {
            userReactionMap.put(comentarioId, reaction);
            updateLikeUI(holder, comentarioId);
        });

        // Recargar los contadores para este comentario específico
        likeProvider.countReactions(comentarioId).observeForever(counts -> {
            reactionCountsMap.put(comentarioId, counts);
            updateCountUI(holder, comentarioId);
        });
    }

    private void updateLikeUI(ComentarioViewHolder holder, String comentarioId) {
        String reaction = userReactionMap.get(comentarioId);

        if ("like".equals(reaction)) {
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.lavender));
            holder.btnDislike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
        } else if ("dislike".equals(reaction)) {
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.btnDislike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.lavender));
        } else {
            holder.btnLike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
            holder.btnDislike.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), R.color.gray));
        }
    }

    private void updateCountUI(ComentarioViewHolder holder, String comentarioId) {
        int[] counts = reactionCountsMap.get(comentarioId);
        if (counts != null) {
            holder.tvLikesCount.setText(String.valueOf(counts[0]));
            holder.tvDislikesCount.setText(String.valueOf(counts[1]));
        } else {
            holder.tvLikesCount.setText("0");
            holder.tvDislikesCount.setText("0");
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    // Un método para actualizar la lista de comentarios
    public void updateComments(List<Comentario> newComments) {
        this.comentarios = newComments;
        notifyDataSetChanged();
    }

    private void eliminarComentario(Comentario comentario, int position) {
        // Crear alerta de confirmación
        new AlertDialog.Builder(context)
                .setTitle("Eliminar comentario")
                .setMessage("¿Estás seguro que deseas eliminar este comentario?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    // Si el usuario confirma, proceder con la eliminación
                    ComentarioViewModel comentarioViewModel = new ComentarioViewModel();

                    comentarioViewModel.deleteComment(comentario.getObjectId()).observeForever(result -> {
                        if (result != null && result.equals("Comentario eliminado exitosamente")) {
                            comentarios.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, comentarios.size());
                        }
                    });
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    // Si cancela, simplemente cerrar el diálogo
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    public static class ComentarioViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsuario, tvComentario, tvLikesCount, tvDislikesCount, tvDelete;
        ImageButton btnLike, btnDislike;

        public ComentarioViewHolder(View itemView) {
            super(itemView);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvComentario = itemView.findViewById(R.id.tvComentario);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvDislikesCount = itemView.findViewById(R.id.tvDislikesCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            tvDelete = itemView.findViewById(R.id.tvDelete);
        }
    }
}