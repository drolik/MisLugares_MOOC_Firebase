package com.example.mislugares;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class UserDetailsFragment extends Fragment{
    private View vista;

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup contenedor,
                             Bundle savedInstanceState) {
        vista = inflador.inflate(R.layout.user_detail,contenedor,false);
        setHasOptionsMenu(false);



        // Nombre de usuario
         SharedPreferences pref = getActivity().getSharedPreferences(
                "com.example.mislugares_internal", this.getContext().MODE_PRIVATE);
        String name = pref.getString("name", "Nombre desconocido");

        TextView nombre = (TextView) vista.findViewById(R.id.user_name);
        nombre.setText(name);

        return vista;
    }

}