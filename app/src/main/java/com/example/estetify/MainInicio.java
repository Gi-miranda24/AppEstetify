package com.example.estetify;

// Imports Android Core
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

// Imports AndroidX
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Imports Google e Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainInicio extends AppCompatActivity {
    // Constantes
    private static final String STATUS_BAR_COLOR = "#2C3E50";
    private static final String NAV_BAR_COLOR = "#2C3E50";

    // Firebase e Google Sign-In
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInOptions gso;

    /**
     * Método do ciclo de vida chamado quando a Activity é criada.
     * Inicializa os componentes principais e verifica o estado do usuário.
     * @param savedInstanceState Estado salvo da activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        inicializarFirebaseEGoogle();
        configurarSignInLauncher();
        configurarBarrasSistema();
        verificarUsuarioAtual();
    }

    /**
     * Inicializa as instâncias do Firebase Authentication, Firestore
     * e configura as opções de login do Google.
     */
    private void inicializarFirebaseEGoogle() {
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Google Sign-In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    /**
     * Configura o launcher para o processo de login com Google,
     * que será responsável por processar o resultado da autenticação.
     */
    private void configurarSignInLauncher() {
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> processarResultadoLogin(result.getData())
        );
    }

    /**
     * Processa o resultado do login com Google.
     * @param data Intent contendo os dados do resultado do login
     */
    private void processarResultadoLogin(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            autenticarComFirebase(account);
        } catch (ApiException e) {
            mostrarErro("Erro no login com Google: " + e.getMessage());
        }
    }

    /**
     * Realiza a autenticação no Firebase usando as credenciais do Google.
     * @param account Conta do Google obtida após o login
     */
    private void autenticarComFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnSuccessListener(authResult -> {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    verificarOuCriarUsuario(user);
                }
            })
            .addOnFailureListener(e -> {
                mostrarErro("Erro na autenticação: " + e.getMessage());
                mGoogleSignInClient.signOut();
            });
    }

    /**
     * Verifica se o usuário já existe no Firestore ou cria um novo registro.
     * @param user Usuário do Firebase Authentication
     */
    private void verificarOuCriarUsuario(FirebaseUser user) {
        db.collection("usuarios")
            .document(user.getUid())
            .get()
            .addOnSuccessListener(document -> {
                if (!document.exists()) {
                    criarNovoUsuario(user);
                } else {
                    navegarParaPainel();
                }
            })
            .addOnFailureListener(e -> {
                mostrarErro("Erro ao verificar usuário: " + e.getMessage());
                fazerLogout();
            });
    }

    /**
     * Cria um novo registro de usuário no Firestore.
     * @param user Usuário do Firebase Authentication
     */
    private void criarNovoUsuario(FirebaseUser user) {
        Usuario novoUsuario = new Usuario(
            user.getDisplayName(),
            user.getEmail(),
            user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
        );

        db.collection("usuarios")
            .document(user.getUid())
            .set(novoUsuario)
            .addOnSuccessListener(aVoid -> navegarParaPainel())
            .addOnFailureListener(e -> {
                mostrarErro("Erro ao criar usuário: " + e.getMessage());
                fazerLogout();
            });
    }

    /**
     * Configura as cores e o padding das barras do sistema.
     */
    private void configurarBarrasSistema() {
        changeStatusBarColor();
        changeNavigationBarColor();
        ajustarPaddingSistema();
    }

    /**
     * Define a cor da barra de status do sistema.
     */
    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor(STATUS_BAR_COLOR));
    }

    /**
     * Define a cor da barra de navegação do sistema.
     */
    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor(NAV_BAR_COLOR));
    }

    /**
     * Ajusta o padding do layout principal considerando as barras do sistema.
     */
    private void ajustarPaddingSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Verifica se existe um usuário atualmente logado e
     * realiza as ações apropriadas com base nessa verificação.
     */
    private void verificarUsuarioAtual() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            verificarUsuarioFirestore(currentUser);
        } else {
            setupViews();
        }
    }

    /**
     * Verifica se o usuário existe no Firestore.
     * @param user Usuário do Firebase Authentication
     */
    private void verificarUsuarioFirestore(FirebaseUser user) {
        db.collection("usuarios")
            .document(user.getUid())
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    navegarParaPainel();
                } else {
                    fazerLogout();
                    setupViews();
                    mostrarErro("Conta não encontrada. Por favor, faça login novamente.");
                }
            })
            .addOnFailureListener(e -> {
                fazerLogout();
                setupViews();
                mostrarErro("Erro ao verificar conta. Por favor, tente novamente.");
            });
    }

    /**
     * Inicializa e configura os elementos da interface do usuário.
     */
    private void setupViews() {
        MaterialButton botaoEntrar = findViewById(R.id.botao_entrar);
        MaterialButton botaoCadastrar = findViewById(R.id.botao_registrar);
        MaterialButton botaoGoogle = findViewById(R.id.botao_google);

        configurarEfeitoBotao(botaoEntrar);
        configurarEfeitoBotao(botaoCadastrar);
        configurarEfeitoBotao(botaoGoogle);

        configurarClickListeners(botaoEntrar, botaoCadastrar, botaoGoogle);
    }

    /**
     * Configura os listeners de clique para os botões da interface.
     * @param botaoEntrar Botão de login
     * @param botaoCadastrar Botão de cadastro
     * @param botaoGoogle Botão de login com Google
     */
    private void configurarClickListeners(MaterialButton botaoEntrar, MaterialButton botaoCadastrar, MaterialButton botaoGoogle) {
        botaoEntrar.setOnClickListener(v -> navegarParaTelaConectar());
        botaoCadastrar.setOnClickListener(v -> navegarParaTelaRegistrar());
        botaoGoogle.setOnClickListener(v -> signIn());
    }

    /**
     * Configura o efeito visual de toque para um botão.
     * @param botao Botão a ser configurado
     */
    private void configurarEfeitoBotao(MaterialButton botao) {
        botao.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                botao.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.azul)));
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                botao.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.branco)));
            }
            return false;
        });
    }

    /**
     * Navega para a tela do painel principal e finaliza a activity atual.
     */
    private void navegarParaPainel() {
        startActivity(new Intent(MainInicio.this, MainPainel.class));
        finish();
    }

    /**
     * Navega para a tela de login.
     */
    private void navegarParaTelaConectar() {
        startActivity(new Intent(MainInicio.this, MainConectar.class));
    }

    /**
     * Navega para a tela de cadastro.
     */
    private void navegarParaTelaRegistrar() {
        startActivity(new Intent(MainInicio.this, MainRegistrar.class));
    }

    /**
     * Inicia o processo de login com Google.
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    /**
     * Realiza o logout do usuário tanto no Firebase quanto no Google.
     */
    private void fazerLogout() {
        mAuth.signOut();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
    }

    /**
     * Exibe uma mensagem de erro para o usuário.
     * @param mensagem Mensagem de erro a ser exibida
     */
    private void mostrarErro(String mensagem) {
        Toast.makeText(MainInicio.this, mensagem, Toast.LENGTH_SHORT).show();
    }
}