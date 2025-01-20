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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainInicio extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> signInLauncher;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Verificar se já existe uma sessão ativa
        if (checkExistingSession()) {
            goToMainPainel();
            return;
        }

        setContentView(R.layout.activity_inicio);

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Configurar o launcher para o resultado do login
        signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleSignInResult(task);
            }
        );

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialButton botaoEntrar = findViewById(R.id.botao_entrar);
        MaterialButton botaoRegistrar = findViewById(R.id.botao_registrar);
        MaterialButton botaoGoogle = findViewById(R.id.botao_google);

        configurarEfeitoBotao(botaoEntrar);
        configurarEfeitoBotao(botaoRegistrar);
        configurarEfeitoBotao(botaoGoogle);

        botaoEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainInicio.this, MainConectar.class);
            startActivity(intent);
        });

        botaoRegistrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainInicio.this, MainRegistrar.class);
            startActivity(intent);
        });

        botaoGoogle.setOnClickListener(v -> signIn());
    }

    private boolean checkExistingSession() {
        // Verificar Firebase Auth
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            return true;
        }

        // Verificar Google Sign In
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        return account != null;
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Login bem sucedido, redirecionar para MainPainel
            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
            goToMainPainel();
        } catch (ApiException e) {
            // Login falhou
            String message;
            switch (e.getStatusCode()) {
                case 12500: // SIGN_IN_FAILED
                    message = "Erro nos serviços do Google Play";
                    break;
                case 7: // NETWORK_ERROR
                    message = "Erro de conexão. Verifique sua internet.";
                    break;
                case 10: // DEVELOPER_ERROR
                    message = "Erro de configuração do OAuth";
                    break;
                default:
                    message = "Falha no login com Google: " + e.getStatusCode();
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void goToMainPainel() {
        Intent intent = new Intent(MainInicio.this, MainPainel.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}