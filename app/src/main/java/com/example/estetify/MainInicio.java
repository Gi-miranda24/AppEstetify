package com.example.estetify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color; // Importa a classe Color para manipulação de cores
import android.os.Bundle; // Importa a classe Bundle para gerenciar o estado da atividade
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView; // Importa a classe ImageView para manipulação de imagens

import androidx.appcompat.app.AppCompatActivity; // Importa a classe base AppCompatActivity, que é a atividade principal com suporte a compatibilidade
import androidx.core.content.ContextCompat; // Importa a classe ContextCompat para acessar funcionalidades avançadas para contexto
import androidx.core.graphics.Insets; // Importa a classe Insets para lidar com os insets da janela (espaços das barras do sistema)
import androidx.core.view.ViewCompat; // Importa a classe ViewCompat para acessar funcionalidades avançadas para Views
import androidx.core.view.WindowInsetsCompat; // Importa a classe WindowInsetsCompat para obter os insets (espaço ocupado pelas barras do sistema)
import com.google.android.material.button.MaterialButton; // Importa a classe MaterialButton para manipulação de botões

// Classe principal da atividade que estende AppCompatActivity
public class MainInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o metodo onCreate da classe base (superclasse)

        setContentView(R.layout.activity_inicio); // Define o layout da atividade usando o arquivo XML 'activity_inicio'

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal para que o conteúdo não ocupe o espaço das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            // Obtém os insets (espaços ocupados pelas barras do sistema, como a barra de status e a barra de navegação)
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Aplica o padding necessário para garantir que o conteúdo não se sobreponha às barras do sistema
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Retorna os insets para que o sistema continue o processamento normal de insets
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

    // Função para mudar a cor da barra de status
    private void changeStatusBarColor() {
        // Define a cor da barra de status para o código de cor #2C3E50 (um tom de azul escuro)
        getWindow().setStatusBarColor(Color.parseColor("#2C3E50"));
    }

    // Função para mudar a cor da barra de navegação
    private void changeNavigationBarColor() {
        // Define a cor da barra de navegação para o código de cor #2C3E50 (um tom de azul escuro)
        getWindow().setNavigationBarColor(Color.parseColor("#2C3E50"));
    }
}