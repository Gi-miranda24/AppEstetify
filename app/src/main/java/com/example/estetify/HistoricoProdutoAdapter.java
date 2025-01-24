package com.example.estetify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Adapter para exibir itens do histórico de compras em um RecyclerView.
 */
public class HistoricoProdutoAdapter extends RecyclerView.Adapter<HistoricoProdutoAdapter.ViewHolder> {
    private List<Carrinho> itens;

    public HistoricoProdutoAdapter(List<Carrinho> itens) {
        this.itens = itens;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_historico_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Carrinho item = itens.get(position);

        // Carregar imagem
        if (item.getFotoUrl() != null && !item.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getFotoUrl())
                    .into(holder.itemImage);
        }

        // Configurar textos
        holder.itemTitle.setText(item.getTitulo());
        holder.storeName.setText(item.getLojaNome());
        holder.itemPrice.setText(String.format("R$ %.2f", item.getPreco()));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    /**
     * ViewHolder para os itens do histórico
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView storeName;
        TextView itemPrice;

        ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            storeName = itemView.findViewById(R.id.store_name);
            itemPrice = itemView.findViewById(R.id.item_price);
        }
    }
}
