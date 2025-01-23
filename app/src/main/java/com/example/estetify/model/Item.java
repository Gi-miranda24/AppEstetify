package com.example.estetify.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String id;
    private String fotoUrl;
    private String titulo;
    private String descricao;
    private String tipo;
    private double preco;
    private String lojaId;

    public Item() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }
    public String getLojaId() { return lojaId; }
    public void setLojaId(String lojaId) { this.lojaId = lojaId; }
}
