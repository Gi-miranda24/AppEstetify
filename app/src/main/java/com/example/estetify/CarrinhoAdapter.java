package com.example.estetify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder> {
    private List<CarrinhoItem> itens;
    private OnItemRemovidoListener listener;

    public interface OnItemRemovidoListener {
        void onItemRemovido(CarrinhoItem item, int position);
    }

    public CarrinhoAdapter(List<CarrinhoItem> itens, OnItemRemovidoListener listener) {
        this.itens = itens;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarrinhoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrinho, parent, false);
        return new CarrinhoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhoViewHolder holder, int position) {
        CarrinhoItem item = itens.get(position);
        
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

        // Configurar botão de remover
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemRemovido(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void updateItens(List<CarrinhoItem> novosItens) {
        this.itens = novosItens;
        notifyDataSetChanged();
    }

    static class CarrinhoViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView storeName;
        TextView itemPrice;
        ImageButton btnRemove;

        CarrinhoViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            storeName = itemView.findViewById(R.id.store_name);
            itemPrice = itemView.findViewById(R.id.item_price);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }
}
