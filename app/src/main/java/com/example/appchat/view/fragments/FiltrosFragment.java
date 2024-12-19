package com.example.appchat.view.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.appchat.R;
import androidx.fragment.app.Fragment;

public class FiltrosFragment extends Fragment {

    public FiltrosFragment() {

    }

    public static FiltrosFragment newInstance(String param1, String param2) {
        return new FiltrosFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtros, container, false);
    }
}
