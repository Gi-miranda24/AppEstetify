package com.example.estetify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.List;

public class LojaAdapter extends RecyclerView.Adapter<LojaAdapter.ViewHolder> {
    private List<Loja> lojas;
    private FragmentManager fragmentManager;

    public LojaAdapter(List<Loja> lojas, FragmentManager fragmentManager) {
        this.lojas = lojas;
        this.fragmentManager = fragmentManager;
    }

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

    public void updateLojas(List<Loja> newLojas) {
        this.lojas = newLojas;
        notifyDataSetChanged();
    }

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
}
