package com.example.estetify;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainRegistrar extends AppCompatActivity {

    // Constantes
    private static final String COR_AZUL = "#2196F3";
    private static final String COR_CINZA = "#FFFFFF";
    private static final String COR_BARRA_SISTEMA = "#2C3E50";

    // Campos de entrada
    private EditText campoNomeCompleto;
    private EditText campoEmail;
    private EditText campoSenha;
    private EditText campoRepetirSenha;
    private CheckBox mostrarSenha;

    // Elementos UI
    private ProgressBar loadingRegistrar;
    private MaterialButton botaoCriar;
    private ImageView iconeNome;
    private ImageView iconeEmail;
    private ImageView iconeSenha;
    private ImageView iconeRepetirSenha;
    private ImageView iconeCheck;

    // Firebase
    private FirebaseAuth mAuth;

    /**
     * Método chamado quando a activity é criada.
     * Inicializa todos os componentes e configurações necessárias.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        inicializarFirebase();
        configurarBarrasSistema();
        inicializarViews();
        configurarCampos();
        configurarBotoes();
    }

    /**
     * Inicializa a instância do Firebase Authentication.
     */
    private void inicializarFirebase() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Configura as cores e comportamento das barras de sistema (status e navegação).
     */
    private void configurarBarrasSistema() {
        changeStatusBarColor();
        changeNavigationBarColor();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Inicializa todas as views (campos de texto, botões, ícones) da interface.
     */
    private void inicializarViews() {
        // Campos de entrada
        campoNomeCompleto = findViewById(R.id.campo_nome_completo);
        campoEmail = findViewById(R.id.campo_email);
        campoSenha = findViewById(R.id.campo_senha);
        campoRepetirSenha = findViewById(R.id.campo_repetir_senha);
        mostrarSenha = findViewById(R.id.mostrar_senha);
        iconeCheck = findViewById(R.id.icone_check_senha);
        
        // Elementos UI
        loadingRegistrar = findViewById(R.id.loading_registrar);
        botaoCriar = findViewById(R.id.botao_criar);
        
        // Ícones
        iconeNome = findViewById(R.id.icone_nome_completo);
        iconeEmail = findViewById(R.id.icone_email);
        iconeSenha = findViewById(R.id.icone_senha);
        iconeRepetirSenha = findViewById(R.id.icone_repetir_senha);
    }

    /**
     * Configura os campos de entrada com suas respectivas validações e comportamentos.
     */
    private void configurarCampos() {
        configurarMostrarSenha();
        configurarValidacaoNome(botaoCriar);
        configurarValidacaoEmail();
        configurarValidacaoSenha();
        configurarEfeitosFocus();
    }

    /**
     * Configura os botões da interface com seus respectivos comportamentos.
     */
    private void configurarBotoes() {
        configurarEfeitoBotao(botaoCriar);
        botaoCriar.setOnClickListener(v -> tentarRegistrar());
    }

    /**
     * Configura o checkbox para mostrar/ocultar a senha e repetir senha.
     */
    private void configurarMostrarSenha() {
        TextView textoMostrarSenha = findViewById(R.id.texto_mostrar_senha);
        
        // Configurar clique no texto
        textoMostrarSenha.setOnClickListener(v -> {
            mostrarSenha.setChecked(!mostrarSenha.isChecked());
        });

        mostrarSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            atualizarVisibilidadeSenha(isChecked);
            atualizarFonteSenha();
            iconeCheck.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * Configura os efeitos visuais de foco para todos os campos de entrada.
     */
    private void configurarEfeitosFocus() {
        MaterialButton corpoNomeCompleto = findViewById(R.id.corpo_nome_completo);
        MaterialButton corpoEmail = findViewById(R.id.corpo_email);
        MaterialButton corpoSenha = findViewById(R.id.corpo_senha);
        MaterialButton corpoRepetirSenha = findViewById(R.id.corpo_repetir_senha);

        int corAzul = Color.parseColor(COR_AZUL);
        int corCinza = Color.parseColor(COR_CINZA);

        configurarEfeitoFocusCampo(campoNomeCompleto, corpoNomeCompleto, iconeNome, corAzul, corCinza);
        configurarEfeitoFocusCampo(campoEmail, corpoEmail, iconeEmail, corAzul, corCinza);
        configurarEfeitoFocusCampo(campoSenha, corpoSenha, iconeSenha, corAzul, corCinza);
        configurarEfeitoFocusCampo(campoRepetirSenha, corpoRepetirSenha, iconeRepetirSenha, corAzul, corCinza);
    }

    /**
     * Configura o efeito visual de foco para um campo específico.
     * @param campo Campo de texto a ser configurado
     * @param corpo Botão material que envolve o campo
     * @param icone Ícone associado ao campo
     * @param corFoco Cor quando o campo está em foco
     * @param corNormal Cor quando o campo está sem foco
     */
    private void configurarEfeitoFocusCampo(EditText campo, MaterialButton corpo, ImageView icone, int corFoco, int corNormal) {
        campo.setOnFocusChangeListener((v, hasFocus) -> {
            int color = hasFocus ? R.color.azul : R.color.branco;
            corpo.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(MainRegistrar.this, color)));
            icone.setColorFilter(hasFocus ? corFoco : corNormal);
        });
    }

    /**
     * Configura o efeito visual de toque para um botão.
     * @param botao Botão a ser configurado
     */
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

    /**
     * Configura a validação do campo nome completo em tempo real.
     * Verifica se contém nome e sobrenome e remove caracteres especiais.
     */
    private void configurarValidacaoNome(MaterialButton botaoCriar) {
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
    }

    /**
     * Configura a validação do campo email em tempo real.
     * Remove espaços e verifica se o formato do email é válido.
     */
    private void configurarValidacaoEmail() {
        campoEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String email = s.toString();
                if (email.contains(" ")) {
                    String emailSemEspaco = email.replace(" ", "");
                    s.replace(0, s.length(), emailSemEspaco);
                    campoEmail.setSelection(emailSemEspaco.length());
                }

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

    /**
     * Configura a validação dos campos de senha e repetir senha em tempo real.
     * Verifica força da senha e se as senhas coincidem.
     */
    private void configurarValidacaoSenha() {
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

    /**
     * Valida todos os campos do formulário antes do registro.
     * @return true se todos os campos são válidos, false caso contrário
     */
    private boolean validarTodosCampos() {
        return validarNomeCompleto() && 
               validarEmail() && 
               validarForcaSenha(campoSenha.getText().toString()) && 
               validarConfirmacaoSenha();
    }

    /**
     * Valida o campo nome completo.
     * @return true se o nome é válido, false caso contrário
     */
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

    /**
     * Valida o campo email.
     * @return true se o email é válido, false caso contrário
     */
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

    /**
     * Valida a força da senha.
     * Verifica comprimento mínimo e presença de números, letras e caracteres especiais.
     * @param senha Senha a ser validada
     * @return true se a senha é forte o suficiente, false caso contrário
     */
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

    /**
     * Valida se a senha de confirmação coincide com a senha original.
     * @return true se as senhas coincidem, false caso contrário
     */
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

    /**
     * Tenta registrar um novo usuário no Firebase.
     * Realiza todas as validações necessárias antes do registro.
     */
    private void tentarRegistrar() {
        if (!validarTodosCampos()) {
            return;
        }

        loadingRegistrar.setVisibility(View.VISIBLE);
        botaoCriar.setEnabled(false);

        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();
        String nome = campoNomeCompleto.getText().toString().trim();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            atualizarPerfilUsuario(user, nome);
                        } else {
                            loadingRegistrar.setVisibility(View.GONE);
                            botaoCriar.setEnabled(true);
                            Toast.makeText(MainRegistrar.this, 
                                "Erro ao criar usuário. Tente novamente.", 
                                Toast.LENGTH_LONG).show();
                        }
                    } else {
                        loadingRegistrar.setVisibility(View.GONE);
                        botaoCriar.setEnabled(true);

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

    /**
     * Atualiza o perfil do usuário após o registro bem-sucedido.
     * @param user Usuário do Firebase recém-criado
     * @param nome Nome completo do usuário para atualizar o perfil
     */
    private void atualizarPerfilUsuario(FirebaseUser user, String nome) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nome)
                .build();

        // Primeiro atualiza o perfil no Authentication
        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Depois cria o documento do usuário no Firestore
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        
                        // Cria um objeto com os dados do usuário
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("nome", nome);
                        userData.put("email", user.getEmail());
                        userData.put("fotoUrl", "");
                        userData.put("cpf", "");
                        userData.put("endereco", "");
                        userData.put("genero", "");
                        userData.put("dataCriacao", new Date());

                        // Salva no Firestore usando o UID como ID do documento
                        db.collection("usuarios")
                            .document(user.getUid())
                            .set(userData)
                            .addOnCompleteListener(firestoreTask -> {
                                loadingRegistrar.setVisibility(View.GONE);
                                botaoCriar.setEnabled(true);

                                if (firestoreTask.isSuccessful()) {
                                    Toast.makeText(MainRegistrar.this, 
                                        "Registro realizado com sucesso!", 
                                        Toast.LENGTH_SHORT).show();
                                    
                                    Intent intent = new Intent(MainRegistrar.this, MainPainel.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Se falhar ao criar no Firestore, faz logout
                                    mAuth.signOut();
                                    Toast.makeText(MainRegistrar.this,
                                        "Erro ao criar perfil. Tente novamente.", 
                                        Toast.LENGTH_SHORT).show();
                                }
                            });
                    } else {
                        loadingRegistrar.setVisibility(View.GONE);
                        botaoCriar.setEnabled(true);
                        Toast.makeText(MainRegistrar.this,
                            "Erro ao atualizar perfil", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Altera a cor da barra de status do sistema.
     */
    private void changeStatusBarColor() {
        getWindow().setStatusBarColor(Color.parseColor(COR_BARRA_SISTEMA));
    }

    /**
     * Altera a cor da barra de navegação do sistema.
     */
    private void changeNavigationBarColor() {
        getWindow().setNavigationBarColor(Color.parseColor(COR_BARRA_SISTEMA));
    }

    private void atualizarVisibilidadeSenha(boolean mostrar) {
        int inputType = mostrar ? 
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
            InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;

        campoSenha.setInputType(inputType);
        campoRepetirSenha.setInputType(inputType);
        
        android.graphics.Typeface interFont = ResourcesCompat.getFont(MainRegistrar.this, R.font.fonte_inter);
        campoSenha.setTypeface(interFont);
        campoRepetirSenha.setTypeface(interFont);
        
        campoSenha.setSelection(campoSenha.getText().length());
        campoRepetirSenha.setSelection(campoRepetirSenha.getText().length());
    }

    private void atualizarFonteSenha() {
        android.graphics.Typeface interFont = ResourcesCompat.getFont(MainRegistrar.this, R.font.fonte_inter);
        campoSenha.setTypeface(interFont);
        campoRepetirSenha.setTypeface(interFont);
    }
}
