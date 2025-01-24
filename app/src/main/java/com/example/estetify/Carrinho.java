package com.example.estetify;

/**
 * Classe modelo que representa um item no carrinho de compras.
 * Esta classe é utilizada para mapear os dados de itens do carrinho entre o app e o Firestore.
 * Contém informações do item e da loja que o oferece.
 */
public class Carrinho {
    //region Campos
    /** ID único do item */
    private String id;
    
    /** Título/nome do item */
    private String titulo;
    
    /** Descrição detalhada do item */
    private String descricao;
    
    /** Preço do item */
    private double preco;
    
    /** URL da foto do item */
    private String fotoUrl;
    
    /** Tipo do item (ex: produto, serviço) */
    private String tipo;
    
    /** ID da loja que oferece o item */
    private String lojaId;
    
    /** Nome da loja que oferece o item */
    private String lojaNome;
    //endregion

    //region Construtores
    /**
     * Construtor vazio necessário para o Firestore
     */
    public Carrinho() {}

    /**
     * Cria um novo item no carrinho a partir de um Item e o nome da loja
     * @param item Item a ser adicionado ao carrinho
     * @param lojaNome Nome da loja que oferece o item
     */
    public Carrinho(Item item, String lojaNome) {
        this.id = item.getId();
        this.titulo = item.getTitulo();
        this.descricao = item.getDescricao();
        this.preco = item.getPreco();
        this.fotoUrl = item.getFotoUrl();
        this.tipo = item.getTipo();
        this.lojaId = item.getLojaId();
        this.lojaNome = lojaNome;
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

    public double getPreco() { 
        return preco; 
    }

    public void setPreco(double preco) { 
        this.preco = preco; 
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

    public String getLojaId() { 
        return lojaId; 
    }

    public void setLojaId(String lojaId) { 
        this.lojaId = lojaId; 
    }

    public String getLojaNome() { 
        return lojaNome; 
    }

    public void setLojaNome(String lojaNome) { 
        this.lojaNome = lojaNome; 
    }
    //endregion
}
