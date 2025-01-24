package com.example.estetify;

// Imports do Android
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

// Imports do AndroidX
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports do Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

// Imports do Java
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsável por exibir o histórico de compras do usuário.
 * Mostra uma lista de pedidos ordenada por data, do mais recente para o mais antigo.
 */
public class HistoricoFragment extends Fragment {
    //region Campos
    // Views
    private RecyclerView recyclerView;
    private ProgressBar loadingHistorico;
    private TextView textoVazio;
    
    // Adapter
    private HistoricoAdapter adapter;
    
    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    
    // Dados
    private List<Pedido> pedidos = new ArrayList<>();
    //endregion

    //region Ciclo de Vida
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verificar autenticação
        AuthVerification.verificarAutenticacao(this, getParentFragmentManager());

        // Configurar o callback do botão voltar
        configurarBotaoVoltar();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historico, container, false);
        
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Inicializar views
        inicializarViews(view);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoricoAdapter(pedidos);
        recyclerView.setAdapter(adapter);

        // Carregar pedidos
        carregarPedidos();

        return view;
    }
    //endregion

    //region Inicialização
    /**
     * Inicializa as referências das views utilizadas no fragment
     */
    private void inicializarViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
        loadingHistorico = view.findViewById(R.id.loading_historico);
        textoVazio = view.findViewById(R.id.texto_vazio);
    }

    /**
     * Configura o comportamento do botão voltar do sistema
     */
    private void configurarBotaoVoltar() {
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navegar de volta para o PerfilFragment
                PerfilFragment perfilFragment = new PerfilFragment();
                getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, perfilFragment)
                    .commit();
            }
        });
    }
    //endregion

    //region Carregar Dados
    /**
     * Carrega os pedidos do usuário do Firestore.
     * Os pedidos são ordenados por data, do mais recente para o mais antigo.
     */
    private void carregarPedidos() {
        if (auth.getCurrentUser() == null) {
            textoVazio.setVisibility(View.VISIBLE);
            textoVazio.setText("Usuário não autenticado");
            loadingHistorico.setVisibility(View.GONE);
            return;
        }

        loadingHistorico.setVisibility(View.VISIBLE);
        textoVazio.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        // Buscar pedidos do usuário
        db.collection("pedidos")
            .whereEqualTo("usuarioId", auth.getCurrentUser().getUid())
            .get()
            .addOnSuccessListener(documents -> {
                pedidos.clear();
                for (var doc : documents) {
                    Pedido pedido = doc.toObject(Pedido.class);
                    if (pedido != null) {
                        pedidos.add(pedido);
                    }
                }
                
                // Ordenar a lista localmente por data
                pedidos.sort((p1, p2) -> p2.getData().compareTo(p1.getData()));
                
                // Atualizar UI
                atualizarUI();
            })
            .addOnFailureListener(e -> {
                // Mostrar erro
                loadingHistorico.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                textoVazio.setVisibility(View.VISIBLE);
                textoVazio.setText("Erro ao carregar histórico: " + e.getMessage());
            });
    }

    /**
     * Atualiza a interface do usuário com base nos dados carregados
     */
    private void atualizarUI() {
        loadingHistorico.setVisibility(View.GONE);
        
        if (pedidos.isEmpty()) {
            textoVazio.setVisibility(View.VISIBLE);
            textoVazio.setText("Nenhuma compra realizada");
            recyclerView.setVisibility(View.GONE);
        } else {
            textoVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }
    //endregion
}
