package com.example.estetify;

// Imports do Android
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Imports do AndroidX
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports do Java
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter para exibir pedidos no histórico de compras em um RecyclerView.
 * Cada pedido exibe sua data, status, valor total e lista de itens.
 */
public class HistoricoAdapter extends RecyclerView.Adapter<HistoricoAdapter.ViewHolder> {
    //region Campos
    /** Lista de pedidos a serem exibidos */
    private List<Pedido> pedidos;
    
    /** Formatador de data no padrão brasileiro */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
    //endregion

    //region Construtor
    /**
     * Cria um novo adapter para o histórico de pedidos
     * @param pedidos Lista de pedidos a serem exibidos
     */
    public HistoricoAdapter(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
    //endregion

    //region Métodos do Adapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_historico, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pedido pedido = pedidos.get(position);
        
        // Configurar data e status
        holder.dataPedido.setText(dateFormat.format(pedido.getData()));
        holder.statusPedido.setText(pedido.getStatus());
        holder.valorTotal.setText(String.format("Total: R$ %.2f", pedido.getValorTotal()));

        // Configurar RecyclerView de itens
        HistoricoProdutoAdapter itemAdapter = new HistoricoProdutoAdapter(pedido.getItens());
        holder.recyclerItens.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.recyclerItens.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
    //endregion

    //region ViewHolder
    /**
     * ViewHolder para os pedidos do histórico
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataPedido;
        TextView statusPedido;
        TextView valorTotal;
        RecyclerView recyclerItens;

        ViewHolder(View itemView) {
            super(itemView);
            dataPedido = itemView.findViewById(R.id.data_pedido);
            statusPedido = itemView.findViewById(R.id.status_pedido);
            valorTotal = itemView.findViewById(R.id.valor_total);
            recyclerItens = itemView.findViewById(R.id.recycler_itens);
        }
    }
    //endregion
}
