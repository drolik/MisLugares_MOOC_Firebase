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
        if (remoteMessage.getNotification() != null) {
            mostrarDialogo(getApplicationContext(),
                    remoteMessage.getNotification().getBody());
        }
    }
}