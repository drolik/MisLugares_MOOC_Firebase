package com.example.mislugares;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.mislugares.Aplicacion.mostrarDialogo;

/**
 * Created by drolik on 23/02/17.
 */

public class MisLugaresFCMService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            String lugar="";
            lugar = "Se ha añadido o editado algún lugar" + "\n";
            lugar =lugar + "Lugar: "+remoteMessage.getData().get("nombre")+ "\n";
            lugar = lugar + "Comentario: "+ remoteMessage.getData().get("comentario")+ "\n";
            lugar = lugar +"Url: "+remoteMessage.getData().get("url")+ "\n";
            mostrarDialogo(getApplicationContext(), lugar);
        } else {
            if (remoteMessage.getNotification() != null) {
                mostrarDialogo(getApplicationContext(),
                        remoteMessage.getNotification().getBody());
            }
        }
    }
}