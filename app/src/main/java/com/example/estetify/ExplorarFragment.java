package com.example.estetify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;

public class ExplorarFragment extends Fragment {
    private ProgressBar loadingExplorar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorar, container, false);
        
        loadingExplorar = view.findViewById(R.id.loading_explorar);
        
        // Mostrar loading
        loadingExplorar.setVisibility(View.VISIBLE);
        
        // Simular carregamento
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingExplorar.setVisibility(View.GONE);
        }, 1000);

        return view;
    }
}
