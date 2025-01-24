package com.example.estetify;

// Imports do Android
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

// Imports do AndroidX
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports de bibliotecas externas
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import de.hdodenhof.circleimageview.CircleImageView;

// Imports do Firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

// Imports do Java
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsável por exibir os detalhes de uma loja.
 * Mostra informações como nome, avaliação, bio e endereço da loja,
 * além de listar seus produtos e serviços.
 */
public class LojaFragment extends Fragment {
    //region Campos
    // Views
    private CircleImageView storeImage;
    private TextView storeName;
    private RatingBar storeRating;
    private TextView storeBio;
    private TextView storeAddress;
    private MaterialButton btnServices;
    private MaterialButton btnProducts;
    private RecyclerView recyclerView;
    
    // Firebase
    private FirebaseFirestore db;
    
    // Dados
    private String lojaId;
    private String currentFilter = "servicos"; // Pode ser: servicos, produtos
    private ItemAdapter itemAdapter;
    //endregion

    //region Factory
    /**
     * Cria uma nova instância do LojaFragment com o ID da loja especificado
     * @param lojaId O ID da loja a ser exibida
     * @return Uma nova instância do LojaFragment
     */
    public static LojaFragment newInstance(String lojaId) {
        LojaFragment fragment = new LojaFragment();
        fragment.lojaId = lojaId;
        return fragment;
    }
    //endregion

    //region Ciclo de Vida
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Restaurar estado se necessário
        if (savedInstanceState != null) {
            lojaId = savedInstanceState.getString("lojaId");
            currentFilter = savedInstanceState.getString("currentFilter", "servicos");
        }
        
        // Verificar autenticação
        AuthVerification.verificarAutenticacao(this, getParentFragmentManager());
        
        // Configurar botão voltar
        configurarBotaoVoltar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("lojaId", lojaId);
        outState.putString("currentFilter", currentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loja, container, false);
        
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        
        // Inicializar views
        inicializarViews(view);
        
        // Configurar RecyclerView
        configurarRecyclerView();
        
        // Carregar dados da loja
        carregarDadosLoja();
        
        // Configurar listeners
        configurarListeners();
        
        // Atualizar estado dos botões
        atualizarBotoesSelecao();
        
        // Carregar itens iniciais
        carregarItens();

        return view;
    }
    //endregion

    //region Inicialização
    /**
     * Inicializa as referências das views utilizadas no fragment
     */
    private void inicializarViews(View view) {
        storeImage = view.findViewById(R.id.store_image);
        storeName = view.findViewById(R.id.store_name);
        storeRating = view.findViewById(R.id.store_rating);
        storeBio = view.findViewById(R.id.store_bio);
        storeAddress = view.findViewById(R.id.store_address);
        btnServices = view.findViewById(R.id.btn_services);
        btnProducts = view.findViewById(R.id.btn_products);
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    /**
     * Configura o RecyclerView e seu adapter
     */
    private void configurarRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new ItemAdapter(new ArrayList<>(), getParentFragmentManager());
        recyclerView.setAdapter(itemAdapter);
    }

    /**
     * Configura o comportamento do botão voltar do sistema
     */
    private void configurarBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Voltar para ExplorarFragment com o filtro de lojas
                ExplorarFragment explorarFragment = new ExplorarFragment();
                Bundle args = new Bundle();
                args.putString("filtro", "lojas");
                explorarFragment.setArguments(args);
                
                getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, explorarFragment)
                    .commit();
            }
        });
    }
    //endregion

    //region Configuração de Dados
    /**
     * Carrega os dados da loja do Firestore
     */
    private void carregarDadosLoja() {
        db.collection("lojas")
            .document(lojaId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    // Carregar imagem da loja
                    String fotoUrl = document.getString("fotoUrl");
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .into(storeImage);
                    }

                    // Configurar dados da loja
                    storeName.setText(document.getString("nome"));
                    storeBio.setText(document.getString("bio"));
                    storeAddress.setText(document.getString("endereco"));
                    
                    Double avaliacao = document.getDouble("avaliacao");
                    if (avaliacao != null) {
                        storeRating.setRating(avaliacao.floatValue());
                    }
                }
            })
            .addOnFailureListener(e -> 
                Toast.makeText(getContext(), 
                    "Erro ao carregar dados da loja", 
                    Toast.LENGTH_SHORT).show());
    }

    /**
     * Configura os listeners de eventos dos componentes
     */
    private void configurarListeners() {
        btnServices.setOnClickListener(v -> {
            currentFilter = "servicos";
            atualizarBotoesSelecao();
            carregarItens();
        });

        btnProducts.setOnClickListener(v -> {
            currentFilter = "produtos";
            atualizarBotoesSelecao();
            carregarItens();
        });
    }

    /**
     * Atualiza o estado visual dos botões de filtro
     */
    private void atualizarBotoesSelecao() {
        btnServices.setBackgroundTintList(getContext().getColorStateList(
            currentFilter.equals("servicos") ? R.color.azul : R.color.azul_escuro));
        btnProducts.setBackgroundTintList(getContext().getColorStateList(
            currentFilter.equals("produtos") ? R.color.azul : R.color.azul_escuro));
    }

    /**
     * Carrega os itens (produtos ou serviços) da loja do Firestore
     */
    private void carregarItens() {
        db.collection("itens")
            .whereEqualTo("lojaId", lojaId)
            .whereEqualTo("tipo", currentFilter.equals("servicos") ? "Serviço" : "Produto")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Item> itens = new ArrayList<>();
                
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    Item item = new Item(
                        document.getId(),
                        document.getString("titulo"),
                        document.getString("descricao"),
                        document.getString("fotoUrl"),
                        document.getString("tipo"),
                        document.getDouble("preco"),
                        document.getString("lojaId")
                    );
                    itens.add(item);
                }

                itemAdapter.updateItems(itens);
            })
            .addOnFailureListener(e -> 
                Toast.makeText(getContext(), 
                    "Erro ao carregar itens", 
                    Toast.LENGTH_SHORT).show());
    }
    //endregion
}
