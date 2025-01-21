package com.example.estetify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color; // Classe para manipulação de cores
import android.os.Bundle; // Classe para gerenciar o estado da atividade
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity; // Classe base para atividades que utilizam a biblioteca de compatibilidade
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets; // Classe para lidar com insets (espaços ocupados pelas barras do sistema)
import androidx.core.view.ViewCompat; // Classe que fornece métodos para manipulação de Views
import androidx.core.view.WindowInsetsCompat; // Classe que fornece suporte para insets de janelas

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

// Classe principal da atividade de apresentação
public class MainRegistrar extends AppCompatActivity {

    private EditText campoNomeCompleto; // Declaração do EditText para o nome completo
    private EditText campoEmail;
    private EditText campoSenha; // Declaração do EditText para a senha
    private EditText campoRepetirSenha;
    private CheckBox mostrarSenha; // Declaração da CheckBox para mostrar/ocultar senhas
    private FirebaseAuth mAuth;
    private ProgressBar loadingRegistrar;
    private MaterialButton botaoCriar;
    private ImageView iconeNome;
    private ImageView iconeEmail;
    private ImageView iconeSenha;
    private ImageView iconeRepetirSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Chama o metodo onCreate da classe base (superclasse)

        setContentView(R.layout.activity_registrar); // Define o layout da atividade usando o arquivo XML 'activity_apresentacao'

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Muda a cor da barra de status e da barra de navegação
        changeStatusBarColor();
        changeNavigationBarColor();

       // Ajusta o padding da View principal para que o conteúdo não ocupe o espaço das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Retorna os insets para que o sistema continue o processamento normal de insets
        });

        // Inicialização dos campos
        campoNomeCompleto = findViewById(R.id.campo_nome_completo);
        campoEmail = findViewById(R.id.campo_email);
        campoSenha = findViewById(R.id.campo_senha);
        campoRepetirSenha = findViewById(R.id.campo_repetir_senha);
        mostrarSenha = findViewById(R.id.mostrar_senha);
        loadingRegistrar = findViewById(R.id.loading_registrar);
        botaoCriar = findViewById(R.id.botao_criar);
        
        // Corrigindo os IDs dos ícones
        iconeNome = findViewById(R.id.icone_nome_completo);
        iconeEmail = findViewById(R.id.icone_email);
        iconeSenha = findViewById(R.id.icone_senha);
        iconeRepetirSenha = findViewById(R.id.icone_repetir_senha);

        // Configurar o listener do CheckBox mostrar senha
        mostrarSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Muda o tipo de entrada para texto visível em ambos os campos
                campoSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                campoRepetirSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Muda o tipo de entrada para texto oculto em ambos os campos
                campoSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                campoRepetirSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            
            // Mantém a fonte Inter após mudar o tipo de input
            android.graphics.Typeface interFont = ResourcesCompat.getFont(MainRegistrar.this, R.font.fonte_inter);
            campoSenha.setTypeface(interFont);
            campoRepetirSenha.setTypeface(interFont);
            
            // Move o cursor para o final do texto em ambos os campos
            campoSenha.setSelection(campoSenha.getText().length());
            campoRepetirSenha.setSelection(campoRepetirSenha.getText().length());
        });

        // Inicialização dos MaterialButtons
        MaterialButton corpoNomeCompleto = findViewById(R.id.corpo_nome_completo);
        MaterialButton corpoEmail = findViewById(R.id.corpo_email);
        MaterialButton corpoSenha = findViewById(R.id.corpo_senha);
        MaterialButton corpoRepetirSenha = findViewById(R.id.corpo_repetir_senha);

        configurarEfeitoBotao(botaoCriar);

        configurarValidacaoNome(botaoCriar);

        configurarValidacaoEmail();

        configurarValidacaoSenha();

        // Cor azul e cinza dos ícones
        int corAzul = Color.parseColor("#2196F3");
        int corCinza = Color.parseColor("#FFFFFF");

        // Listener para o campo de nome completo
        campoNomeCompleto.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            corpoNomeCompleto.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainRegistrar.this, color)));
            if (hasFocus) {
                iconeNome.setColorFilter(corAzul);
            } else {
                iconeNome.setColorFilter(corCinza);
            }
        });

        // Listener para o campo de email
        campoEmail.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            corpoEmail.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainRegistrar.this, color)));
            if (hasFocus) {
                iconeEmail.setColorFilter(corAzul);
            } else {
                iconeEmail.setColorFilter(corCinza);
            }
        });

        // Listener para o campo de senha
        campoSenha.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            corpoSenha.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainRegistrar.this, color)));
            if (hasFocus) {
                iconeSenha.setColorFilter(corAzul);
            } else {
                iconeSenha.setColorFilter(corCinza);
            }
        });

        // Listener para o campo de repetir senha
        campoRepetirSenha.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            corpoRepetirSenha.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainRegistrar.this, color)));
            if (hasFocus) {
                iconeRepetirSenha.setColorFilter(corAzul);
            } else {
                iconeRepetirSenha.setColorFilter(corCinza);
            }
        });

        // Configurar click do botão criar
        botaoCriar.setOnClickListener(v -> tentarRegistrar());
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

    // Método para configurar e validar nome completo
    private void configurarValidacaoNome(MaterialButton botaoCriar) {
        // Validação em tempo real
        campoNomeCompleto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString();
                String textoLimpo = texto.replaceAll("[^a-zA-ZÀ-ÿ\\s]", "");
                
                if (!texto.equals(textoLimpo)) {
                    s.replace(0, s.length(), textoLimpo);
                    campoNomeCompleto.setSelection(textoLimpo.length());
                }

                // Validar em tempo real
                if (!textoLimpo.isEmpty()) {
                    String[] partes = textoLimpo.split("\\s+");
                    if (partes.length < 2) {
                        campoNomeCompleto.setError("Digite seu nome e sobrenome");
                    } else {
                        campoNomeCompleto.setError(null);
                    }
                }
            }
        });

        // Configurar validação no botão criar
        botaoCriar.setOnClickListener(v -> {
            if (!validarTodosCampos()) {
                return;
            }
            
            // Se chegou aqui, todos os campos são válidos
            // Aqui você pode adicionar a próxima validação ou ação
        });
    }

    // Método para configurar e validar email
    private void configurarValidacaoEmail() {
        // Validação em tempo real
        campoEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                // Remove espaços em branco
                if (email.contains(" ")) {
                    String emailSemEspaco = email.replace(" ", "");
                    s.replace(0, s.length(), emailSemEspaco);
                    campoEmail.setSelection(emailSemEspaco.length());
                }

                // Validar formato do email em tempo real
                if (!email.isEmpty()) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        campoEmail.setError("E-mail inválido");
                    } else {
                        campoEmail.setError(null);
                    }
                }
            }
        });
    }

    // Metodo para validar todos os campos
    private boolean validarTodosCampos() {
        boolean nomeValido = validarNomeCompleto();
        boolean emailValido = validarEmail();
        boolean senhaValida = validarForcaSenha(campoSenha.getText().toString());
        boolean confirmacaoValida = validarConfirmacaoSenha();

        if (nomeValido && emailValido && senhaValida && confirmacaoValida) {
            return true;
        }
        return false;
    }

    // Método para validar nome completo
    private boolean validarNomeCompleto() {
        String nome = campoNomeCompleto.getText().toString().trim();
        
        if (nome.isEmpty()) {
            campoNomeCompleto.setError("Nome completo é obrigatório");
            return false;
        }
        
        String[] partes = nome.split("\\s+");
        if (partes.length < 2) {
            campoNomeCompleto.setError("Digite seu nome e sobrenome");
            return false;
        }
        
        return true;
    }

    // Método para configurar e validar senha
    private void configurarValidacaoSenha() {
        // Validação em tempo real da força da senha
        campoSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String senha = s.toString();
                if (!senha.isEmpty()) {
                    validarForcaSenha(senha);
                }
            }
        });

        // Validação em tempo real se as senhas coincidem
        campoRepetirSenha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String senha = campoSenha.getText().toString();
                String confirmacao = s.toString();
                if (!confirmacao.isEmpty() && !senha.equals(confirmacao)) {
                    campoRepetirSenha.setError("As senhas não coincidem");
                }
            }
        });
    }

    // Método auxiliar para validar email
    private boolean validarEmail() {
        String email = campoEmail.getText().toString().trim();
        
        if (email.isEmpty()) {
            campoEmail.setError("E-mail é obrigatório");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            campoEmail.setError("E-mail inválido");
            return false;
        }

        return true;
    }

    // Método para validar força da senha
    private boolean validarForcaSenha(String senha) {
        if (senha.length() < 6) {
            campoSenha.setError("A senha deve ter pelo menos 6 caracteres");
            return false;
        }

        boolean temNumero = senha.matches(".*\\d.*");
        boolean temLetraMinuscula = senha.matches(".*[a-z].*");
        boolean temLetraMaiuscula = senha.matches(".*[A-Z].*");
        boolean temCaracterEspecial = senha.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        if (!temNumero || !temLetraMinuscula || !temLetraMaiuscula || !temCaracterEspecial) {
            campoSenha.setError("A senha deve conter números, letras maiúsculas, minúsculas e caracteres especiais");
            return false;
        }

        return true;
    }

    // Método para validar se as senhas coincidem
    private boolean validarConfirmacaoSenha() {
        String senha = campoSenha.getText().toString();
        String confirmacao = campoRepetirSenha.getText().toString();

        if (confirmacao.isEmpty()) {
            campoRepetirSenha.setError("Confirme sua senha");
            return false;
        }

        if (!senha.equals(confirmacao)) {
            campoRepetirSenha.setError("As senhas não coincidem");
            return false;
        }

        return true;
    }

    private void tentarRegistrar() {
        if (!validarTodosCampos()) {
            return;
        }

        // Mostrar loading e desabilitar botão
        loadingRegistrar.setVisibility(View.VISIBLE);
        botaoCriar.setEnabled(false);

        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();
        String nome = campoNomeCompleto.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro bem sucedido
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            atualizarPerfilUsuario(user, nome);
                        } else {
                            // Caso improvável, mas precisamos tratar
                            loadingRegistrar.setVisibility(View.GONE);
                            botaoCriar.setEnabled(true);
                            Toast.makeText(MainRegistrar.this, 
                                "Erro ao criar usuário. Tente novamente.", 
                                Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Esconder loading e reabilitar botão
                        loadingRegistrar.setVisibility(View.GONE);
                        botaoCriar.setEnabled(true);

                        // Tratar erros específicos
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            campoSenha.setError("Senha muito fraca");
                            campoSenha.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            campoEmail.setError("Email inválido");
                            campoEmail.requestFocus();
                        } catch (FirebaseAuthUserCollisionException e) {
                            campoEmail.setError("Este email já está em uso");
                            campoEmail.requestFocus();
                        } catch (Exception e) {
                            Toast.makeText(MainRegistrar.this, 
                                "Erro no registro: " + e.getMessage(), 
                                Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void atualizarPerfilUsuario(FirebaseUser user, String nome) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    // Esconder loading e reabilitar botão
                    loadingRegistrar.setVisibility(View.GONE);
                    botaoCriar.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(MainRegistrar.this, 
                            "Registro realizado com sucesso!", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Redirecionar para MainPainel
                        Intent intent = new Intent(MainRegistrar.this, MainPainel.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(MainRegistrar.this,
                            "Erro ao atualizar perfil", 
                            Toast.LENGTH_SHORT).show();
                    }
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
