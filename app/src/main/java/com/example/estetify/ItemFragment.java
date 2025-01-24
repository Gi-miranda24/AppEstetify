package com.example.estetify;

// Imports do Android
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

// Imports do AndroidX
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

// Imports de bibliotecas externas
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

// Imports do Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment responsável por exibir os detalhes de um item (produto ou serviço).
 * Permite visualizar informações como título, descrição, preço e loja, além de
 * adicionar o item ao carrinho.
 */
public class ItemFragment extends Fragment {
    //region Campos
    // Views
    private ImageView itemImage;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemPrice;
    private Button btnAddToCart;
    private CircleImageView storeImage;
    private TextView storeName;
    private View storeContainer;
    
    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Dados
    private Item item;
    //endregion

    //region Factory
    /**
     * Cria uma nova instância do ItemFragment com o item especificado
     * @param item O item a ser exibido
     * @return Uma nova instância do ItemFragment
     */
    public static ItemFragment newInstance(Item item) {
        ItemFragment fragment = new ItemFragment();
        fragment.item = item;
        return fragment;
    }
    //endregion

    //region Ciclo de Vida
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Restaurar estado se necessário
        if (savedInstanceState != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                item = savedInstanceState.getSerializable("item", Item.class);
            } else {
                item = (Item) savedInstanceState.getSerializable("item");
            }
        }
        
        // Verificar autenticação
        AuthVerification.verificarAutenticacao(this, getParentFragmentManager());
        
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Configurar botão voltar
        configurarBotaoVoltar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (item != null) {
            outState.putSerializable("item", item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        
        // Inicializar views
        inicializarViews(view);
        
        // Configurar dados do item
        configurarDadosItem();
        
        // Configurar listeners
        configurarListeners();

        return view;
    }
    //endregion

    //region Inicialização
    /**
     * Inicializa as referências das views utilizadas no fragment
     */
    private void inicializarViews(View view) {
        itemImage = view.findViewById(R.id.item_image);
        itemTitle = view.findViewById(R.id.item_title);
        itemDescription = view.findViewById(R.id.item_description);
        itemPrice = view.findViewById(R.id.item_price);
        btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        storeImage = view.findViewById(R.id.store_image);
        storeName = view.findViewById(R.id.store_name);
        storeContainer = view.findViewById(R.id.store_container);
    }

    /**
     * Configura o comportamento do botão voltar do sistema
     */
    private void configurarBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Voltar para ExplorarFragment com o filtro correto
                ExplorarFragment explorarFragment = new ExplorarFragment();
                Bundle args = new Bundle();
                args.putString("filtro", item.getTipo().toLowerCase().equals("serviço") ? "servicos" : "produtos");
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
     * Configura os dados do item na interface
     */
    private void configurarDadosItem() {
        // Carregar imagem do item
        if (item.getFotoUrl() != null && !item.getFotoUrl().isEmpty()) {
            Glide.with(this)
                .load(item.getFotoUrl())
                .into(itemImage);
        }

        // Configurar textos
        itemTitle.setText(item.getTitulo());
        itemDescription.setText(item.getDescricao());
        itemPrice.setText(String.format("R$ %.2f", item.getPreco()));

        // Carregar dados da loja
        carregarDadosLoja();
    }

    /**
     * Carrega os dados da loja do Firestore
     */
    private void carregarDadosLoja() {
        db.collection("lojas")
            .document(item.getLojaId())
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

                    // Configurar nome da loja
                    storeName.setText(document.getString("nome"));
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
        // Botão adicionar ao carrinho
        btnAddToCart.setOnClickListener(v -> adicionarAoCarrinho());

        // Clique na loja
        storeContainer.setOnClickListener(v -> {
            // Abrir LojaFragment
            LojaFragment lojaFragment = LojaFragment.newInstance(item.getLojaId());
            getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, lojaFragment)
                .addToBackStack(null)
                .commit();
        });
    }
    //endregion

    //region Ações
    /**
     * Adiciona o item atual ao carrinho do usuário
     */
    private void adicionarAoCarrinho() {
        // Verificar autenticação
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Faça login para adicionar ao carrinho", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adicionar ao carrinho
        Map<String, Object> carrinhoItem = new HashMap<>();
        carrinhoItem.put("id", item.getId());
        carrinhoItem.put("titulo", item.getTitulo());
        carrinhoItem.put("descricao", item.getDescricao());
        carrinhoItem.put("preco", item.getPreco());
        carrinhoItem.put("fotoUrl", item.getFotoUrl());
        carrinhoItem.put("tipo", item.getTipo());
        carrinhoItem.put("lojaId", item.getLojaId());
        carrinhoItem.put("lojaNome", storeName.getText().toString());

        // Salvar no Firestore
        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .collection("carrinho")
            .document(item.getId())
            .set(carrinhoItem)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Item adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                
                // Navegar para o carrinho
                irParaCarrinho();
            })
            .addOnFailureListener(e -> 
                Toast.makeText(getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show()
            );
    }

    /**
     * Navega para o fragment do carrinho e atualiza o menu inferior
     */
    private void irParaCarrinho() {
        // Navegar para o carrinho e selecionar o item no menu
        getParentFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, new CarrinhoFragment())
            .commit();

        // Selecionar o item do carrinho no menu inferior
        if (getActivity() instanceof MainPainel) {
            ((MainPainel) getActivity()).getBottomNavigationView()
                .setSelectedItemId(R.id.navegacao_carrinho);
        }
    }
    //endregion
}
