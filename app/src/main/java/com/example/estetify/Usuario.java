package com.example.estetify;

public class Usuario {
    private String nome;
    private String email;
    private String fotoUrl;

    public Usuario() {
        // Construtor vazio necessário para o Firestore
    }

    public Usuario(String nome, String email, String fotoUrl) {
        this.nome = nome;
        this.email = email;
        this.fotoUrl = fotoUrl;
    }

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
}
