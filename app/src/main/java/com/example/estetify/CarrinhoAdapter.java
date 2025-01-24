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

/**
 * Adapter para exibir itens do carrinho em um RecyclerView.
 * Gerencia a exibição dos itens e a interação com o botão de remover.
 */
public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder> {
    private List<Carrinho> itens;
    private OnItemRemovidoListener listener;

    public interface OnItemRemovidoListener {
        void onItemRemovido(Carrinho item, int position);
    }

    public CarrinhoAdapter(List<Carrinho> itens, OnItemRemovidoListener listener) {
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

    public void updateItens(List<Carrinho> novosItens) {
        this.itens = novosItens;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder para os itens do carrinho
     */
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
