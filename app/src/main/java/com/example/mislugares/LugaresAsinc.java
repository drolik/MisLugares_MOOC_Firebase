package com.example.mislugares;

/**
 * Created by drolik on 17/09/17.
 */

public interface LugaresAsinc {
    interface EscuchadorElemento{
        void onRespuesta(Lugar lugar);
    }
    interface EscuchadorTamanyo{
        void onRespuesta(long tamanyo);
    }

    void elemento(String id, EscuchadorElemento escuchador);
    void anyade(Lugar lugar);
    String nuevo();
    void borrar(String id);
    void actualiza(String id, Lugar lugar);
    void tamanyo(EscuchadorTamanyo escuchador);
}