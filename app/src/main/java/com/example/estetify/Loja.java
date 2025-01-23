package com.example.estetify;

import java.io.Serializable;

public class Loja implements Serializable {
    private String id;
    private String nome;
    private String bio;
    private String endereco;
    private String fotoUrl;
    private double avaliacao;

    public Loja() {} // Construtor vazio necessário para o Firestore

    public Loja(String id, String nome, String bio, String endereco, String fotoUrl, double avaliacao) {
        this.id = id;
        this.nome = nome;
        this.bio = bio;
        this.endereco = endereco;
        this.fotoUrl = fotoUrl;
        this.avaliacao = avaliacao;
    }

    // Getters
    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getBio() { return bio; }
    public String getEndereco() { return endereco; }
    public String getFotoUrl() { return fotoUrl; }
    public double getAvaliacao() { return avaliacao; }

    // Setters necessários para o Firestore
    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setBio(String bio) { this.bio = bio; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setAvaliacao(double avaliacao) { this.avaliacao = avaliacao; }
}
