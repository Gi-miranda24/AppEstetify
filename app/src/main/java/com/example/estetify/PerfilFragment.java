package com.example.estetify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.SetOptions;
import de.hdodenhof.circleimageview.CircleImageView;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.Map;
import android.text.InputType;
import android.text.InputFilter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class PerfilFragment extends Fragment {
    private TextView nomeUsuario;
    private TextView emailUsuario;
    private TextView cpfUsuario;
    private TextView generoUsuario;
    private TextView enderecoUsuario;
    private CircleImageView fotoPerfil;
    private MaterialButton botaoAlterarSenha;
    private MaterialButton botaoHistorico;
    private MaterialButton botaoExcluirConta;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar loadingPerfil;
    
    private final ActivityResultLauncher<String> pegarImagem = registerForActivityResult(
        new ActivityResultContracts.GetContent(),
        this::atualizarFotoPerfil
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        loadingPerfil = view.findViewById(R.id.loading_perfil);
        
        // Mostrar loading
        loadingPerfil.setVisibility(View.VISIBLE);
        
        // Inicializar views
        inicializarViews(view);
        
        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar informações do usuário
        setupUserInfo();

        // Configurar cliques dos botões
        configurarBotoes();

        // Simular carregamento
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingPerfil.setVisibility(View.GONE);
        }, 1000);

        return view;
    }

    private void inicializarViews(View view) {
        nomeUsuario = view.findViewById(R.id.nome_usuario);
        emailUsuario = view.findViewById(R.id.email_usuario);
        cpfUsuario = view.findViewById(R.id.cpf_usuario);
        generoUsuario = view.findViewById(R.id.genero_usuario);
        enderecoUsuario = view.findViewById(R.id.endereco_usuario);
        fotoPerfil = view.findViewById(R.id.foto_perfil);
        botaoAlterarSenha = view.findViewById(R.id.botao_alterar_senha);
        botaoHistorico = view.findViewById(R.id.botao_historico);
        botaoExcluirConta = view.findViewById(R.id.botao_excluir_conta);

        // Botões de edição
        view.findViewById(R.id.botao_editar_nome).setOnClickListener(v -> mostrarDialogoEditarNome());
        view.findViewById(R.id.botao_editar_cpf).setOnClickListener(v -> mostrarDialogoEditarCPF());
        view.findViewById(R.id.botao_editar_genero).setOnClickListener(v -> mostrarDialogoEditarGenero());
        view.findViewById(R.id.botao_editar_endereco).setOnClickListener(v -> mostrarDialogoEditarEndereco());
        view.findViewById(R.id.botao_editar_foto).setOnClickListener(v -> mostrarOpcoesEditarFoto());
        view.findViewById(R.id.botao_excluir_conta).setOnClickListener(v -> excluirConta());
    }

    private void configurarBotoes() {
        botaoAlterarSenha.setOnClickListener(v -> mostrarDialogoAlterarSenha());
        botaoHistorico.setOnClickListener(v -> {
            Toast.makeText(requireContext(),
                "Histórico em desenvolvimento",
                Toast.LENGTH_SHORT).show();
        });
    }

    private void setupUserInfo() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            nomeUsuario.setText(firebaseUser.getDisplayName());
            emailUsuario.setText(firebaseUser.getEmail());
            
            // Carregar dados adicionais do Firestore
            db.collection("usuarios")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String cpf = document.getString("cpf");
                        String genero = document.getString("genero");
                        String endereco = document.getString("endereco");
                        
                        cpfUsuario.setText(cpf != null ? cpf : "Não informado");
                        generoUsuario.setText(genero != null ? genero : "Não informado");
                        enderecoUsuario.setText(endereco != null ? endereco : "Não informado");
                    }
                });
            
            carregarFotoPerfil(firebaseUser.getUid());
        }
    }

    private void carregarFotoPerfil(String userId) {
        db.collection("usuarios")
            .document(userId)
            .get()
            .addOnSuccessListener(document -> {
                if (document.exists() && document.contains("fotoUrl")) {
                    String fotoUrl = document.getString("fotoUrl");
                    if (fotoUrl != null && !fotoUrl.isEmpty()) {
                        Glide.with(this)
                            .load(fotoUrl)
                            .placeholder(R.drawable.icone_conta)
                            .into(fotoPerfil);
                    }
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(),
                    "Erro ao carregar foto de perfil",
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void mostrarDialogoEditarNome() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_campo, null);
        EditText campoNome = dialogView.findViewById(R.id.campo_texto);
        campoNome.setHint("Nome completo");
        campoNome.setText(nomeUsuario.getText());
        campoNome.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Nome")
            .setView(dialogView)
            .setPositiveButton("Salvar", (dialog, which) -> {
                String novoNome = campoNome.getText().toString();
                if (!novoNome.isEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(novoNome)
                            .build();

                        user.updateProfile(profileUpdates)
                            .addOnSuccessListener(aVoid -> {
                                nomeUsuario.setText(novoNome);
                                Toast.makeText(requireContext(),
                                    "Nome atualizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                Toast.makeText(requireContext(),
                                    "Erro ao atualizar nome",
                                    Toast.LENGTH_SHORT).show());
                    }
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void mostrarDialogoEditarCPF() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_campo, null);
        EditText campoCPF = dialogView.findViewById(R.id.campo_texto);
        campoCPF.setHint("CPF (apenas números)");
        campoCPF.setText(cpfUsuario.getText().toString().replaceAll("[^0-9]", ""));
        campoCPF.setInputType(InputType.TYPE_CLASS_NUMBER);
        campoCPF.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar CPF")
            .setView(dialogView)
            .setPositiveButton("Salvar", (dialog, which) -> {
                String novoCPF = campoCPF.getText().toString();
                if (novoCPF.length() == 11) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("cpf", novoCPF);

                        db.collection("usuarios")
                            .document(user.getUid())
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                String cpfFormatado = formatarCPF(novoCPF);
                                cpfUsuario.setText(cpfFormatado);
                                Toast.makeText(requireContext(),
                                    "CPF atualizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                Toast.makeText(requireContext(),
                                    "Erro ao atualizar CPF",
                                    Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(requireContext(),
                        "CPF inválido",
                        Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private String formatarCPF(String cpf) {
        return cpf.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private void mostrarDialogoEditarGenero() {
        String[] generos = getResources().getStringArray(R.array.generos);
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Selecionar Gênero")
            .setItems(generos, (dialog, which) -> {
                String novoGenero = generos[which];
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("genero", novoGenero);
                    db.collection("usuarios")
                        .document(user.getUid())
                        .set(userData, SetOptions.merge())
                        .addOnSuccessListener(aVoid -> {
                            generoUsuario.setText(novoGenero);
                            Toast.makeText(requireContext(),
                                "Gênero atualizado com sucesso!",
                                Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e ->
                            Toast.makeText(requireContext(),
                                "Erro ao atualizar gênero",
                                Toast.LENGTH_SHORT).show());
                }
            })
            .show();
    }

    private void mostrarDialogoEditarEndereco() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_editar_campo, null);
        EditText campoEndereco = dialogView.findViewById(R.id.campo_texto);
        campoEndereco.setHint("Endereço completo");
        campoEndereco.setText(enderecoUsuario.getText());
        campoEndereco.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS);

        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Editar Endereço")
            .setView(dialogView)
            .setPositiveButton("Salvar", (dialog, which) -> {
                String novoEndereco = campoEndereco.getText().toString();
                if (!novoEndereco.isEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("endereco", novoEndereco);

                        db.collection("usuarios")
                            .document(user.getUid())
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                enderecoUsuario.setText(novoEndereco);
                                Toast.makeText(requireContext(),
                                    "Endereço atualizado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                Toast.makeText(requireContext(),
                                    "Erro ao atualizar endereço",
                                    Toast.LENGTH_SHORT).show());
                    }
                }
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private void mostrarOpcoesEditarFoto() {
        String[] opcoes = {"Mudar foto de perfil", "Remover foto"};
        
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Foto de perfil")
            .setItems(opcoes, (dialog, which) -> {
                if (which == 0) {
                    // Mudar foto
                    pegarImagem.launch("image/*");
                } else {
                    // Remover foto
                    removerFotoPerfil();
                }
            })
            .show();
    }

    private void removerFotoPerfil() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("fotoUrl", "");
            
            db.collection("usuarios")
                .document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    fotoPerfil.setImageResource(R.drawable.icone_conta);
                    Toast.makeText(requireContext(),
                        "Foto de perfil removida com sucesso!",
                        Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(),
                        "Erro ao remover foto de perfil",
                        Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void mostrarDialogoAlterarSenha() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Alterar Senha")
                .setMessage("Enviaremos um e-mail para " + user.getEmail() + 
                          " com instruções para alterar sua senha.")
                .setPositiveButton("Enviar", (dialog, which) -> {
                    mAuth.sendPasswordResetEmail(user.getEmail())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(),
                                    "E-mail enviado com sucesso!",
                                    Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(),
                                    "Erro ao enviar e-mail",
                                    Toast.LENGTH_SHORT).show();
                            }
                        });
                })
                .setNegativeButton("Cancelar", null)
                .show();
        }
    }

    private void excluirConta() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim", (dialog, which) -> {
                    // Primeiro excluir dados do Firestore
                    db.collection("usuarios").document(user.getUid())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            // Verificar se é uma conta do Google
                            if (user.getProviderData().stream()
                                    .anyMatch(userInfo -> userInfo.getProviderId().equals("google.com"))) {
                                // Configurar Google Sign In
                                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestIdToken(getString(R.string.default_web_client_id))
                                    .requestEmail()
                                    .build();
                                
                                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
                                
                                // Revogar acesso do Google e depois excluir conta do Firebase
                                googleSignInClient.revokeAccess()
                                    .addOnCompleteListener(task -> excluirContaFirebase(user));
                            } else {
                                // Se não é conta do Google, excluir direto do Firebase
                                excluirContaFirebase(user);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(),
                                "Erro ao excluir dados: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        });
                })
                .setNegativeButton("Não", null)
                .show();
        }
    }

    private void excluirContaFirebase(FirebaseUser user) {
        user.delete()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(requireContext(),
                    "Conta excluída com sucesso",
                    Toast.LENGTH_SHORT).show();
                // Voltar para a tela inicial
                Intent intent = new Intent(requireContext(), MainInicio.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(requireContext(),
                    "Erro ao excluir conta: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void atualizarFotoPerfil(Uri imageUri) {
        if (imageUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // Salvar a URL da imagem no Firestore
                Map<String, Object> userData = new HashMap<>();
                userData.put("fotoUrl", imageUri.toString());
                
                db.collection("usuarios")
                    .document(user.getUid())
                    .set(userData, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        // Atualizar a imagem na interface
                        Glide.with(this)
                            .load(imageUri)
                            .into(fotoPerfil);
                        Toast.makeText(requireContext(),
                            "Foto de perfil atualizada com sucesso!",
                            Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(),
                            "Erro ao atualizar foto de perfil",
                            Toast.LENGTH_SHORT).show();
                    });
            }
        }
    }
}
