package com.example.estetify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Verificar se já existe um usuário logado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            verificarUsuarioFirestore(currentUser);
        } else {
            // Se não existe usuário, continua na tela de início
            setupViews();
        }

        // Configurar o launcher para o resultado do login do Google
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    // Criar credencial do Firebase com a conta Google
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    
                    // Autenticar no Firebase
                    mAuth.signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> {
                            // Criar/verificar usuário no Firestore
                            FirebaseUser user = authResult.getUser();
                            if (user != null) {
                                db.collection("usuarios")
                                    .document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(document -> {
                                        if (!document.exists()) {
                                            // Criar novo usuário no Firestore
                                            db.collection("usuarios")
                                                .document(user.getUid())
                                                .set(new Usuario(
                                                    user.getDisplayName(),
                                                    user.getEmail(),
                                                    user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null
                                                ))
                                                .addOnSuccessListener(aVoid -> {
                                                    // Ir para o painel
                                                    startActivity(new Intent(MainInicio.this, MainPainel.class));
                                                    finish();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(MainInicio.this,
                                                        "Erro ao criar usuário: " + e.getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                                    mAuth.signOut();
                                                    mGoogleSignInClient.signOut();
                                                });
                                        } else {
                                            // Usuário já existe, ir para o painel
                                            startActivity(new Intent(MainInicio.this, MainPainel.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(MainInicio.this,
                                            "Erro ao verificar usuário: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
                                        mGoogleSignInClient.signOut();
                                    });
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(MainInicio.this,
                                "Erro na autenticação: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                            mGoogleSignInClient.signOut();
                        });
                } catch (ApiException e) {
                    Toast.makeText(MainInicio.this,
                        "Erro no login com Google: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                }
            });

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void verificarUsuarioFirestore(FirebaseUser user) {
        db.collection("usuarios")
            .document(user.getUid())
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists()) {
                    // Usuário existe no Firestore, pode prosseguir
                    startActivity(new Intent(MainInicio.this, MainPainel.class));
                    finish();
                } else {
                    // Usuário não existe no Firestore, fazer logout e mostrar tela de início
                    mAuth.signOut();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                    googleSignInClient.signOut();
                    setupViews();
                    Toast.makeText(MainInicio.this, 
                        "Conta não encontrada. Por favor, faça login novamente.", 
                        Toast.LENGTH_LONG).show();
                }
            })
            .addOnFailureListener(e -> {
                // Erro ao verificar, manter na tela de início
                mAuth.signOut();
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
                googleSignInClient.signOut();
                setupViews();
                Toast.makeText(MainInicio.this, 
                    "Erro ao verificar conta. Por favor, tente novamente.", 
                    Toast.LENGTH_LONG).show();
            });
    }

    private void setupViews() {
        MaterialButton botaoEntrar = findViewById(R.id.botao_entrar);
        MaterialButton botaoCadastrar = findViewById(R.id.botao_registrar);
        MaterialButton botaoGoogle = findViewById(R.id.botao_google);

        configurarEfeitoBotao(botaoEntrar);
        configurarEfeitoBotao(botaoCadastrar);
        configurarEfeitoBotao(botaoGoogle);

        botaoEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainInicio.this, MainConectar.class);
            startActivity(intent);
        });

        botaoCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainInicio.this, MainRegistrar.class);
            startActivity(intent);
        });

        botaoGoogle.setOnClickListener(v -> signIn());
    }

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

    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor("#2C3E50"));
    }

    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor("#2C3E50"));
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }
}