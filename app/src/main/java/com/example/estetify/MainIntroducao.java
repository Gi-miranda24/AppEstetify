package com.example.estetify;

// Imports de Android Core
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

// Imports de Views
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

// Imports de AndroidX
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainIntroducao extends AppCompatActivity {
    // Constantes
    private static final int ANIMATION_DURATION = 500;
    private static final int ANIMATION_DELAY = 0;
    private static final String STATUS_BAR_COLOR = "#2C3E50";
    private static final String PREFS_NAME = "app_prefs";
    private static final String PREF_INTRODUCAO_EXIBIDA = "isIntroducaoExibida";

    // Views - Botões
    private Button botaoPular;
    private Button botaoVoltar;
    private Button botaoAvancar;

    // Views - Imagens
    private ImageView apresentacaoImageUm;
    private ImageView apresentacaoImageDois;

    // Views - Textos
    private TextView textoProdutos;
    private TextView textoAgendamentos;
    private TextView tituloProdutos;
    private TextView tituloAgendamentos;

    // Views - Layout
    private LinearLayout linearLayout;

    /**
     * Método do ciclo de vida chamado quando a Activity é criada.
     * Verifica se a introdução já foi exibida e configura a interface inicial.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (verificarIntroducaoJaExibida()) {
            navegarParaInicio();
            return;
        }

        setContentView(R.layout.activity_introducao);
        configurarBarrasSistema();
        inicializarViews();
        iniciarIntroducao();
    }

    /**
     * Verifica nas preferências se a introdução já foi exibida anteriormente.
     * @return true se a introdução já foi exibida, false caso contrário
     */
    private boolean verificarIntroducaoJaExibida() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(PREF_INTRODUCAO_EXIBIDA, false);
    }

    /**
     * Navega para a tela inicial do aplicativo e finaliza a atual.
     */
    private void navegarParaInicio() {
        startActivity(new Intent(this, MainInicio.class));
        finish();
    }

    /**
     * Configura as barras do sistema (status e navegação).
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
        getWindow().setNavigationBarColor(Color.parseColor(STATUS_BAR_COLOR));
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
     * Inicializa todas as referências das views utilizadas na tela.
     */
    private void inicializarViews() {
        // Botões
        botaoPular = findViewById(R.id.botao_pular);
        botaoVoltar = findViewById(R.id.botao_voltar);
        botaoAvancar = findViewById(R.id.botao_avancar);

        // Imagens
        apresentacaoImageUm = findViewById(R.id.apresentacao_image_um);
        apresentacaoImageDois = findViewById(R.id.apresentacao_image_dois);

        // Textos
        textoProdutos = findViewById(R.id.texto_produtos);
        textoAgendamentos = findViewById(R.id.texto_agendamentos);
        tituloProdutos = findViewById(R.id.titulo_produtos);
        tituloAgendamentos = findViewById(R.id.titulo_agendamentos);

        // Layout
        linearLayout = findViewById(R.id.linear_layout);
    }

    /**
     * Inicia a sequência de introdução chamando a primeira tela.
     */
    private void iniciarIntroducao() {
        chamarIntroducaoUm();
    }

    /**
     * Prepara e exibe a primeira tela da introdução.
     */
    private void chamarIntroducaoUm() {
        desabilitarBotoes();
        
        findViewById(R.id.main).postDelayed(() -> {
            animarElementosIntroducaoUm();
            chamarElementos();
        }, ANIMATION_DELAY);
    }

    /**
     * Anima os elementos da primeira tela de introdução com fade in.
     */
    private void animarElementosIntroducaoUm() {
        tituloProdutos.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        apresentacaoImageUm.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        linearLayout.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        textoProdutos.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
    }

    /**
     * Chama os elementos comuns (botões) após a animação inicial.
     */
    private void chamarElementos() {
        desabilitarBotoes();
        
        findViewById(R.id.main).postDelayed(() -> {
            animarBotoes();
            configurarClickListeners();
        }, ANIMATION_DELAY);
    }

    /**
     * Anima os botões com fade in.
     */
    private void animarBotoes() {
        botaoAvancar.animate().alpha(1f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::habilitarBotaoAvancar).start();
        botaoPular.animate().alpha(1f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::habilitarBotaoPular).start();
    }

    /**
     * Configura os listeners de clique dos botões da primeira tela.
     */
    private void configurarClickListeners() {
        botaoPular.setOnClickListener(v -> pularIntroducao());
        botaoAvancar.setOnClickListener(v -> fecharIntroducaoUm());
    }

    /**
     * Inicia o processo de fechamento da primeira tela de introdução.
     */
    private void fecharIntroducaoUm() {
        desabilitarBotoes();
        animarFadeOutIntroducaoUm();
    }

    /**
     * Anima a saída dos elementos da primeira tela com fade out.
     */
    private void animarFadeOutIntroducaoUm() {
        tituloProdutos.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        apresentacaoImageUm.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        linearLayout.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        textoProdutos.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        botaoPular.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        botaoAvancar.animate().alpha(0f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::chamarIntroducaoDois).start();
    }

    /**
     * Prepara e exibe a segunda tela da introdução.
     */
    private void chamarIntroducaoDois() {
        findViewById(R.id.main).postDelayed(() -> {
            animarElementosIntroducaoDois();
            chamarElementosNovamente();
        }, ANIMATION_DELAY);
    }

    /**
     * Anima os elementos da segunda tela de introdução com fade in.
     */
    private void animarElementosIntroducaoDois() {
        tituloAgendamentos.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        apresentacaoImageDois.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        textoAgendamentos.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
        linearLayout.animate().alpha(1f).setDuration(ANIMATION_DURATION).start();
    }

    /**
     * Chama os elementos comuns (botões) para a segunda tela.
     */
    private void chamarElementosNovamente() {
        findViewById(R.id.main).postDelayed(() -> {
            animarBotoesNovamente();
            configurarClickListenersNovamente();
        }, ANIMATION_DELAY);
    }

    /**
     * Anima os botões da segunda tela com fade in.
     */
    private void animarBotoesNovamente() {
        botaoVoltar.animate().alpha(1f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::habilitarBotaoVoltar).start();
        botaoAvancar.animate().alpha(1f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::habilitarBotaoAvancar).start();
        botaoPular.animate().alpha(1f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::habilitarBotaoPular).start();
    }

    /**
     * Configura os listeners de clique dos botões da segunda tela.
     */
    private void configurarClickListenersNovamente() {
        botaoVoltar.setOnClickListener(v -> fecharIntroducaoDois());
        botaoPular.setOnClickListener(v -> pularIntroducao());
        botaoAvancar.setOnClickListener(v -> pularIntroducao());
    }

    /**
     * Inicia o processo de fechamento da segunda tela de introdução.
     */
    private void fecharIntroducaoDois() {
        desabilitarBotoes();
        animarFadeOutIntroducaoDois();
    }

    /**
     * Anima a saída dos elementos da segunda tela com fade out.
     */
    private void animarFadeOutIntroducaoDois() {
        tituloAgendamentos.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        apresentacaoImageDois.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        textoAgendamentos.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        linearLayout.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        botaoVoltar.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        botaoPular.animate().alpha(0f).setDuration(ANIMATION_DURATION).start();
        botaoAvancar.animate().alpha(0f).setDuration(ANIMATION_DURATION)
            .withEndAction(this::chamarIntroducaoUm).start();
    }

    /**
     * Finaliza a introdução e navega para a tela inicial.
     */
    private void pularIntroducao() {
        salvarIntroducaoExibida();
        navegarParaInicio();
    }

    /**
     * Salva nas preferências que a introdução já foi exibida.
     */
    private void salvarIntroducaoExibida() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PREF_INTRODUCAO_EXIBIDA, true);
        editor.apply();
    }

    /**
     * Desabilita todos os botões da interface.
     */
    private void desabilitarBotoes() {
        botaoVoltar.setEnabled(false);
        botaoAvancar.setEnabled(false);
        botaoPular.setEnabled(false);
    }

    /**
     * Habilita o botão de voltar.
     */
    private void habilitarBotaoVoltar() {
        botaoVoltar.setEnabled(true);
    }

    /**
     * Habilita o botão de avançar.
     */
    private void habilitarBotaoAvancar() {
        botaoAvancar.setEnabled(true);
    }

    /**
     * Habilita o botão de pular.
     */
    private void habilitarBotaoPular() {
        botaoPular.setEnabled(true);
    }
}