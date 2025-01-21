package com.example.estetify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainConectar extends AppCompatActivity {
    
    private CheckBox mostrarSenha;
    private EditText campoSenha;
    private EditText campoEmail;
    private MaterialButton botaoConectar;
    private ProgressBar loadingConectar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectar);

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

        // Inicialização dos elementos
        mostrarSenha = findViewById(R.id.mostrar_senha);
        campoSenha = findViewById(R.id.campo_senha);
        campoEmail = findViewById(R.id.campo_email);
        botaoConectar = findViewById(R.id.botao_conectar);
        loadingConectar = findViewById(R.id.loading_conectar);
        ImageView iconeEmail = findViewById(R.id.icone_email);
        ImageView iconeSenha = findViewById(R.id.icone_senha);

        // Cor azul e cinza dos ícones
        int corAzul = Color.parseColor("#2196F3");
        int corCinza = Color.parseColor("#FFFFFF");

        // Listener para o campo de email
        campoEmail.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            MaterialButton corpoEmail = findViewById(R.id.corpo_email);
            corpoEmail.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainConectar.this, color)));
            if (hasFocus) {
                iconeEmail.setColorFilter(corAzul);
            } else {
                iconeEmail.setColorFilter(corCinza);
            }
        });

        // Listener para o campo de senha
        campoSenha.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            MaterialButton corpoSenha = findViewById(R.id.corpo_senha);
            corpoSenha.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainConectar.this, color)));
            if (hasFocus) {
                iconeSenha.setColorFilter(corAzul);
            } else {
                iconeSenha.setColorFilter(corCinza);
            }
        });

        // Listener para a CheckBox
        mostrarSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                campoSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                campoSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            
            android.graphics.Typeface interFont = ResourcesCompat.getFont(MainConectar.this, R.font.fonte_inter);
            campoSenha.setTypeface(interFont);
            campoSenha.setSelection(campoSenha.getText().length());
        });

        configurarEfeitoBotao(botaoConectar);

        // Configurar click do botão conectar
        botaoConectar.setOnClickListener(v -> tentarLogin());
    }

    private void tentarLogin() {
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();

        if (!validarCampos(email, senha)) {
            return;
        }

        // Mostrar loading e desabilitar botão
        loadingConectar.setVisibility(View.VISIBLE);
        botaoConectar.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login bem sucedido
                        Toast.makeText(MainConectar.this, 
                            "Login realizado com sucesso", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Redirecionar para o MainPainel
                        Intent intent = new Intent(MainConectar.this, MainPainel.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Esconder loading e reabilitar botão
                        loadingConectar.setVisibility(View.GONE);
                        botaoConectar.setEnabled(true);

                        // Tratar erros específicos
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            campoEmail.setError("Usuário não encontrado");
                            campoEmail.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            campoSenha.setError("Senha incorreta");
                            campoSenha.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(MainConectar.this,
                                "Erro ao fazer login: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean validarCampos(String email, String senha) {
        if (email.isEmpty()) {
            campoEmail.setError("Digite seu email");
            campoEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            campoEmail.setError("Email inválido");
            campoEmail.requestFocus();
            return false;
        }

        if (senha.isEmpty()) {
            campoSenha.setError("Digite sua senha");
            campoSenha.requestFocus();
            return false;
        }

        return true;
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
}
