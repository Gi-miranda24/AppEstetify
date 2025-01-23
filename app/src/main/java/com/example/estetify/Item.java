package com.example.estetify;

public class Item {
    private String id;
    private String titulo;
    private String descricao;
    private String fotoUrl;
    private String tipo;
    private double preco;
    private String lojaId;

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
}
