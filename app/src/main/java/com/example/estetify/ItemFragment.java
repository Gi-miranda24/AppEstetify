package com.example.estetify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import de.hdodenhof.circleimageview.CircleImageView;

public class ItemFragment extends Fragment {
    private ImageView itemImage;
    private TextView itemTitle;
    private TextView itemDescription;
    private TextView itemPrice;
    private Button btnAddToCart;
    private CircleImageView storeImage;
    private TextView storeName;
    private View storeContainer;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Item item;

    public static ItemFragment newInstance(Item item) {
        ItemFragment fragment = new ItemFragment();
        fragment.item = item;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verificar autenticação
        AuthVerification.verificarAutenticacao(this, getParentFragmentManager());
        
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Habilitar callback de back press
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

    private void adicionarAoCarrinho() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Faça login para adicionar ao carrinho", Toast.LENGTH_SHORT).show();
            return;
        }

        CarrinhoItem carrinhoItem = new CarrinhoItem(item, storeName.getText().toString());

        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .collection("carrinho")
            .document(item.getId())
            .set(carrinhoItem)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Item adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                
                // Navegar para o carrinho
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CarrinhoFragment())
                    .commit();
            })
            .addOnFailureListener(e -> 
                Toast.makeText(getContext(), "Erro ao adicionar ao carrinho", Toast.LENGTH_SHORT).show()
            );
    }
}
