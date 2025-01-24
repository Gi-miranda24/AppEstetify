package com.example.estetify;

// Imports Android Core
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
import android.widget.TextView;
import android.widget.Toast;

// Imports AndroidX
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Imports Google e Firebase
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainConectar extends AppCompatActivity {
    // Constantes
    private static final String STATUS_BAR_COLOR = "#2C3E50";
    private static final String NAV_BAR_COLOR = "#2C3E50";
    private static final String ICON_COLOR_BLUE = "#2196F3";
    private static final String ICON_COLOR_WHITE = "#FFFFFF";
    
    // Views - Campos de entrada
    private EditText campoEmail;
    private EditText campoSenha;
    private CheckBox mostrarSenha;
    private ImageView iconeCheck;
    
    // Views - Botões e Loading
    private MaterialButton botaoConectar;
    private ProgressBar loadingConectar;
    
    // Firebase
    private FirebaseAuth mAuth;

    /**
     * Método do ciclo de vida chamado quando a Activity é criada.
     * Inicializa os componentes principais e configura a interface.
     * @param savedInstanceState Estado salvo da activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectar);

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
     * Inicializa todas as referências das views utilizadas na tela.
     */
    private void inicializarViews() {
        // Campos de entrada
        mostrarSenha = findViewById(R.id.mostrar_senha);
        campoSenha = findViewById(R.id.campo_senha);
        campoEmail = findViewById(R.id.campo_email);
        iconeCheck = findViewById(R.id.icone_check_senha);
        
        // Botões e Loading
        botaoConectar = findViewById(R.id.botao_conectar);
        loadingConectar = findViewById(R.id.loading_conectar);
    }

    /**
     * Configura os campos de entrada e seus ícones.
     */
    private void configurarCampos() {
        ImageView iconeEmail = findViewById(R.id.icone_email);
        ImageView iconeSenha = findViewById(R.id.icone_senha);

        configurarCampoEmail(iconeEmail);
        configurarCampoSenha(iconeSenha);
        configurarMostrarSenha();
    }

    /**
     * Configura o campo de email e seu comportamento de foco.
     * @param iconeEmail Ícone associado ao campo de email
     */
    private void configurarCampoEmail(ImageView iconeEmail) {
        campoEmail.setOnFocusChangeListener((v, hasFocus) -> {
            atualizarCorCampo(R.id.corpo_email, hasFocus);
            atualizarCorIcone(iconeEmail, hasFocus);
        });
    }

    /**
     * Configura o campo de senha e seu comportamento de foco.
     * @param iconeSenha Ícone associado ao campo de senha
     */
    private void configurarCampoSenha(ImageView iconeSenha) {
        campoSenha.setOnFocusChangeListener((v, hasFocus) -> {
            atualizarCorCampo(R.id.corpo_senha, hasFocus);
            atualizarCorIcone(iconeSenha, hasFocus);
        });
    }

    /**
     * Configura o checkbox de mostrar/ocultar senha.
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
     * Atualiza a cor do campo de acordo com o estado de foco.
     * @param campoId ID do campo a ser atualizado
     * @param hasFocus Estado de foco do campo
     */
    private void atualizarCorCampo(int campoId, boolean hasFocus) {
        int color = hasFocus ? R.color.azul : R.color.branco;
        MaterialButton corpo = findViewById(campoId);
        corpo.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(this, color)));
    }

    /**
     * Atualiza a cor do ícone de acordo com o estado de foco.
     * @param icone Ícone a ser atualizado
     * @param hasFocus Estado de foco do campo associado
     */
    private void atualizarCorIcone(ImageView icone, boolean hasFocus) {
        int cor = Color.parseColor(hasFocus ? ICON_COLOR_BLUE : ICON_COLOR_WHITE);
        icone.setColorFilter(cor);
    }

    /**
     * Atualiza a visibilidade do texto da senha.
     * @param mostrar true para mostrar a senha, false para ocultá-la
     */
    private void atualizarVisibilidadeSenha(boolean mostrar) {
        int inputType = mostrar ? 
            (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
            (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        campoSenha.setInputType(inputType);
    }

    /**
     * Atualiza a fonte do campo de senha e mantém o cursor no final.
     */
    private void atualizarFonteSenha() {
        android.graphics.Typeface interFont = ResourcesCompat.getFont(this, R.font.fonte_inter);
        campoSenha.setTypeface(interFont);
        campoSenha.setSelection(campoSenha.getText().length());
    }

    /**
     * Configura os botões da interface e seus listeners.
     */
    private void configurarBotoes() {
        configurarBotaoConectar();
        configurarBotaoEsqueceuSenha();
    }

    /**
     * Configura o botão de conectar.
     */
    private void configurarBotaoConectar() {
        configurarEfeitoBotao(botaoConectar);
        botaoConectar.setOnClickListener(v -> tentarLogin());
    }

    /**
     * Configura o botão de "Esqueceu a senha" para mostrar um diálogo de recuperação
     */
    private void configurarBotaoEsqueceuSenha() {
        TextView botaoEsqueceuSenha = findViewById(R.id.botao_esqueceu_senha);
        botaoEsqueceuSenha.setOnClickListener(v -> mostrarDialogoRecuperarSenha());
    }

    /**
     * Mostra um diálogo com campo de email para recuperação de senha
     */
    private void mostrarDialogoRecuperarSenha() {
        // Criar o EditText para o email
        EditText campoEmailRecuperar = new EditText(this);
        campoEmailRecuperar.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        campoEmailRecuperar.setHint("Digite seu e-mail");
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        campoEmailRecuperar.setPadding(padding, padding, padding, padding);

        // Criar e configurar o diálogo
        new MaterialAlertDialogBuilder(this)
            .setTitle("Recuperar Senha")
            .setMessage("Digite seu e-mail para receber o link de recuperação de senha")
            .setView(campoEmailRecuperar)
            .setPositiveButton("Enviar", (dialog, which) -> {
                String email = campoEmailRecuperar.getText().toString().trim();
                if (!email.isEmpty()) {
                    enviarEmailRecuperacao(email);
                } else {
                    Toast.makeText(MainConectar.this, 
                        "Por favor, digite seu e-mail", 
                        Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Não", null)
            .show();
    }

    /**
     * Envia o email de recuperação de senha usando o Firebase
     * @param email Email do usuário
     */
    private void enviarEmailRecuperacao(String email) {
        loadingConectar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                loadingConectar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    Toast.makeText(MainConectar.this,
                        "Email de recuperação enviado com sucesso",
                        Toast.LENGTH_LONG).show();
                } else {
                    String erro = "Erro ao enviar email de recuperação";
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        erro = "Não existe uma conta com este e-mail";
                    }
                    Toast.makeText(MainConectar.this, erro, Toast.LENGTH_LONG).show();
                }
            });
    }

    /**
     * Configura as cores e o padding das barras do sistema.
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
        getWindow().setNavigationBarColor(Color.parseColor(NAV_BAR_COLOR));
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
     * Inicia o processo de tentativa de login.
     * Valida os campos e inicia a autenticação se estiverem corretos.
     */
    private void tentarLogin() {
        String email = campoEmail.getText().toString().trim();
        String senha = campoSenha.getText().toString();

        if (!validarCampos(email, senha)) {
            return;
        }

        iniciarProcessoLogin();
        realizarLogin(email, senha);
    }

    /**
     * Prepara a interface para o processo de login,
     * mostrando o loading e desabilitando o botão.
     */
    private void iniciarProcessoLogin() {
        loadingConectar.setVisibility(View.VISIBLE);
        botaoConectar.setEnabled(false);
    }

    /**
     * Realiza a autenticação no Firebase com email e senha.
     * @param email Email do usuário
     * @param senha Senha do usuário
     */
    private void realizarLogin(String email, String senha) {
        mAuth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    loginSucesso();
                } else {
                    loginFalha(task.getException());
                }
            });
    }

    /**
     * Processa o sucesso do login, exibindo mensagem e navegando para o painel.
     */
    private void loginSucesso() {
        mostrarMensagem("Login realizado com sucesso");
        navegarParaPainel();
    }

    /**
     * Processa a falha do login, finalizando o processo e tratando o erro.
     * @param exception Exceção que causou a falha
     */
    private void loginFalha(Exception exception) {
        finalizarProcessoLogin();
        tratarErroLogin(exception);
    }

    /**
     * Finaliza o processo de login, ocultando o loading
     * e reabilitando o botão.
     */
    private void finalizarProcessoLogin() {
        loadingConectar.setVisibility(View.GONE);
        botaoConectar.setEnabled(true);
    }

    /**
     * Trata os diferentes tipos de erro que podem ocorrer durante o login.
     * @param exception Exceção a ser tratada
     */
    private void tratarErroLogin(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidUserException e) {
            setarErro(campoEmail, "Usuário não encontrado");
        } catch (FirebaseAuthInvalidCredentialsException e) {
            setarErro(campoSenha, "Senha incorreta");
        } catch (Exception e) {
            mostrarMensagem("Erro ao fazer login: " + e.getMessage());
        }
    }

    /**
     * Valida os campos de email e senha.
     * @param email Email a ser validado
     * @param senha Senha a ser validada
     * @return true se os campos são válidos, false caso contrário
     */
    private boolean validarCampos(String email, String senha) {
        if (email.isEmpty()) {
            setarErro(campoEmail, "Digite seu email");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setarErro(campoEmail, "Email inválido");
            return false;
        }

        if (senha.isEmpty()) {
            setarErro(campoSenha, "Digite sua senha");
            return false;
        }

        return true;
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
     * Define uma mensagem de erro para um campo e o coloca em foco.
     * @param campo Campo que receberá o erro
     * @param mensagem Mensagem de erro a ser exibida
     */
    private void setarErro(EditText campo, String mensagem) {
        campo.setError(mensagem);
        campo.requestFocus();
    }

    /**
     * Exibe uma mensagem toast para o usuário.
     * @param mensagem Mensagem a ser exibida
     */
    private void mostrarMensagem(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    /**
     * Navega para o painel principal, limpando a pilha de activities.
     */
    private void navegarParaPainel() {
        Intent intent = new Intent(this, MainPainel.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
