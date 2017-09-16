package com.example.mislugares;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


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

        Button cerrarSesion =(Button) vista.findViewById(R.id.btnSignout);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getActivity())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SharedPreferences pref = getActivity().getSharedPreferences(
                                        "com.example.audiolibros_internal", getActivity().MODE_PRIVATE);
                                pref.edit().remove("provider").commit();
                                pref.edit().remove("email").commit();
                                pref.edit().remove("name").commit();
                                Intent i = new Intent(getActivity(),LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        });

            }
        });
        return vista;
    }

}