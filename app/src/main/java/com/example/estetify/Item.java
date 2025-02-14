package com.example.estetify;

import java.io.Serializable;

/**
 * Classe modelo que representa um item/serviço disponível para compra no sistema.
 * Esta classe é utilizada para mapear os dados de itens entre o app e o Firestore.
 * Implementa Serializable para permitir a passagem do objeto entre Activities/Fragments.
 */
public class Item implements Serializable {
    //region Campos
    /** ID único do item */
    private String id;
    
    /** Título/nome do item */
    private String titulo;
    
    /** Descrição detalhada do item */
    private String descricao;
    
    /** URL da foto do item */
    private String fotoUrl;
    
    /** Tipo do item (ex: produto, serviço) */
    private String tipo;
    
    /** Preço do item */
    private double preco;
    
    /** ID da loja que oferece o item */
    private String lojaId;
    //endregion

    //region Construtores
    /**
     * Construtor vazio necessário para o Firestore
     */
    public Item() {}

    /**
     * Cria um novo item com os dados especificados
     * @param id ID único do item
     * @param titulo Título/nome do item
     * @param descricao Descrição detalhada
     * @param fotoUrl URL da foto
     * @param tipo Tipo do item
     * @param preco Preço do item
     * @param lojaId ID da loja que oferece o item
     */
    public Item(String id, String titulo, String descricao, String fotoUrl, String tipo, double preco, String lojaId) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.fotoUrl = fotoUrl;
        this.tipo = tipo;
        this.preco = preco;
        this.lojaId = lojaId;
    }
    //endregion

    //region Getters & Setters
    public String getId() { 
        return id; 
    }

    public void setId(String id) { 
        this.id = id; 
    }

    public String getTitulo() { 
        return titulo; 
    }

    public void setTitulo(String titulo) { 
        this.titulo = titulo; 
    }

    public String getDescricao() { 
        return descricao; 
    }

    public void setDescricao(String descricao) { 
        this.descricao = descricao; 
    }

    public String getFotoUrl() { 
        return fotoUrl; 
    }

    public void setFotoUrl(String fotoUrl) { 
        this.fotoUrl = fotoUrl; 
    }

    public String getTipo() { 
        return tipo; 
    }

    public void setTipo(String tipo) { 
        this.tipo = tipo; 
    }

    public double getPreco() { 
        return preco; 
    }

    public void setPreco(double preco) { 
        this.preco = preco; 
    }

    public String getLojaId() { 
        return lojaId; 
    }

    public void setLojaId(String lojaId) { 
        this.lojaId = lojaId; 
    }
    //endregion
}
