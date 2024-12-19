package com.example.appchat.view;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.appchat.R;
import com.example.appchat.adapters.ComentarioAdapter;
import com.example.appchat.adapters.EfectoTransformer;
import com.example.appchat.adapters.ImageSliderAdapter;
import com.example.appchat.databinding.ActivityPostDetailBinding;
import com.example.appchat.model.Comentario;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.example.appchat.providers.ComentarioProvider;
import com.example.appchat.viewmodel.ComentarioViewModel;
import com.example.appchat.viewmodel.PostDetailViewModel;
import com.example.appchat.viewmodel.PostViewModel;
import com.example.appchat.viewmodel.UserViewModel;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {
    private ActivityPostDetailBinding binding;
    private PostDetailViewModel viewModel;
    private String postId;
    private ComentarioViewModel comentarioViewModel;
    private PostViewModel postViewModel;
    private RecyclerView recyclerViewComentarios;
    private ComentarioAdapter comentarioAdapter;
    private List<Comentario> comentarios;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postViewModel = new PostViewModel();
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(PostDetailViewModel.class);
        postId=getIntent().getStringExtra("idPost"); //"idPost"
        detailInfo();
        setupObservers();
        /*
        if (postId != null) {
            viewModel.fetchComments(postId);
        }
        */

        binding.fabComentar.setOnClickListener(v -> showDialogComment());

        recyclerViewComentarios = findViewById(R.id.recyclerViewComentarios);
        recyclerViewComentarios.setLayoutManager(new LinearLayoutManager(this));

        // Obtener el ID del post que fue clickeado
        String postId = getIntent().getStringExtra("idPost");

        // Inicializa la lista de comentarios
        comentarios = new ArrayList<>();
        comentarioAdapter = new ComentarioAdapter(comentarios);
        recyclerViewComentarios.setAdapter(comentarioAdapter);

        // Cargar comentarios desde el proveedor
        loadComments(postId);
    }

    private void loadComments(String postId) {
        comentarioViewModel = new ViewModelProvider(this).get(ComentarioViewModel.class);
        comentarioViewModel.getCommentsByPost(postId).observe(this, comentarios -> {
            // Actualiza la lista de comentarios en el adaptador
            if (comentarios != null) {
                this.comentarios.clear();
                this.comentarios.addAll(comentarios);
                comentarioAdapter.notifyDataSetChanged(); // Notifica al adaptador que se han actualizado los datos
            } else {
                Log.d("PostDetailActivity", "No comentarios disponibles.");
            }

        });
    }

    private void showComments(){
        comentarioViewModel = new ViewModelProvider(this).get(ComentarioViewModel.class);
        Log.d("PostDetailActivity", "postID: " + postId);
        if (postId != null) {
            Log.d("PostDetailActivity", "postID no es nulo");
            comentarioViewModel.getCommentsByPost(postId).observe(this, comentarios -> {
                if (comentarios != null) {
                    // Iterar a través de la lista de comentarios y registrarlos
                    for (Comentario comentario : comentarios) {
                        Log.d("PostDetailActivity", "Comentario: " + comentario.getTexto());
                    }
                } else {
                    Log.d("PostDetailActivity", "No comentarios disponibles.");
                }
            });
        }


    }


    private void showDialogComment() {
        AlertDialog.Builder alert = new AlertDialog.Builder(PostDetailActivity.this);
        alert.setTitle("¡COMENTARIO!");
        alert.setMessage("Ingresa tu comentario: ");

        EditText editText = new EditText(PostDetailActivity.this);
        editText.setHint("Texto");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        editText.setLayoutParams(params);
        params.setMargins(36, 0, 36, 36);


        RelativeLayout container = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        container.setLayoutParams(relativeParams);
        container.addView(editText);
        alert.setView(container);

        alert.setPositiveButton("Ok", (dialog, which) -> {
            String value = editText.getText().toString().trim();
            if (!value.isEmpty()) {

                postViewModel.getPostById2(postId).observe(this, new Observer<Post>() {
                    @Override
                    public void onChanged(Post post) {
                        if (post!=null) {
                            Comentario comentario = new Comentario();
                            comentario.setTexto(value);
                            comentario.setPost(post);
                            comentario.setUser((User) ParseUser.getCurrentUser());
                            comentarioViewModel.saveComment(comentario);
                            Toast.makeText(PostDetailActivity.this, "Comentario guardado correctamente", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            //showComments(); // Refresca los comentarios después de guardar uno nuevo
                        }
                    }
                });

            } else {
                Toast.makeText(PostDetailActivity.this, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });

        alert.show();
    }

    private void setupObservers() {
        viewModel.getCommentsLiveData().observe(this, comments -> {

            // updateUI(comments);

        });


        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void detailInfo() {

        binding.nameUser.setText(getIntent().getStringExtra("username"));
        binding.emailUser.setText(getIntent().getStringExtra("email"));
        binding.insta.setText(getIntent().getStringExtra("redsocial"));

        String fotoUrl = getIntent().getStringExtra("foto_perfil");
        if (fotoUrl != null) {
            Picasso.get()
                    .load(fotoUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .into(binding.circleImageView);
        } else {
            binding.circleImageView.setImageResource(R.drawable.ic_person);
        }

        ArrayList<String> urls = getIntent().getStringArrayListExtra("imagenes");
        String titulo = "Lugar: " + getIntent().getStringExtra("titulo");
        binding.lugar.setText(titulo);
        String categoria = "Categoria: " + getIntent().getStringExtra("categoria");
        binding.categoria.setText(categoria);
        String comentario = "descripción: " + getIntent().getStringExtra("descripcion");
        binding.description.setText(comentario);
        String duracion = "Duración: " + getIntent().getIntExtra("duracion", 0) + " día/s";
        binding.duracion.setText(duracion);
        String presupuesto = "Presupuesto: U$ " + getIntent().getDoubleExtra("presupuesto", 0.0);
        binding.presupuesto.setText(presupuesto);

        if (urls != null && !urls.isEmpty()) {
            ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(urls);
            binding.viewPager.setAdapter(imageSliderAdapter);
            binding.viewPager.setPageTransformer(new EfectoTransformer());

            // Conexión TabLayout con ViewPager2
            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            }).attach();
        }
    }
}


