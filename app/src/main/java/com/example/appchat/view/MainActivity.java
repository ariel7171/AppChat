package com.example.appchat.view;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.appchat.databinding.ActivityMainBinding;
import com.example.appchat.util.Validaciones;
import com.example.appchat.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this) .get(MainViewModel.class);
        manejarEventos();
    }

    @Override
    protected void onStart() {
        super.onStart();
      /*  if (viewModel != null) {
            viewModel.verificarSesionActiva().observe(this, si -> {
                if (Boolean.TRUE.equals(si)) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        }*/
    }

    private void manejarEventos() {
        binding.tvRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // TODO: Comento esto para que no se cierre la app cuando se ejecuta finish()
            startActivity(intent);
        });

        binding.btLogin.setOnClickListener(v -> {
            String email = obtenerTextoSeguro(binding.itUsuario);
            String pass = obtenerTextoSeguro(binding.itPassword);

            if (!Validaciones.validarMail(email)) {
                showToast("Email incorrecto");
                return;
            }

            if (!Validaciones.controlarPasword(pass)) {
                showToast("Password incorrecto");
                return;
            }

            viewModel.login(email, pass).observe(this, user_id -> {
                if (user_id != null) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    showToast("Login Exitoso");
                    startActivity(intent);
                } else {
                    showToast("Login fallido");
                }
            });
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        limpiarCampos();
    }

    private void limpiarCampos() {
        if (binding != null) {
            binding.itUsuario.setText("");
            binding.itPassword.setText("");
        }
    }

    /**
     * Obtiene texto de un campo de texto asegurando que no sea nulo.
     */
    private String obtenerTextoSeguro(EditText editText) {
        if (editText == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
}
