package com.example.estetify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

public class MainPainel extends AppCompatActivity {

    //region Constantes
    private static final String COR_BARRA_SISTEMA = "#2C3E50";
    //endregion

    //region Campos
    private BottomNavigationView navegacaoInferior;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    //endregion

    //region Ciclo de Vida
    /**
     * Método principal chamado na criação da activity.
     * Inicializa os serviços, configura a interface e navegação.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);

        inicializarServicos();
        configurarInterface();
        configurarNavegacao();
        configurarBotoes();
    }
    //endregion

    //region Inicialização
    /**
     * Inicializa os serviços necessários para a aplicação:
     * Firebase Authentication e Google Sign-In.
     */
    private void inicializarServicos() {
        inicializarFirebase();
        inicializarGoogleSignIn();
    }

    /**
     * Inicializa o Firebase Authentication para gerenciamento
     * de autenticação do usuário.
     */
    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Inicializa o serviço de autenticação do Google,
     * configurando as opções necessárias para o login.
     */
    private void inicializarGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    //endregion

    //region Interface do Usuário
    /**
     * Configura os elementos visuais da interface do usuário,
     * incluindo barras do sistema e padding do conteúdo.
     */
    private void configurarInterface() {
        configurarBarrasDoSistema();
        configurarPaddingConteudo();
    }

    /**
     * Configura as cores das barras de status e navegação
     * do sistema operacional.
     */
    private void configurarBarrasDoSistema() {
        getWindow().setStatusBarColor(Color.parseColor(COR_BARRA_SISTEMA));
        getWindow().setNavigationBarColor(Color.parseColor(COR_BARRA_SISTEMA));
    }

    /**
     * Configura o padding do conteúdo principal para se ajustar
     * corretamente às barras do sistema.
     */
    private void configurarPaddingConteudo() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    //endregion

    //region Navegação e Fragmentos
    /**
     * Configura o sistema de navegação do aplicativo,
     * incluindo o fragmento inicial e a barra de navegação inferior.
     */
    private void configurarNavegacao() {
        inicializarFragmentoInicial();
        configurarBarraNavegacao();
    }

    /**
     * Inicializa o primeiro fragmento a ser exibido quando
     * o usuário acessa o painel (ExplorarFragment).
     */
    private void inicializarFragmentoInicial() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExplorarFragment())
                .commit();
        }
    }

    /**
     * Configura a barra de navegação inferior com seus itens
     * e comportamento de seleção.
     */
    private void configurarBarraNavegacao() {
        navegacaoInferior = findViewById(R.id.navegacao_inferior);
        navegacaoInferior.setSelectedItemId(R.id.navegacao_explorar);
        navegacaoInferior.setOnItemSelectedListener(item -> {
            Fragment novoFragmento = selecionarFragmento(item.getItemId());
            if (novoFragmento != null) {
                trocarFragmento(novoFragmento);
            }
            return true;
        });
    }

    /**
     * Seleciona o fragmento apropriado com base no item
     * selecionado na barra de navegação.
     * @param itemId ID do item selecionado na navegação
     * @return Fragment correspondente ao item selecionado ou null
     */
    private Fragment selecionarFragmento(int itemId) {
        if (itemId == R.id.navegacao_explorar) {
            return new ExplorarFragment();
        } else if (itemId == R.id.navegacao_carrinho) {
            return new CarrinhoFragment();
        } else if (itemId == R.id.navegacao_perfil) {
            return new PerfilFragment();
        }
        return null;
    }

    /**
     * Realiza a troca do fragmento atual pelo novo fragmento selecionado.
     * @param fragment Novo fragmento a ser exibido
     */
    private void trocarFragmento(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit();
    }
    //endregion

    //region Eventos de Botões
    /**
     * Configura os botões da interface, incluindo
     * botões de sair e voltar.
     */
    private void configurarBotoes() {
        configurarBotaoSair();
        configurarBotaoVoltar();
    }

    /**
     * Configura o botão de sair com o comportamento
     * de exibir o diálogo de confirmação.
     */
    private void configurarBotaoSair() {
        findViewById(R.id.botao_sair).setOnClickListener(v -> mostrarDialogoSair());
    }

    /**
     * Configura o comportamento do botão voltar do dispositivo
     * para exibir o diálogo de confirmação ao sair.
     */
    private void configurarBotaoVoltar() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                mostrarDialogoSairAplicativo();
            }
        });
    }
    //endregion

    //region Diálogos do Sistema
    /**
     * Exibe um diálogo de confirmação para o usuário
     * quando ele tenta fazer logout do aplicativo.
     */
    private void mostrarDialogoSair() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sim", (dialog, which) -> realizarLogout())
            .setNegativeButton("Não", null)
            .show();
    }

    /**
     * Exibe um diálogo de confirmação para o usuário
     * quando ele tenta sair completamente do aplicativo.
     */
    private void mostrarDialogoSairAplicativo() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Sair")
            .setMessage("Deseja sair do Estetify?")
            .setPositiveButton("Sim", (dialog, which) -> finishAffinity())
            .setNegativeButton("Não", null)
            .show();
    }
    //endregion

    //region Autenticação e Navegação
    /**
     * Realiza o processo de logout do usuário,
     * desconectando tanto do Firebase quanto do Google.
     */
    private void realizarLogout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            redirecionarParaTelaInicial();
        });
    }

    /**
     * Redireciona o usuário para a tela inicial após
     * o logout, limpando a pilha de activities.
     */
    private void redirecionarParaTelaInicial() {
        Intent intent = new Intent(this, MainInicio.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    //endregion
}