package com.example.estetify;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class CarrinhoFragment extends Fragment implements CarrinhoAdapter.OnItemRemovidoListener {
    private ProgressBar loadingCarrinho;
    private RecyclerView recyclerView;
    private TextView totalItens;
    private TextView valorTotal;
    private MaterialButton btnFinalizar;
    private CarrinhoAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<CarrinhoItem> itensCarrinho = new ArrayList<>();

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

    private void inicializarViews(View view) {
        loadingCarrinho = view.findViewById(R.id.loading_carrinho);
        recyclerView = view.findViewById(R.id.recycler_view);
        totalItens = view.findViewById(R.id.total_itens);
        valorTotal = view.findViewById(R.id.valor_total);
        btnFinalizar = view.findViewById(R.id.btn_finalizar);
    }

    private void configurarListeners() {
        btnFinalizar.setOnClickListener(v -> mostrarDialogConfirmacao());
    }

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
                    CarrinhoItem item = doc.toObject(CarrinhoItem.class);
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

    private void atualizarResumo() {
        int total = itensCarrinho.size();
        double valor = itensCarrinho.stream()
                .mapToDouble(CarrinhoItem::getPreco)
                .sum();

        totalItens.setText(String.valueOf(total));
        valorTotal.setText(String.format("R$ %.2f", valor));
        
        btnFinalizar.setEnabled(total > 0);
    }

    @Override
    public void onItemRemovido(CarrinhoItem item, int position) {
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

    private void mostrarDialogConfirmacao() {
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Finalizar Compra")
            .setMessage("Tem certeza que deseja finalizar sua compra?")
            .setPositiveButton("Sim", (dialog, which) -> finalizarCompra())
            .setNegativeButton("Não", null)
            .show();
    }

    private void finalizarCompra() {
        if (auth.getCurrentUser() == null) return;

        loadingCarrinho.setVisibility(View.VISIBLE);
        
        // Criar pedido
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("usuarioId", auth.getCurrentUser().getUid());
        pedido.put("data", new Date());
        pedido.put("itens", itensCarrinho);
        pedido.put("valorTotal", itensCarrinho.stream()
                .mapToDouble(CarrinhoItem::getPreco)
                .sum());
        pedido.put("status", "pendente");

        // Salvar pedido no Firestore
        db.collection("pedidos")
            .add(pedido)
            .addOnSuccessListener(documentReference -> {
                // Limpar carrinho
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
            })
            .addOnFailureListener(e -> {
                loadingCarrinho.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Erro ao finalizar compra", Toast.LENGTH_SHORT).show();
            });
    }
}
