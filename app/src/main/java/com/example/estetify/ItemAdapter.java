package com.example.estetify;

// Imports do Android
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// Imports do AndroidX
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports de Bibliotecas
import com.bumptech.glide.Glide;

// Imports do Java
import java.util.List;

/**
 * Adapter para exibir itens/serviços em um RecyclerView.
 * Cada item exibe sua imagem, título, descrição e preço.
 * Ao clicar em um item, abre o ItemFragment com os detalhes completos.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    //region Campos
    /** Lista de itens a serem exibidos */
    private List<Item> items;
    
    /** FragmentManager para navegação entre fragments */
    private FragmentManager fragmentManager;
    //endregion

    //region Construtor
    /**
     * Cria um novo adapter para itens
     * @param items Lista de itens a serem exibidos
     * @param fragmentManager FragmentManager para navegação
     */
    public ItemAdapter(List<Item> items, FragmentManager fragmentManager) {
        this.items = items;
        this.fragmentManager = fragmentManager;
    }
    //endregion

    //region Métodos do Adapter
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

    /**
     * Atualiza a lista de itens e notifica o adapter
     * @param newItems Nova lista de itens
     */
    public void updateItems(List<Item> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }
    //endregion

    //region ViewHolder
    /**
     * ViewHolder para os itens
     */
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
    //endregion
}
