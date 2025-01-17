package com.example.estetify;

import android.graphics.Color; // Classe para manipulação de cores
import android.os.Bundle; // Classe para gerenciar o estado da atividade

import androidx.appcompat.app.AppCompatActivity; // Classe base para atividades que utilizam a biblioteca de compatibilidade
import androidx.core.graphics.Insets; // Classe para lidar com insets (espaços ocupados pelas barras do sistema)
import androidx.core.view.ViewCompat; // Classe que fornece métodos para manipulação de Views
import androidx.core.view.WindowInsetsCompat; // Classe que fornece suporte para insets de janelas

// Classe principal da atividade de apresentação
public class MainRegistrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o metodo onCreate da classe base (superclasse)

        setContentView(R.layout.activity_registrar); // Define o layout da atividade usando o arquivo XML 'activity_apresentacao'

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

        // Ajusta o padding da View principal para que o conteúdo não ocupe o espaço das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Retorna os insets para que o sistema continue o processamento normal de insets
        });
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
