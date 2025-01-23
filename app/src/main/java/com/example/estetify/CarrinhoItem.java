package com.example.estetify;

public class CarrinhoItem {
    private String id;
    private String titulo;
    private String descricao;
    private double preco;
    private String fotoUrl;
    private String tipo;
    private String lojaId;
    private String lojaNome;

    public CarrinhoItem() {
        // Construtor vazio necessário para o Firestore
    }

    public CarrinhoItem(Item item, String lojaNome) {
        this.id = item.getId();
        this.titulo = item.getTitulo();
        this.descricao = item.getDescricao();
        this.preco = item.getPreco();
        this.fotoUrl = item.getFotoUrl();
        this.tipo = item.getTipo();
        this.lojaId = item.getLojaId();
        this.lojaNome = lojaNome;
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getLojaId() { return lojaId; }
    public void setLojaId(String lojaId) { this.lojaId = lojaId; }

    public String getLojaNome() { return lojaNome; }
    public void setLojaNome(String lojaNome) { this.lojaNome = lojaNome; }
}
