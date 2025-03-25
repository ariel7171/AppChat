package com.example.appchat.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.appchat.R;
import com.example.appchat.adapters.PostAdapter;
import com.example.appchat.databinding.FragmentPerfilBinding;
import com.example.appchat.model.User;
import com.example.appchat.util.ImageUtils;
import com.example.appchat.util.Validaciones;
import com.example.appchat.view.HomeActivity;
import com.example.appchat.view.MainActivity;
import com.example.appchat.view.RegisterActivity;
import com.example.appchat.viewmodel.PostViewModel;
import com.example.appchat.viewmodel.UserViewModel;
import com.squareup.picasso.Picasso;
import com.parse.ParseUser;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.IOException;

public class PerfilFragment extends Fragment {
    private FragmentPerfilBinding binding;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private PostViewModel postViewModel;
    private UserViewModel userViewModel;
    private User currentUser ;
    private LinearLayout layoutActualizarDatos;
    private int cont = 0;

    public PerfilFragment() {
        // Constructor vacío
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        setupMenu();
        setupToolbar();
        displayUserInfo();
        setupGalleryLauncher();
        setupProfileImageClick();
        setupViewModel();
        setupUpdateProfileFields();
        setupTextInputListeners();
        return binding.getRoot();
    }

    // TODO: Agregue este metodo para observar la cantidad de posts y actualizar el TextView con id postCount
    private void setupViewModel() {
        postViewModel = new ViewModelProvider(this).get(PostViewModel.class);

        // Observa la cantidad de posts
        postViewModel.countPosts().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                binding.postCount.setText(count.toString());
            }
        });

        // Configurar RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postViewModel.getPostsByCurrentUser().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null && !posts.isEmpty()) {
                Log.d("PerfilFragment", "Número de posts: " + posts.size());
                PostAdapter adapter = new PostAdapter(posts);
                binding.recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                ((HomeActivity) requireActivity()).hideProgressBar();
            } else {
                Log.d("PerfilFragment", "No hay posts disponibles.");
                ((HomeActivity) requireActivity()).hideProgressBar();
            }
        });


    }

    private void setupMenu() {
        Log.d("PerfilFragment", "setupMenu ejecutado");
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Log.d("PerfilFragment", "onCreateMenu ejecutado");
                menuInflater.inflate(R.menu.close_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.itemClose) {
                    showLogoutConfirmationDialog();
                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void setupToolbar() {
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.getRoot().findViewById(R.id.tools_filtro)); // TODO: Esta linea busca el toolbar con id tools_filtro en el layout de la vista, el toolbar no estaba, lo tuve que agregar.
    }

    // TODO: Cambie este metodo para mostrar la informacion del usuario pero usando el UserViewModel
    private void displayUserInfo() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {

                    currentUser = user;
                    binding.nameUser.setText(user.getUsername());
                    binding.itUsuario.setText(user.getUsername());
                    //binding.emailUser.setText(user.getEmail());
                    binding.itEmail.setText(user.getEmail());
                    binding.insta.setText(user.getRedSocial());
                    binding.itInsta.setText(user.getRedSocial());

                    Log.d("PerfilFragment", "redSocial: " + user.getRedSocial());
                    Log.d("PerfilFragment", "foto_perfil: " + user.getFotoperfil());
                    Log.d("PerfilFragment", "username: " + user.getUsername());
                    //Log.d("PerfilFragment", "email: " + user.getEmail());
                    Log.d("PerfilFragment", "objectId: " + user.getObjectId());

                    String fotoUrl = user.getFotoperfil();
                    if (fotoUrl != null) {
                        Picasso.get()
                                .load(fotoUrl)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.ic_person)
                                .into(binding.circleImageView);
                    } else {
                        binding.circleImageView.setImageResource(R.drawable.ic_person);
                    }
                } else {
                    Toast.makeText(getContext(), "Usuario no logueado", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            handleImageSelection(imageUri);
                        }
                    }
                }
        );
    }

    private void setupProfileImageClick() {
        binding.circleImageView.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ImageUtils.pedirPermisos(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            ImageUtils.openGallery(requireContext(), galleryLauncher);
        });
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            binding.circleImageView.setImageBitmap(bitmap);

            ImageUtils.subirImagenAParse(requireContext(), imageUri, new ImageUtils.ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {

                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null) {
                        currentUser.put("foto_perfil", imageUrl);
                        currentUser.saveInBackground(e -> {
                            if (e == null) {
                                Toast.makeText(requireContext(), "Foto subida correctamente", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Error al guardar la URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(requireContext(), "Error al subir la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.e("PerfilFragment", "Error al manejar la imagen: " + e.getMessage(), e);
            Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // TODO: Metodo agregado para el dialog de cerrar sesion
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
        alert.setTitle("Cerrar Sesión");
        alert.setMessage("¿Estás seguro de que deseas cerrar sesión?");
        alert.setPositiveButton("Sí", (dialog, which) -> {
            logoutUser();
        });
        alert.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });
        alert.show();
    }

    // TODO: Metodo para gestionar las actividades al cerrar las sesion.
    private void logoutUser() {
        ParseUser.logOut();
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Toast.makeText(requireContext(), "Sesion cerrada", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupUpdateProfileFields() {

        layoutActualizarDatos = binding.getRoot().findViewById(R.id.layout_updateUser);

        // Configura el botón "Actualizar Perfil"
        binding.btnUpdatePerfil.setOnClickListener(v -> {
            layoutActualizarDatos.setVisibility(View.VISIBLE);  // Muestra el layout de actualización
            binding.btnUpdatePerfil.setVisibility(View.GONE); // Oculta el botón de actualizar perfil
        });

        // Manejar el botón "Guardar Cambios"
        binding.btnUpdateUser.setOnClickListener(v -> {

            if (updateUser()) {
                layoutActualizarDatos.setVisibility(View.GONE); // Oculta el layout de actualización
                binding.btnUpdatePerfil.setVisibility(View.VISIBLE); // Muestra el botón de actualizar perfil
            }

        });

        // Manejar el botón "Cancelar"
        binding.btnCancel.setOnClickListener(v -> {
            layoutActualizarDatos.setVisibility(View.GONE); // Oculta el layout de actualización
            binding.btnUpdatePerfil.setVisibility(View.VISIBLE); // Muestra el botón de actualizar perfil
        });


    }

    private void updateBtnManager(){
        boolean isChanged = false;

        if (currentUser!=null){
            if (!binding.itUsuario.getText().toString().equals(currentUser.getUsername())) {
                isChanged = true;
            }
            if (!binding.itEmail.getText().toString().equals(currentUser.getEmail())) {
                isChanged = true;
            }
            if (!binding.itInsta.getText().toString().equals(currentUser.getRedSocial())) {
                isChanged = true;
            }
        }

        if (isChanged) {
            binding.btnUpdateUser.setEnabled(true);
            binding.btnUpdateUser.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.primary));
        } else {
            binding.btnUpdateUser.setEnabled(false);
            binding.btnUpdateUser.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorGrayIcon));
        }
    }

    private void setupTextInputListeners() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Puedes dejarlo vacío o manejarlo si lo necesitas
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se ejecuta mientras el input cambia
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Verificar si todos los campos tienen texto después de cualquier cambio
                cont++;
                if (cont > 3){
                    Log.d("PerfilFragment", "textChanged: ");
                    updateBtnManager();
                }
            }
        };

        // Agrega el TextWatcher a cada uno de los EditTexts
        binding.itUsuario.addTextChangedListener(textWatcher);
        binding.itEmail.addTextChangedListener(textWatcher);
        binding.itInsta.addTextChangedListener(textWatcher);
    }

    private boolean updateUser(){

            String usuario = binding.itUsuario.getText().toString().trim();
            String email = binding.itEmail.getText().toString().trim();
            String insta = binding.itInsta.getText().toString().trim();

            // Validaciones de entrada
            if (!Validaciones.validarTexto(usuario)) {
                showToast("Usuario incorrecto");
                return false;
            }
            if (!Validaciones.validarMail(email)) {
                showToast("El correo no es válido");
                return false;
            }

            if (!Validaciones.validarTexto(insta)) {
                showToast("Instagram incorrecto");
                return false;
            }

            if (currentUser!=null) {
                User user = currentUser;
                user.setEmail(email);
                user.setUsername(usuario);
                user.setRedSocial(insta);
                Log.d("PerfilFragment", "Usuario actualizado: " + usuario + ", Email: " + email+" insta: "+insta);
                userViewModel.updateUser(user).observe(getViewLifecycleOwner(),updateResult ->{
                    if (updateResult) {
                        showToast("Actualizacion exitosa");
                        displayUserInfo();
                    } else {
                        showToast("Actualizacion fallida");
                    }
                });
            }
            return true;
    }

    private void showToast(String message) {
        if (message != null) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

}
