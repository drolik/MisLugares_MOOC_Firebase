package com.example.mislugares;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private static DatabaseReference referenciaLugares;
    private DatabaseReference referenciaUsuarios;

    // Firebase storage
    private FirebaseStorage storage;
    private static StorageReference storageRef;
   // private static DatabaseReference misLugaresReference;

    // Configuración remota
    static FirebaseRemoteConfig mFirebaseRemoteConfig;
    static long distancia;

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

        // Firebase storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://mis-lugares-firebase-b72fa.appspot.com/");

        // Configuración remota
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings =
                new FirebaseRemoteConfigSettings
                        .Builder()
                        .setDeveloperModeEnabled(BuildConfig.DEBUG)
                        .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);

        long cacheExpiration = 3600;
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();

                        getDistancia();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        distancia=mFirebaseRemoteConfig.getLong("distancia");
                    }
                });

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

    public static DatabaseReference getReferenciaLugares() {
        return referenciaLugares;
    }

    public DatabaseReference getReferenciaUsuarios () {
        return referenciaUsuarios;
    }

    public static StorageReference getStorageReference() {
        return storageRef;
    }

    static void mostrarDialogo(final Context context, final String mensaje) {
        Intent intent = new Intent(context, Dialogo.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mensaje" , mensaje);
        context.startActivity(intent);
    }

    private void getDistancia() {
        distancia = mFirebaseRemoteConfig.getLong("distancia");
    }


}