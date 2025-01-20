package com.example.estetify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPainel extends AppCompatActivity {
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private TextView textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .requestIdToken(getString(R.string.google_client_id))
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Verificar autenticação
        checkAuthentication();
    }

    private void checkAuthentication() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        GoogleSignInAccount googleAccount = GoogleSignIn.getLastSignedInAccount(this);

        if (firebaseUser != null) {
            // Usuário logado via Firebase
            setupUserInterface(firebaseUser);
        } else if (googleAccount != null) {
            // Usuário logado via Google
            setupGoogleUserInterface(googleAccount);
        } else {
            // Nenhum usuário logado
            goToMainInicio();
        }
    }

    private void setupUserInterface(FirebaseUser user) {
        // Configurar nome do usuário
        TextView nomeUsuario = findViewById(R.id.nome_usuario);
        if (nomeUsuario != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                nomeUsuario.setText(displayName);
            } else {
                nomeUsuario.setVisibility(View.GONE);
            }
        }

        // Configurar email do usuário
        textViewEmail = findViewById(R.id.email_usuario);
        if (textViewEmail != null) {
            textViewEmail.setText(user.getEmail());
        }

        // Configurar botão de logout
        MaterialButton botaoSair = findViewById(R.id.botao_sair);
        if (botaoSair != null) {
            botaoSair.setOnClickListener(v -> signOut());
        }
    }

    private void setupGoogleUserInterface(GoogleSignInAccount account) {
        // Configurar nome do usuário
        TextView nomeUsuario = findViewById(R.id.nome_usuario);
        if (nomeUsuario != null) {
            String displayName = account.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                nomeUsuario.setText(displayName);
            } else {
                nomeUsuario.setVisibility(View.GONE);
            }
        }

        // Configurar email do usuário
        textViewEmail = findViewById(R.id.email_usuario);
        if (textViewEmail != null) {
            textViewEmail.setText(account.getEmail());
        }

        // Configurar botão de logout
        MaterialButton botaoSair = findViewById(R.id.botao_sair);
        if (botaoSair != null) {
            botaoSair.setOnClickListener(v -> signOut());
        }
    }

    private void signOut() {
        // Logout do Firebase
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        // Logout do Google
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    goToMainInicio();
                    Toast.makeText(MainPainel.this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
                });
        } else {
            goToMainInicio();
            Toast.makeText(MainPainel.this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainInicio() {
        Intent intent = new Intent(MainPainel.this, MainInicio.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Função para mudar a cor da barra de status
    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor("#2C3E50"));
    }

    // Função para mudar a cor da barra de navegação
    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor("#2C3E50"));
    }
}