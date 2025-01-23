package com.example.estetify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private FragmentManager fragmentManager;

    public ItemAdapter(List<Item> items, FragmentManager fragmentManager) {
        this.items = items;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        
        // Carregar imagem
        if (item.getFotoUrl() != null && !item.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(item.getFotoUrl())
                .into(holder.itemImage);
        }

        // Configurar textos
        holder.itemTitle.setText(item.getTitulo());
        holder.itemDescription.setText(item.getDescricao());
        holder.itemPrice.setText(String.format("R$ %.2f", item.getPreco()));

        // Configurar clique
        holder.itemView.setOnClickListener(v -> {
            ItemFragment itemFragment = ItemFragment.newInstance(item);
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, itemFragment)
                .addToBackStack(null)
                .commit();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemTitle;
        TextView itemDescription;
        TextView itemPrice;

        ViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemTitle = itemView.findViewById(R.id.item_title);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemPrice = itemView.findViewById(R.id.item_price);
        }
    }
}
