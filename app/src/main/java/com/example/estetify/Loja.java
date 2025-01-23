package com.example.estetify;

public class Loja {
    private String id;
    private String nome;
    private String bio;
    private String endereco;
    private String fotoUrl;
    private double avaliacao;

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
}
