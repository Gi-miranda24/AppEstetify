package com.example.estetify;

// Imports do Android
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

// Imports do AndroidX
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

// Imports de Bibliotecas
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;

// Imports do Java
import java.util.List;

/**
 * Adapter para exibir lojas em um RecyclerView.
 * Cada loja exibe sua foto de perfil, nome, endereço, bio e avaliação.
 * Ao clicar em uma loja, abre o LojaFragment com os detalhes completos.
 */
public class LojaAdapter extends RecyclerView.Adapter<LojaAdapter.ViewHolder> {
    //region Campos
    /** Lista de lojas a serem exibidas */
    private List<Loja> lojas;
    
    /** FragmentManager para navegação entre fragments */
    private FragmentManager fragmentManager;
    //endregion

    //region Construtor
    /**
     * Cria um novo adapter para lojas
     * @param lojas Lista de lojas a serem exibidas
     * @param fragmentManager FragmentManager para navegação
     */
    public LojaAdapter(List<Loja> lojas, FragmentManager fragmentManager) {
        this.lojas = lojas;
        this.fragmentManager = fragmentManager;
    }
    //endregion

    //region Métodos do Adapter
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.card_loja, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Loja loja = lojas.get(position);
        
        // Carregar imagem
        if (loja.getFotoUrl() != null && !loja.getFotoUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                .load(loja.getFotoUrl())
                .into(holder.storeImage);
        }

        // Configurar textos e avaliação
        holder.storeName.setText(loja.getNome());
        holder.storeAddress.setText(loja.getEndereco());
        holder.storeBio.setText(loja.getBio());
        holder.storeRating.setRating((float) loja.getAvaliacao());

        // Configurar clique
        holder.itemView.setOnClickListener(v -> {
            LojaFragment lojaFragment = LojaFragment.newInstance(loja.getId());
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, lojaFragment)
                .addToBackStack(null)
                .commit();
        });
    }

    @Override
    public int getItemCount() {
        return lojas.size();
    }

    /**
     * Atualiza a lista de lojas e notifica o adapter
     * @param newLojas Nova lista de lojas
     */
    public void updateLojas(List<Loja> newLojas) {
        this.lojas = newLojas;
        notifyDataSetChanged();
    }
    //endregion

    //region ViewHolder
    /**
     * ViewHolder para as lojas
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView storeImage;
        TextView storeName;
        TextView storeAddress;
        TextView storeBio;
        RatingBar storeRating;

        ViewHolder(View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.store_image);
            storeName = itemView.findViewById(R.id.store_name);
            storeAddress = itemView.findViewById(R.id.store_address);
            storeBio = itemView.findViewById(R.id.store_bio);
            storeRating = itemView.findViewById(R.id.store_rating);
        }
    }
    //endregion
}
