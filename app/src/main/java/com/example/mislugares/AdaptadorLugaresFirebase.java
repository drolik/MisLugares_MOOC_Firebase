package com.example.mislugares;

import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by drolik on 17/09/17.
 */

public class AdaptadorLugaresFirebase extends
        FirebaseRecyclerAdapter<Lugar, AdaptadorLugares.ViewHolder> {
    private onFireBaseDataChanged listener;

    protected View.OnClickListener onClickListener;

    public AdaptadorLugaresFirebase( onFireBaseDataChanged listener) {
        super(Lugar.class, R.layout.elemento_lista,
                AdaptadorLugares.ViewHolder.class,
                FirebaseDatabase.getInstance().getReference().child("lugares"));
        this.listener = listener;
    }

    @Override public void populateViewHolder(final AdaptadorLugares
            .ViewHolder holder, Lugar lugar,  int posicion) {
        AdaptadorLugares.personalizaVista(holder, lugar);
        holder.itemView.setOnClickListener(onClickListener);
    }

    public void setOnItemClickListener(View.OnClickListener onClick) {
        onClickListener = onClick;
    }

    @Override
    protected void onDataChanged() {
        super.onDataChanged();
        listener.onFireBaseDataChanged();
    }

    public interface onFireBaseDataChanged{
        void onFireBaseDataChanged();
    }
}