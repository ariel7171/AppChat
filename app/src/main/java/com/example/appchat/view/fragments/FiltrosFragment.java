package com.example.appchat.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appchat.R;
import com.example.appchat.adapters.PostAdapter;
import com.example.appchat.databinding.FragmentFiltrosBinding;
import com.example.appchat.model.Post;
import com.example.appchat.view.MainActivity;
import com.example.appchat.viewmodel.AuthViewModel;
import com.example.appchat.viewmodel.PostViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FiltrosFragment extends Fragment {
    private FragmentFiltrosBinding binding;
    private PostViewModel postViewModel; // ViewModel para los posts
    private AuthViewModel authViewModel; // ViewModel para la autenticación
    private Spinner spinnerFiltro; // Spinner para seleccionar el filtro
    private String filtroSeleccionado = "Título"; // Filtro seleccionado por defecto
    private static final long DEBOUNCE_DELAY = 1000; // 500 milisegundos
    private Handler handler = new Handler();
    private View progressBarLayout;


    public FiltrosFragment() {
        // Constructor vacío requerido
    }

    public static FiltrosFragment newInstance() {
        return new FiltrosFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializar ViewModels
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout del fragmento usando ViewBinding
        binding = FragmentFiltrosBinding.inflate(inflater, container, false);

        // Agregar el ProgressBar al layout del fragmento
        progressBarLayout = inflater.inflate(R.layout.progress_layout, binding.getRoot(), false);
        binding.linear2.addView(progressBarLayout);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar la barra de herramientas
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.tools);

        // Configurar RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hideProgressBar();
        setupSpinner();
        setupSearchView();
        setupMenu();
    }

    private void setupSpinner() {
        spinnerFiltro = binding.spinnerFiltro;

        // Crear un ArrayAdapter usando un array de strings y un layout por defecto para el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.opciones_filtro, // Array de opciones definido en res/values/strings.xml
                android.R.layout.simple_spinner_item // Layout por defecto
        );

        // Especificar el layout que se usará cuando se despliegue la lista de opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerFiltro.setAdapter(adapter);

        // Manejar la selección del Spinner
        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el filtro seleccionado
                filtroSeleccionado = parent.getItemAtPosition(position).toString();
                Log.d("FiltrosFragment", "Filtro seleccionado: " + filtroSeleccionado);

                // Si hay un texto en el SearchView aplicar el filtro
                String query = binding.searchView.getQuery().toString();
                if (!query.isEmpty()) {
                    getAllFilterPost(query);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });
    }

    private void getAllFilterPost(String texto) {
        if (texto.trim().isEmpty()) {
            binding.filtrosText.setText("Ingrese un palabra para filtrar");
            binding.filtrosText.setVisibility(View.VISIBLE);
            return;
        }
        binding.filtrosText.setVisibility(View.GONE);
        showProgressBar();


        // Observar los posts desde el ViewModel
        postViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            boolean flag = false;
            if (posts != null && !posts.isEmpty()) {
                // Filtrar los posts segun el texto y la categoria seleccionada
                List<Post> postsFiltrados = posts.stream()
                        .filter(post -> contieneTexto(texto, post, filtroSeleccionado))
                        .collect(Collectors.toList());

                // Actualizar el RecyclerView con los posts filtrados
                PostAdapter adapter = new PostAdapter(postsFiltrados);
                binding.recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (postsFiltrados.isEmpty()){
                    flag = true;
                }

            } else {
                Log.d("FiltrosFragment", "No hay posts disponibles.");
            }
            new Handler().postDelayed(() -> hideProgressBar(), 1000);

            if(flag){
                binding.filtrosText.setText("No hay posts disponibles.");
                binding.filtrosText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupMenu() {
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Inflar el menu
                menuInflater.inflate(R.menu.main_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Manejar la selección del ítem del menú
                if (menuItem.getItemId() == R.id.itemLogout) {
                    onLogout();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private Runnable filterRunnable = new Runnable() {
        @Override
        public void run() {
            // Aquí ejecutas la función de filtrado
            String query = binding.searchView.getQuery().toString();

            getAllFilterPost(query);
        }
    };


    private void setupSearchView() {
        // Acceder al SearchView directamente desde el binding
        SearchView searchView = binding.searchView;
        if (searchView == null) {
            Log.e("FiltrosFragment", "SearchView no encontrado en el layout.");
        } else {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    handler.removeCallbacks(filterRunnable); // Cancelar cualquier retraso pendiente
                    getAllFilterPost(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Retrasar la ejecucion hasta que el usuario deje de escribir
                    handler.removeCallbacks(filterRunnable); // Cancelar cualquier retraso pendiente
                    handler.postDelayed(filterRunnable, DEBOUNCE_DELAY); // Programar la ejecución
                    return true;
                }
            });
        }
    }

    private void onLogout() {
        // Manejar el cierre de sesion
        authViewModel.logout().observe(getViewLifecycleOwner(), logoutResult -> {
            if (logoutResult != null && logoutResult) {
                // Navegar a MainActivity y limpiar la pila de actividades
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Error al cerrar sesión. Intenta nuevamente.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(filterRunnable); // Cancelar cualquier retraso pendiente
        // Limpiar el binding para evitar fugas de memoria
        binding = null;
    }

    private boolean contieneTexto(String texto, Post post, String filtro) {

        texto = texto.toLowerCase();

        // Filtrar segun la categoria seleccionada
        switch (filtro) {
            case "Título":
                return post.getTitulo() != null && post.getTitulo().toLowerCase().contains(texto);
            case "Usuario":
                return post.getUser().getUsername() != null && post.getUser().getUsername().toLowerCase().contains(texto);
            case "Descripción":
                return post.getDescripcion() != null && post.getDescripcion().toLowerCase().contains(texto);
            case "Categoría":
                return post.getCategoria() != null && post.getCategoria().toLowerCase().contains(texto);
            default:
                return false;
        }
    }

    public void showProgressBar() {
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
    }

}