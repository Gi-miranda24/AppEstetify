package com.example.estetify;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.fragment.app.Fragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Intent;
import android.widget.Toast;

public class MainPainel extends AppCompatActivity {
    private BottomNavigationView navegacaoInferior;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painel);

        mAuth = FirebaseAuth.getInstance();
        
        // Configurar botão de sair
        findViewById(R.id.botao_sair).setOnClickListener(v -> mostrarDialogoSair());

        // Configurar Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build();
        
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Carregar o fragmento inicial (Explorar)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ExplorarFragment())
                .commit();
        }

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configurar navegação
        navegacaoInferior = findViewById(R.id.navegacao_inferior);
        navegacaoInferior.setSelectedItemId(R.id.navegacao_explorar);
        navegacaoInferior.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navegacao_explorar) {
                selectedFragment = new ExplorarFragment();
            } else if (itemId == R.id.navegacao_carrinho) {
                selectedFragment = new CarrinhoFragment();
            } else if (itemId == R.id.navegacao_perfil) {
                selectedFragment = new PerfilFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            }
            return true;
        });
    }

    private void mostrarDialogoSair() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sim", (dialog, which) -> {
                // Fazer logout do Firebase e Google
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                    // Voltar para a tela inicial
                    Intent intent = new Intent(this, MainInicio.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            })
            .setNegativeButton("Não", null)
            .show();
    }

    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor("#2C3E50"));
    }

    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor("#2C3E50"));
    }

    @Override
    public void onBackPressed() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Sair")
            .setMessage("Deseja sair do Estetify?")
            .setPositiveButton("Sim", (dialog, which) -> {
                finishAffinity(); // Fecha todas as activities
            })
            .setNegativeButton("Não", null)
            .show();
    }
}