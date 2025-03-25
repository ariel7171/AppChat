package com.example.appchat.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appchat.R;
import com.example.appchat.adapters.ComentarioAdapter;
import com.example.appchat.adapters.ImageSliderAdapter;
import com.example.appchat.databinding.ActivityPostDetailBinding;
import com.example.appchat.model.Comentario;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.example.appchat.viewmodel.ComentarioViewModel;
import com.example.appchat.viewmodel.PostViewModel;
import com.example.appchat.viewmodel.UserViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    private PostViewModel postViewModel;
    private ComentarioViewModel comentarioViewModel;
    private UserViewModel userViewModel;
    private String postId;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar ViewModels
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        comentarioViewModel = new ViewModelProvider(this).get(ComentarioViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        // Obtener ID del post
        postId = getIntent().getStringExtra("idPost");
        if (postId == null) {
            Toast.makeText(this, "Error: Post no disponible", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        postViewModel.getPostById(postId).observe(this, postSeleccionado -> {
            if (postSeleccionado != null) {
                post = postSeleccionado;
                setupObservers();
                setupUI();
                setupListeners();
            } else {
                Toast.makeText(this, "Error: Post no disponible", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void setupObservers() {
        // Observar datos del post
        updatePostUI(post);
        loadComments(); // Cargar comentarios después de obtener el post

        // Observar errores
        postViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupUI() {
        // Configurar RecyclerView
        binding.recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewComentarios.setAdapter(new ComentarioAdapter(new ArrayList<>(), (User) ParseUser.getCurrentUser(), this));
        /*
        // Mostrar botón de eliminar solo si es el autor
        userViewModel.getCurrentUser().observe(this, currentUser -> {
            if (currentUser != null && currentUser.getId().equals(post.getUser().getId())) {
                binding.btnEliminarPost.setVisibility(View.VISIBLE);
            }
        });
        */
        String userId = ((User) ParseUser.getCurrentUser()).getId();
        String postUserId = post.getUser().getId();
        if (userId!=null && userId.equals(postUserId)){
            binding.btnEliminarPost.setVisibility(View.VISIBLE);
        }

    }

    private void updatePostUI(Post post) {
        // Actualizar datos del post
        binding.lugar.setText("Lugar: " + post.getTitulo());
        binding.categoria.setText("Categoría: " + post.getCategoria());
        binding.description.setText("Descripción: " + post.getDescripcion());
        binding.duracion.setText("Duración: " + post.getDuracion() + " día/s");
        binding.presupuesto.setText("Presupuesto: U$ " + post.getPresupuesto());

        // Cargar imágenes
        if (post.getImagenes() != null && !post.getImagenes().isEmpty()) {
            binding.viewPager.setAdapter(new ImageSliderAdapter(post.getImagenes()));
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            }).attach();
        }

        // Cargar datos del usuario de forma segura
        post.getUser().fetchIfNeededInBackground((parseUser, e) -> {
            if (e == null) {
                User user = (User) parseUser;
                runOnUiThread(() -> {
                    binding.nameUser.setText(user.getUsername());
                    binding.insta.setText(user.getRedSocial());

                    if (user.getString("foto_perfil") != null) {
                        Picasso.get()
                                .load(user.getString("foto_perfil"))
                                .into(binding.circleImageView);
                    }
                });
            } else {
                Log.e("UserFetch", "Error al cargar el usuario: " + e.getMessage());
            }
        });
    }

    private void loadComments() {
        comentarioViewModel.getCommentsByPost(postId).observe(this, comentarios -> {
            if (comentarios != null) {
                ((ComentarioAdapter) binding.recyclerViewComentarios.getAdapter()).updateComments(comentarios);
            }
        });
    }

    private void setupListeners() {
        // Botón de comentar
        binding.fabComentar.setOnClickListener(v -> showDialogComment());

        // Botón de eliminar
        binding.btnEliminarPost.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de eliminar este post?")
                .setPositiveButton("Eliminar", (d, w) -> eliminarPost())
                .setNegativeButton("Cancelar", null)
                .show());
    }

    private void eliminarPost(){
        postViewModel.eliminarPost(postId);

        postViewModel.getPostSuccess().observe(this, message -> {
            if (message != null && message.equals("Post eliminado correctamente")) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish(); // Cierra la actividad actual
            }
        });
    }

    private void showDialogComment() {
        EditText input = new EditText(this);
        input.setHint("Escribe tu comentario...");

        new AlertDialog.Builder(this)
                .setTitle("Nuevo comentario")
                .setView(input)
                .setPositiveButton("Publicar", (d, w) -> {
                    String texto = input.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        Comentario comentario = new Comentario();
                        comentario.setTexto(texto);
                        comentario.setPost(ParseObject.createWithoutData(Post.class, postId));
                        comentario.setUser((User) ParseUser.getCurrentUser());
                        comentarioViewModel.saveComment(comentario).observe(this, result -> {
                            if (result != null && result.contains("exito")) {
                                comentarioViewModel.getCommentsByPost(postId);
                                loadComments();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}