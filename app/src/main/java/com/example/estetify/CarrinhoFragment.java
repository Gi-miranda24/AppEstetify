package com.example.estetify;

// Imports do Android
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// Imports do AndroidX
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports do Material Design
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

// Imports do Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

// Imports do Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Fragment responsável por exibir e gerenciar o carrinho de compras do usuário.
 * Permite visualizar itens adicionados, remover itens e finalizar a compra.
 */
public class CarrinhoFragment extends Fragment implements CarrinhoAdapter.OnItemRemovidoListener {
    //region Campos
    // Views
    private ProgressBar loadingCarrinho;
    private RecyclerView recyclerView;
    private TextView totalItens;
    private TextView valorTotal;
    private MaterialButton btnFinalizar;
    
    // Adapter
    private CarrinhoAdapter adapter;
    
    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Dados
    private List<Carrinho> itensCarrinho = new ArrayList<>();
    //endregion

    //region Ciclo de Vida
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verificar autenticação
        AuthVerification.verificarAutenticacao(this, getParentFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);
        
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        
        // Inicializar views
        inicializarViews(view);
        
        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CarrinhoAdapter(itensCarrinho, this);
        recyclerView.setAdapter(adapter);
        
        // Carregar itens do carrinho
        carregarItensCarrinho();
        
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
        loadingCarrinho = view.findViewById(R.id.loading_carrinho);
        recyclerView = view.findViewById(R.id.recycler_view);
        totalItens = view.findViewById(R.id.total_itens);
        valorTotal = view.findViewById(R.id.valor_total);
        btnFinalizar = view.findViewById(R.id.btn_finalizar);
    }

    /**
     * Configura os listeners de eventos dos componentes
     */
    private void configurarListeners() {
        btnFinalizar.setOnClickListener(v -> mostrarDialogConfirmacao());
    }
    //endregion

    //region Carregar Dados
    /**
     * Carrega os itens do carrinho do usuário do Firestore
     */
    private void carregarItensCarrinho() {
        if (auth.getCurrentUser() == null) return;

        loadingCarrinho.setVisibility(View.VISIBLE);
        
        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .collection("carrinho")
            .get()
            .addOnSuccessListener(documents -> {
                itensCarrinho.clear();
                for (var doc : documents) {
                    Carrinho item = doc.toObject(Carrinho.class);
                    itensCarrinho.add(item);
                }
                adapter.updateItens(itensCarrinho);
                atualizarResumo();
                loadingCarrinho.setVisibility(View.GONE);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Erro ao carregar carrinho", Toast.LENGTH_SHORT).show();
                loadingCarrinho.setVisibility(View.GONE);
            });
    }

    /**
     * Atualiza o resumo do carrinho (total de itens e valor total)
     */
    private void atualizarResumo() {
        int total = itensCarrinho.size();
        double valor = itensCarrinho.stream()
                .mapToDouble(Carrinho::getPreco)
                .sum();

        totalItens.setText(String.valueOf(total));
        valorTotal.setText(String.format("R$ %.2f", valor));
        
        btnFinalizar.setEnabled(total > 0);
    }
    //endregion

    //region Eventos
    @Override
    public void onItemRemovido(Carrinho item, int position) {
        if (auth.getCurrentUser() == null) return;

        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .collection("carrinho")
            .document(item.getId())
            .delete()
            .addOnSuccessListener(aVoid -> {
                itensCarrinho.remove(position);
                adapter.updateItens(itensCarrinho);
                atualizarResumo();
                Toast.makeText(getContext(), "Item removido do carrinho", Toast.LENGTH_SHORT).show();
            })
            .addOnFailureListener(e -> 
                Toast.makeText(getContext(), "Erro ao remover item", Toast.LENGTH_SHORT).show()
            );
    }
    //endregion

    //region Finalizar Compra
    /**
     * Exibe o diálogo de confirmação para finalizar a compra
     */
    private void mostrarDialogConfirmacao() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Finalizar Compra")
            .setMessage("Tem certeza que deseja finalizar sua compra?")
            .setPositiveButton("Sim", (dialog, which) -> finalizarCompra())
            .setNegativeButton("Não", null)
            .show();
    }

    /**
     * Finaliza a compra, criando um novo pedido e limpando o carrinho
     */
    private void finalizarCompra() {
        if (auth.getCurrentUser() == null) return;

        loadingCarrinho.setVisibility(View.VISIBLE);
        
        // Criar pedido
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("usuarioId", auth.getCurrentUser().getUid());
        pedido.put("data", new Date());
        pedido.put("itens", itensCarrinho);
        pedido.put("valorTotal", itensCarrinho.stream()
                .mapToDouble(Carrinho::getPreco)
                .sum());
        pedido.put("status", "pendente");

        // Salvar pedido no Firestore
        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener(documentReference -> {
                // Limpar carrinho
                limparCarrinho();
            })
            .addOnFailureListener(e -> {
                loadingCarrinho.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
            });
    }
    
    /**
     * Limpa todos os itens do carrinho após finalizar a compra
     */
    private void limparCarrinho() {
        db.collection("usuarios")
            .document(auth.getCurrentUser().getUid())
            .collection("carrinho")
            .get()
            .addOnSuccessListener(documents -> {
                for (var doc : documents) {
                    doc.getReference().delete();
                }
                itensCarrinho.clear();
                adapter.updateItens(itensCarrinho);
                atualizarResumo();
                loadingCarrinho.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Compra finalizada com sucesso!", Toast.LENGTH_LONG).show();
            });
    }
    //endregion
}
