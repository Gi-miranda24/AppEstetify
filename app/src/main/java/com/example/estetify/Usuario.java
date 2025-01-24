package com.example.estetify;

/**
 * Classe modelo que representa um usuário no sistema.
 * Esta classe é utilizada para mapear os dados do usuário entre o app e o Firestore.
 */
public class Usuario {
    //region Campos
    /** Nome completo do usuário */
    private String nome;
    
    /** Email do usuário (usado para autenticação) */
    private String email;
    
    /** URL da foto de perfil do usuário */
    private String fotoUrl;
    //endregion

    //region Construtores
    /**
     * Construtor vazio necessário para o Firestore
     */
    public Usuario() {}

    /**
     * Cria um novo usuário com os dados especificados
     * @param nome Nome completo do usuário
     * @param email Email do usuário
     * @param fotoUrl URL da foto de perfil
     */
    public Usuario(String nome, String email, String fotoUrl) {
        this.nome = nome;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }
    //endregion

    //region Getters & Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
    //endregion
}
