package com.example.mislugares;

import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by drolik on 17/09/17.
 */

public class AdaptadorLugaresFirebase extends
        FirebaseRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> {

    protected View.OnClickListener onClickListener;

    public AdaptadorLugaresFirebase() {
        super(Lugar.class, R.layout.elemento_lista,
                AdaptadorLugares.ViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("lugares"));
    }

    @Override public void populateViewHolder(final AdaptadorLugares
            .ViewHolder holder, Lugar lugar,  int posicion) {
        AdaptadorLugares.personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }
}