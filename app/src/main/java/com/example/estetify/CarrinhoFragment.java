package com.example.estetify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;

public class CarrinhoFragment extends Fragment {
    private ProgressBar loadingCarrinho;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        
        loadingCarrinho = view.findViewById(R.id.loading_carrinho);
        
        // Mostrar loading
        loadingCarrinho.setVisibility(View.VISIBLE);
        
        // Simular carregamento
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingCarrinho.setVisibility(View.GONE);
        }, 1000);

        return view;
    }
}
