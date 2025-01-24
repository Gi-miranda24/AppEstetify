package com.example.estetify;

import java.io.Serializable;

public class Item implements Serializable {
    private String id;
    private String titulo;
    private String descricao;
    private String fotoUrl;
    private String tipo;
    private double preco;
    private String lojaId;

    public Item() {} // Construtor vazio necessário para o Firestore

    public Item(String id, String titulo, String descricao, String fotoUrl, String tipo, double preco, String lojaId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.fotoUrl = fotoUrl;
        this.tipo = tipo;
        this.preco = preco;
        this.lojaId = lojaId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public String getFotoUrl() { return fotoUrl; }
    public String getTipo() { return tipo; }
    public double getPreco() { return preco; }
    public String getLojaId() { return lojaId; }

    // Setters necessários para o Firestore
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setLojaId(String lojaId) { this.lojaId = lojaId; }
}
