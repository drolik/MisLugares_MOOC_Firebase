package com.example.mislugares;

import android.app.Application;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by drolik on 18/12/16.
 */

public class Aplicacion extends Application {

    // Variables Volley
    private static RequestQueue colaPeticiones;
    private static ImageLoader lectorImagenes;

    // Variables Base de datos
    private final static String NODO_LUGARES  = "lugares";
    private final static String NODO_USUARIOS = "usuarios";
    private DatabaseReference referenciaLugares;
    private DatabaseReference referenciaUsuarios;

    @Override
    public void onCreate() {
        super.onCreate();

        // Volley
        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache =
                            new LruCache<String, Bitmap>(10);

                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }

                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }
                });

        // Bases de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);
        referenciaLugares  = database.getReference().child(NODO_LUGARES);
        referenciaUsuarios = database.getReference().child(NODO_USUARIOS);

    }

    public static RequestQueue getColaPeticiones() {
        return colaPeticiones;
    }

    public static void setColaPeticiones(RequestQueue colaPeticiones) {
        Aplicacion.colaPeticiones = colaPeticiones;
    }

    public static ImageLoader getLectorImagenes() {
        return lectorImagenes;
    }

    public static void setLectorImagenes(ImageLoader lectorImagenes) {
        Aplicacion.lectorImagenes = lectorImagenes;
    }

    public DatabaseReference getReferenciaLugares() {
        return referenciaLugares;
    }

    public DatabaseReference getReferenciaUsuarios () {
        return referenciaUsuarios;
    }
}