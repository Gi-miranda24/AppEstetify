package com.example.estetify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ExplorarFragment extends Fragment {
    private ProgressBar loadingExplorar;
    private EditText searchField;
    private MaterialButton btnServices;
    private MaterialButton btnProducts;
    private MaterialButton btnStores;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private String currentFilter = "servicos"; // Pode ser: servicos, produtos, lojas
    private ItemAdapter itemAdapter;
    private LojaAdapter lojaAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verificar se há um filtro nos argumentos
        if (getArguments() != null && getArguments().containsKey("filtro")) {
            currentFilter = getArguments().getString("filtro");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explorar, container, false);
        
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        
        // Inicializar views
        inicializarViews(view);
        
        // Mostrar loading
        loadingExplorar.setVisibility(View.VISIBLE);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Inicializar adapters
        itemAdapter = new ItemAdapter(new ArrayList<>(), getParentFragmentManager());
        lojaAdapter = new LojaAdapter(new ArrayList<>(), getParentFragmentManager());
        
        // Configurar listeners
        configurarListeners();
        
        // Atualizar estado dos botões
        atualizarBotoesSelecao();
        
        // Carregar dados iniciais (serviços)
        carregarDados();

        return view;
    }

    private void inicializarViews(View view) {
        loadingExplorar = view.findViewById(R.id.loading_explorar);
        searchField = view.findViewById(R.id.search_field);
        btnServices = view.findViewById(R.id.btn_services);
        btnProducts = view.findViewById(R.id.btn_products);
        btnStores = view.findViewById(R.id.btn_stores);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void configurarListeners() {
        // Configurar botões de filtro
        btnServices.setOnClickListener(v -> {
            currentFilter = "servicos";
            atualizarBotoesSelecao();
            carregarDados();
        });

        btnProducts.setOnClickListener(v -> {
            currentFilter = "produtos";
            atualizarBotoesSelecao();
            carregarDados();
        });

        btnStores.setOnClickListener(v -> {
            currentFilter = "lojas";
            atualizarBotoesSelecao();
            carregarDados();
        });

        // Configurar busca
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Realizar busca após o usuário digitar
                carregarDados();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void atualizarBotoesSelecao() {
        btnServices.setBackgroundTintList(getContext().getColorStateList(
            currentFilter.equals("servicos") ? R.color.azul : R.color.azul_escuro));
        btnProducts.setBackgroundTintList(getContext().getColorStateList(
            currentFilter.equals("produtos") ? R.color.azul : R.color.azul_escuro));
        btnStores.setBackgroundTintList(getContext().getColorStateList(
            currentFilter.equals("lojas") ? R.color.azul : R.color.azul_escuro));
    }

    private void carregarDados() {
        loadingExplorar.setVisibility(View.VISIBLE);
        String searchText = searchField.getText().toString().toLowerCase();

        if (currentFilter.equals("lojas")) {
            carregarLojas(searchText);
        } else {
            carregarItens(searchText);
        }
    }

    private void carregarLojas(String searchText) {
        db.collection("lojas")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Loja> lojas = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String nome = document.getString("nome");
                    if (searchText.isEmpty() || nome.toLowerCase().contains(searchText)) {
                        Loja loja = new Loja(
                            document.getId(),
                            nome,
                            document.getString("bio"),
                            document.getString("endereco"),
                            document.getString("fotoUrl"),
                            document.getDouble("avaliacao")
                        );
                        lojas.add(loja);
                    }
                }

                recyclerView.setAdapter(lojaAdapter);
                lojaAdapter.updateLojas(lojas);
                loadingExplorar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Erro ao carregar lojas", Toast.LENGTH_SHORT).show();
                loadingExplorar.setVisibility(View.GONE);
            });
    }

    private void carregarItens(String searchText) {
        db.collection("itens")
            .whereEqualTo("tipo", currentFilter.equals("servicos") ? "Serviço" : "Produto")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Item> itens = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    String titulo = document.getString("titulo");
                    if (searchText.isEmpty() || titulo.toLowerCase().contains(searchText)) {
                        Item item = new Item(
                            document.getId(),
                            titulo,
                            document.getString("descricao"),
                            document.getString("fotoUrl"),
                            document.getString("tipo"),
                            document.getDouble("preco"),
                            document.getString("lojaId")
                        );
                        itens.add(item);
                    }
                }

                recyclerView.setAdapter(itemAdapter);
                itemAdapter.updateItems(itens);
                loadingExplorar.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Erro ao carregar itens", Toast.LENGTH_SHORT).show();
                loadingExplorar.setVisibility(View.GONE);
            });
    }
}
