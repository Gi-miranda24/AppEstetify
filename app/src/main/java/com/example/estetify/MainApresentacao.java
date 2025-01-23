package com.example.estetify;

// Imports de Android Core
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

// Imports de Views
import android.widget.ImageView;
import android.widget.TextView;

// Imports de Animação
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

// Imports de AndroidX
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Imports do Firebase
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainApresentacao extends AppCompatActivity {
    // Constantes
    private static final int ANIMATION_DURATION = 500;
    private static final int DELAY_DURATION = 1000;
    private static final String STATUS_BAR_COLOR = "#2C3E50";

    // Firebase
    private FirebaseAuth mAuth;

    // Views
    private ImageView logotipo;
    private TextView texto_rodape;

    /**
     * Método do ciclo de vida chamado quando a Activity é criada.
     * Inicializa a interface, Firebase e executa as animações iniciais.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apresentacao);

        // Inicialização
        inicializarViews();
        inicializarFirebase();
        configurarBarrasSistema();
        
        // Animações e navegação
        executarAnimacoes();
        iniciarVerificacaoLogin();
    }

    /**
     * Inicializa as referências das views utilizadas na tela.
     * Conecta os elementos do layout com as variáveis do código.
     */
    private void inicializarViews() {
        logotipo = findViewById(R.id.logotipo);
        texto_rodape = findViewById(R.id.texto_rodape);
    }

    /**
     * Inicializa a instância do Firebase Authentication.
     * Necessário para verificar o estado de login do usuário.
     */
    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Configura a aparência das barras do sistema (status e navegação).
     * Ajusta cores e padding para melhor experiência visual.
     */
    private void configurarBarrasSistema() {
        changeStatusBarColor();
        changeNavigationBarColor();
        ajustarPaddingSistema();
    }

    /**
     * Ajusta o padding do layout principal considerando as barras do sistema.
     * Garante que o conteúdo não fique sobreposto às barras do sistema.
     */
    private void ajustarPaddingSistema() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Executa as animações de fade in do logotipo e texto do rodapé.
     * Cria e inicia um conjunto de animações simultâneas.
     */
    private void executarAnimacoes() {
        // Criar animações
        ObjectAnimator fadeInLogoTipo = criarFadeInAnimator(logotipo);
        ObjectAnimator fadeInTextoRodape = criarFadeInAnimator(texto_rodape);

        // Executar animações juntas
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeInLogoTipo, fadeInTextoRodape);
        animatorSet.start();
    }

    /**
     * Cria uma animação de fade in para uma view específica.
     * @param view View que receberá a animação
     * @return ObjectAnimator configurado com a animação de fade in
     */
    private ObjectAnimator criarFadeInAnimator(android.view.View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        fadeIn.setDuration(ANIMATION_DURATION);
        fadeIn.setStartDelay(0);
        return fadeIn;
    }

    /**
     * Inicia a verificação de login com delay.
     * Aguarda a conclusão das animações antes de verificar o estado do login.
     */
    private void iniciarVerificacaoLogin() {
        new Handler(Looper.getMainLooper()).postDelayed(
            this::verificarUsuarioLogado, 
            DELAY_DURATION
        );
    }

    /**
     * Verifica se existe um usuário logado e redireciona adequadamente.
     * Se houver usuário logado, vai para MainPainel, caso contrário para MainIntroducao.
     */
    private void verificarUsuarioLogado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
            intent = new Intent(this, MainPainel.class);
        } else {
            intent = new Intent(this, MainIntroducao.class);
        }

        startActivity(intent);
        finish();
    }

    /**
     * Define a cor da barra de status do sistema.
     * Utiliza a cor definida na constante STATUS_BAR_COLOR.
     */
    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor(STATUS_BAR_COLOR));
    }

    /**
     * Define a cor da barra de navegação do sistema.
     * Utiliza a mesma cor da barra de status para manter consistência visual.
     */
    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor(STATUS_BAR_COLOR));
    }
}
