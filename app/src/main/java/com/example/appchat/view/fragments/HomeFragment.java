package com.example.appchat.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appchat.R;
import com.example.appchat.adapters.PostAdapter;
import com.example.appchat.databinding.FragmentHomeBinding;
import com.example.appchat.providers.AuthProvider;
import com.example.appchat.view.MainActivity;
import com.example.appchat.view.PostActivity;
import com.example.appchat.viewmodel.AuthViewModel;
import com.example.appchat.viewmodel.PostViewModel;
import com.example.appchat.model.Post;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PostViewModel postViewModel;
    private List<Post> postList;
    private PostAdapter adapter;

    public HomeFragment() {}

    public static HomeFragment newInstance(String p1, String p2) {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa el ViewModel
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        // Configura el RecyclerView
        postList = new ArrayList<>();
        adapter = new PostAdapter(postList); // Asegúrate de tener un adaptador implementado
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        // Cargar posts
        cargarPosts();

        // Configurar el botón para añadir nuevos posts
        binding.btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PostActivity.class);
            startActivity(intent);
        });
    }

    private void cargarPosts() {
        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            postList.clear();
            postList.addAll(posts);
            adapter.notifyDataSetChanged(); // Notificar al adaptador tras recibir los datos
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onLogout() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    // Comenta o elimina si no vas a usar ese método
    public void setupMenu() {
        binding.btnAdd.setOnClickListener(v -> onLogout());
    }
}