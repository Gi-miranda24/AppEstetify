package com.example.estetify;

// Importações necessárias para a funcionalidade da atividade
import android.graphics.Color; // Para manipulação de cores
import android.os.Bundle; // Para gerenciar o estado da atividade
import android.widget.Button; // Para manipulação de botões
import android.widget.ImageView; // Para manipulação de imagens
import android.widget.TextView; // Para manipulação de textos
import android.content.Intent; // Para manipulação de intents
import android.content.SharedPreferences; // Para armazenamento de dados persistentes

import androidx.appcompat.app.AppCompatActivity; // Classe base para atividades com suporte a compatibilidade
import androidx.core.graphics.Insets; // Para lidar com os insets da janela
import androidx.core.view.ViewCompat; // Para acessar funcionalidades avançadas para Views
import androidx.core.view.WindowInsetsCompat; // Para obter os insets ocupados pelas barras do sistema

// Classe principal da atividade de introdução
public class MainIntroducao extends AppCompatActivity {

    // Declaração das variáveis de interface
    private Button botaoPular, botaoVoltar, botaoAvancar, fundoCaixa; // Botões de navegação
    private ImageView apresentacaoImageUm, apresentacaoImageDois; // Imagens a serem exibidas
    private TextView textoProdutos, textoAgendamentos, tituloProdutos, tituloAgendamentos; // Textos a serem exibidos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o metodo onCreate da classe base

        // Verifica se a introdução já foi exibida
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean isIntroducaoExibida = prefs.getBoolean("isIntroducaoExibida", false);
        
        if (isIntroducaoExibida) {
            // Se já foi exibida, inicia a tela MainInicio
            Intent intent = new Intent(this, MainInicio.class);
            startActivity(intent);
            finish(); // Encerra a atividade atual
            return; // Sai do metodo
        }

        // Configuração da barra de status e layout
        setContentView(R.layout.activity_introducao); // Define o layout da atividade

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal para que o conteúdo não ocupe o espaço das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Obtém os insets das barras do sistema
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Aplica o padding necessário
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Retorna os insets
            return insets;
        });

        // Inicialização dos elementos de interface
        botaoPular = findViewById(R.id.botao_pular);
        apresentacaoImageUm = findViewById(R.id.apresentacao_image_um);
        apresentacaoImageDois = findViewById(R.id.apresentacao_image_dois);
        textoProdutos = findViewById(R.id.texto_produtos);
        textoAgendamentos = findViewById(R.id.texto_agendamentos);
        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoAvancar = findViewById(R.id.botao_avancar);
        tituloProdutos = findViewById(R.id.titulo_produtos);
        tituloAgendamentos = findViewById(R.id.titulo_agendamentos);
        fundoCaixa = findViewById(R.id.fundo_caixa);

        // Chama o metodo para fazer apresentacaoImageUm e textoProdutos aparecerem com animação de fade-in
        chamarIntroducaoUm(); // Chama o metodo para iniciar a animação de introdução
    }

    // Função para mudar a cor da barra de status
    private void changeStatusBarColor() {
        // Define a cor da barra de status
        getWindow().setStatusBarColor(Color.parseColor("#2C3E50"));
    }

    // Função para mudar a cor da barra de navegação
    private void changeNavigationBarColor() {
        // Define a cor da barra de navegação
        getWindow().setNavigationBarColor(Color.parseColor("#2C3E50"));
    }

    // Função para fazer apresentacaoImageUm e textoProdutos aparecerem com animação de fade-in
    private void chamarIntroducaoUm() {
        // Desativar os botões para cliques
        botaoVoltar.setEnabled(false);
        botaoAvancar.setEnabled(false);
        botaoPular.setEnabled(false);
        
        // Atraso de 1000ms usando View.postDelayed()
        findViewById(R.id.main).postDelayed(() -> {
            // Fade-in para apresentacaoImageUm
            tituloProdutos.animate().alpha(1f).setDuration(500).start();
            // Fade-in para apresentacaoImageUm
            apresentacaoImageUm.animate().alpha(1f).setDuration(500).start();
            // Fade-in para fundoApresentacao
            fundoCaixa.animate().alpha(1f).setDuration(500).start();
            // Fade-in para textoProdutos
            textoProdutos.animate().alpha(1f).setDuration(500).start();

            // Chamar o próximo metodo aqui
            chamarElementos(); // Chama o metodo para configurar os elementos
        }, 0); // Atraso de 0
    }

    // Função para fazer apresentacaoImageUm e textoProdutos aparecerem com animação de fade-in
    private void chamarElementos() {
        // Desativar os botões para cliques
        botaoVoltar.setEnabled(false);
        botaoAvancar.setEnabled(false);
        botaoPular.setEnabled(false);
        // Atraso de 0ms usando View.postDelayed()
        findViewById(R.id.main).postDelayed(() -> {
            // Fade-in para botaoAvancar
            botaoAvancar.animate().alpha(1f).setDuration(500).withEndAction(() -> {
                // Habilitar botaoAvancar após a animação
                botaoAvancar.setEnabled(true);
            }).start();

            // Fade-in para botaoPular
            botaoPular.animate().alpha(1f).setDuration(500).withEndAction(() -> {
                // Habilitar botaoPular após a animação
                botaoPular.setEnabled(true);
            }).start();

            // Configurando eventos de clique
            botaoPular.setOnClickListener(v -> pularIntroducao());

            botaoAvancar.setOnClickListener(v -> fecharIntroducaoUm());
        }, 0); // Atraso de 0ms
    }

    // Função para fechar a introdução
    private void fecharIntroducaoUm() {
        // Desativar os botões para cliques
        botaoAvancar.setEnabled(false);
        botaoPular.setEnabled(false);

        // Fade-out para apresentacaoImageUm, textoProdutos, botaoPular e botaoAvancar
        tituloProdutos.animate().alpha(0f).setDuration(500).start();
        apresentacaoImageUm.animate().alpha(0f).setDuration(500).start();
        fundoCaixa.animate().alpha(0f).setDuration(500).start();
        textoProdutos.animate().alpha(0f).setDuration(500).start();
        botaoPular.animate().alpha(0f).setDuration(500).start();
        botaoAvancar.animate().alpha(0f).setDuration(500).withEndAction(this::chamarIntroducaoDois).start();
    }

    // Função para fazer apresentacaoImageDois e textoAgendamentos aparecerem com animação de fade-in
    private void chamarIntroducaoDois() {
        // Atraso de 0ms usando View.postDelayed()
        findViewById(R.id.main).postDelayed(() -> {
            // Fade-in para apresentacaoImageDois
            tituloAgendamentos.animate().alpha(1f).setDuration(500).start();
            // Fade-in para apresentacaoImageDois
            apresentacaoImageDois.animate().alpha(1f).setDuration(500).start();
            // Fade-in para textoAgendamentos
            textoAgendamentos.animate().alpha(1f).setDuration(500).start();
            // Fade-in para fundoApresentacao
            fundoCaixa.animate().alpha(1f).setDuration(500).start();

            // Chamar o próximo metodo aqui
            chamarElementosNovamente();
        }, 0); // Atraso de 0ms
    }

    // Função para chamar os elementos novamente após um atraso
    private void chamarElementosNovamente() {
        // Atraso de 0ms usando View.postDelayed()
        findViewById(R.id.main).postDelayed(() -> {
            // Fade-in para botaoVoltar
            botaoVoltar.animate().alpha(1f).setDuration(500).withEndAction(this::habilitarBotaoVoltar).start();

            // Fade-in para botaoAvancar
            botaoAvancar.animate().alpha(1f).setDuration(500).withEndAction(this::habilitarBotaoAvancar).start();

            // Fade-in para botaoPular
            botaoPular.animate().alpha(1f).setDuration(500).withEndAction(this::habilitarBotaoPular).start();

            // Configurando eventos de clique
            botaoVoltar.setOnClickListener(v -> fecharIntroducaoDois());

            botaoPular.setOnClickListener(v -> pularIntroducao());

            botaoAvancar.setOnClickListener(v -> pularIntroducao());
        }, 0); // Atraso de 0ms
    }

    private void fecharIntroducaoDois() {
        // Desativar os botões para cliques
        botaoVoltar.setEnabled(false);
        botaoAvancar.setEnabled(false);
        botaoPular.setEnabled(false);

        // Fade-out para apresentacaoImageDois, textoAgendamentos, botaoVoltar, botaoPular e botaoAvancar
        tituloAgendamentos.animate().alpha(0f).setDuration(500).start();
        apresentacaoImageDois.animate().alpha(0f).setDuration(500).start();
        textoAgendamentos.animate().alpha(0f).setDuration(500).start();
        fundoCaixa.animate().alpha(0f).setDuration(500).start();
        botaoVoltar.animate().alpha(0f).setDuration(500).start();
        botaoPular.animate().alpha(0f).setDuration(500).start();
        botaoAvancar.animate().alpha(0f).setDuration(500).withEndAction(this::chamarIntroducaoUm).start();
    }

    private void pularIntroducao() {
        // Armazena que a introdução foi exibida
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isIntroducaoExibida", true);
        editor.apply();

        // Encerrar a atividade atual
        finish();
        
        // Iniciar a tela MainInicio
        Intent intent = new Intent(this, MainInicio.class);
        startActivity(intent);
    }

    private void habilitarBotaoVoltar() {
        botaoVoltar.setEnabled(true);
    }

    private void habilitarBotaoAvancar() {
        botaoAvancar.setEnabled(true);
    }

    private void habilitarBotaoPular() {
        botaoPular.setEnabled(true);
    }
}