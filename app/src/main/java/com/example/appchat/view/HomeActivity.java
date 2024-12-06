package com.example.appchat.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appchat.R;
import com.example.appchat.databinding.ActivityHomeBinding;
import com.example.appchat.view.fragments.ChatsFragment;
import com.example.appchat.view.fragments.FiltrosFragment;
import com.example.appchat.view.fragments.HomeFragment;
import com.example.appchat.view.fragments.PerfilFragment;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //agrego un progress bar carando datos
        LayoutInflater inflater = LayoutInflater.from(this);
        View progressBarLayout = inflater.inflate(R.layout.progress_layout, binding.main, false);
        binding.main.addView(progressBarLayout);
        //

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navItemHome) {
                    openFragment(HomeFragment.newInstance("", ""));
                } else if (item.getItemId() == R.id.navItemFiltros) {
                    openFragment(new FiltrosFragment());
                } else if (item.getItemId() == R.id.navItemCharts) {
                    openFragment(new ChatsFragment());
                } else if (item.getItemId() == R.id.navItemPerfil) {
                    openFragment(new PerfilFragment());
                }
                return true;
            }
        });
        openFragment(HomeFragment.newInstance("", ""));
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();

        new Thread(() -> {
            try {
                // Simula un tiempo de espera de 2 segundos
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // Aquí simula que se han cargado los datos.
                hideProgressBar(); // Oculta el ProgressBar después de que se simula que los datos han sido cargados.
            });
        }).start();
    }

    public void hideProgressBar() {
        View progressBarLayout = findViewById(R.id.progress_layout);
        if (progressBarLayout != null) {
            progressBarLayout.setVisibility(View.GONE);
        }
    }

}