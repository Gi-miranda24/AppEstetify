package com.example.estetify;

import java.util.Date;
import java.util.List;

/**
 * Classe modelo que representa um pedido no sistema.
 * Esta classe é utilizada para mapear os dados de pedidos entre o app e o Firestore.
 * Um pedido contém informações sobre os itens comprados, valor total e status.
 */
public class Pedido {
    //region Campos
    /** ID do usuário que fez o pedido */
    private String usuarioId;
    
    /** Data em que o pedido foi realizado */
    private Date data;
    
    /** Lista de itens incluídos no pedido */
    private List<Carrinho> itens;
    
    /** Valor total do pedido */
    private double valorTotal;
    
    /** Status atual do pedido (ex: pendente, concluído, etc) */
    private String status;
    //endregion

    //region Construtores
    /**
     * Construtor vazio necessário para o Firestore
     */
    public Pedido() {}

    /**
     * Cria um novo pedido com os dados especificados
     * @param usuarioId ID do usuário que fez o pedido
     * @param data Data do pedido
     * @param itens Lista de itens do pedido
     * @param valorTotal Valor total do pedido
     * @param status Status inicial do pedido
     */
    public Pedido(String usuarioId, Date data, List<Carrinho> itens, double valorTotal, String status) {
        this.usuarioId = usuarioId;
        this.data = data;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.status = status;
    }
    //endregion

    //region Getters & Setters
    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public List<Carrinho> getItens() {
        return itens;
    }

    public void setItens(List<Carrinho> itens) {
        this.itens = itens;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    //endregion
}
