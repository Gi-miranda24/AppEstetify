package com.example.estetify;

// Importa as classes necessárias para animações e manipulação de UI
import android.animation.AnimatorSet; // Classe que permite combinar múltiplos animadores
import android.animation.ObjectAnimator; // Classe para animações de propriedades de objetos
import android.content.Intent; // Classe para gerenciar intenções de navegação entre atividades
import android.graphics.Color; // Classe para manipulação de cores
import android.os.Bundle; // Classe para gerenciar o estado da atividade
import android.os.Handler; // Classe para agendar tarefas a serem executadas em um determinado tempo
import android.os.Looper; // Classe que fornece acesso ao loop de mensagens do thread principal
import android.widget.Button; // Classe para manipulação de botões
import android.widget.ImageView; // Classe para manipulação de imagens
import android.widget.TextView; // Classe para manipulação de textos

import androidx.appcompat.app.AppCompatActivity; // Classe base para atividades que utilizam a biblioteca de compatibilidade
import androidx.core.graphics.Insets; // Classe para lidar com insets (espaços ocupados pelas barras do sistema)
import androidx.core.view.ViewCompat; // Classe que fornece métodos para manipulação de Views
import androidx.core.view.WindowInsetsCompat; // Classe que fornece suporte para insets de janelas

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Classe principal da atividade de apresentação
public class MainApresentacao extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o metodo onCreate da classe base (superclasse)

        setContentView(R.layout.activity_apresentacao); // Define o layout da atividade usando o arquivo XML 'activity_apresentacao'

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal para que o conteúdo não ocupe o espaço das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Retorna os insets para que o sistema continue o processamento normal de insets
        });

        // Inicializa os elementos de interface
        ImageView logotipo = findViewById(R.id.logotipo);
        TextView texto_rodape = findViewById(R.id.texto_rodape);

        // Cria animações para os elementos
        ObjectAnimator fadeInLogoTipo = ObjectAnimator.ofFloat(logotipo, "alpha", 0f, 1f);
        fadeInLogoTipo.setDuration(500);
        fadeInLogoTipo.setStartDelay(0);

        ObjectAnimator fadeInTextoRodape = ObjectAnimator.ofFloat(texto_rodape, "alpha", 0f, 1f);
        fadeInTextoRodape.setDuration(500);
        fadeInTextoRodape.setStartDelay(0);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeInLogoTipo, fadeInTextoRodape);
        animatorSet.start(); // Inicia as animações

        mAuth = FirebaseAuth.getInstance();

        // Atrasar a verificação de login
        new Handler(Looper.getMainLooper()).postDelayed(() -> verificarUsuarioLogado(), 1000); // 1 segundo de atraso
    }

    private void verificarUsuarioLogado() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usuário está logado, redirecionar para MainPainel
            startActivity(new Intent(MainApresentacao.this, MainPainel.class));
            finish();
        } else {
            // Usuário não está logado, permanecer na tela de apresentação
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(MainApresentacao.this, MainIntroducao.class);
                startActivity(intent); // Inicia a próxima atividade
                finish(); // Finaliza a atividade atual
            }, 1000); // 1000 ms = 1 segundo
        }
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
