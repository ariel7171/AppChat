package com.example.appchat.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.appchat.R;
import com.example.appchat.adapters.ImageAdapter;
import com.example.appchat.databinding.ActivityPostBinding;
import com.example.appchat.model.Post;
import com.example.appchat.util.ImageUtils;
import com.example.appchat.util.Validaciones;
import com.example.appchat.viewmodel.PostViewModel;

import java.util.ArrayList;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private static final int MAX_IMAGES = 3;
    private static final int REQUEST_IMAGE = 1;

    private ActivityPostBinding binding;
    private PostViewModel postViewModel;
    private final List<String> imagenesUrls = new ArrayList<>();
    private ImageAdapter adapter;
    private String categoria;

    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecycLerView();
        setupViewModels();
        setupCategorySpinner();
        setupGalleryLauncher();

        binding.btnPublicar.setOnClickListener(v -> publicarPost());
    }

    private void setupRecycLerView() {
        adapter = new ImageAdapter(imagenesUrls, this);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        binding.recyclerView.setAdapter(adapter);
        updateRecyclerViewVisibility();
    }

    private void setupViewModels() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        postViewModel.getPostSuccess().observe(this, success -> {
            String message = success ? "Post publicado con éxito" : "Error al publicar";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (success) finish();
        });
    }

    private void setupCategorySpinner() {
        // Implementa la configuración del spinner de categorías aquí
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, getResources().getStringArray(R.array.categorias_array)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(adapter);

        binding.spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoria = null;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null && imagenesUrls.size() < MAX_IMAGES) {
                            ImageUtils.subirImagenAParse(PostActivity.this, imageUri, new ImageUtils.ImageUploadCallback() {
                                @Override
                                public void onSuccess(String imageUrl) {
                                    Log.d("PostActivity", "Imagen subida con éxito: " + imageUrl);
                                    imagenesUrls.add(imageUrl);
                                    adapter.notifyDataSetChanged();
                                    updateRecyclerViewVisibility();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("PostActivity", "Error al subir la imagen", e);
                                    Toast.makeText(PostActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else if (imagenesUrls.size() >= MAX_IMAGES) {
                            Toast.makeText(PostActivity.this, "Máximo de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        binding.uploadImage.setOnClickListener(v -> {
            Log.d("PostActivity", "Botón clicado");
            ImageUtils.pedirPermisos(PostActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_IMAGE);
            ImageUtils.openGallery(PostActivity.this, galleryLauncher);
            Log.d("PostActivity", "galeria abierta");
        });
    }

    private void publicarPost() { // usage mariel
        String titulo = binding.itTitulo.getText().toString().trim();
        String descripcion = binding.etDescripcion.getText().toString().trim();
        String duracionStr = binding.etDuracion.getText().toString().trim();
        String presupuestoStr = binding.etPresupuesto.getText().toString().trim();

        // Validar campos
        if (!Validaciones.validarTexto(titulo)) {
            binding.itTitulo.setError("El título no es válido");
            return;
        }
        if (!Validaciones.validarTexto(descripcion)) {
            binding.etDescripcion.setError("La descripción no es válida");
            return;
        }
        int duracion = Validaciones.validarNumero(duracionStr);
        if (duracion == -1) {
            binding.etDuracion.setError("Duración no válida");
            return;
        }

        double presupuesto;
        try {
            presupuesto = Double.parseDouble(presupuestoStr);
        } catch (NumberFormatException e) {
            binding.etPresupuesto.setError("Presupuesto no válido");
            return;
        }

        Post post = new Post(titulo, descripcion, duracion, categoria, presupuesto, new ArrayList<>(imagenesUrls));
        postViewModel.publicar(post);
    }

    private void updateRecyclerViewVisibility() { // 2 usages mariel
        boolean hasImages = !imagenesUrls.isEmpty();
        binding.recyclerView.setVisibility(hasImages ? View.VISIBLE : View.GONE);
        binding.uploadImage.setVisibility(imagenesUrls.size() < MAX_IMAGES ? View.VISIBLE : View.GONE);
    }

}//llave